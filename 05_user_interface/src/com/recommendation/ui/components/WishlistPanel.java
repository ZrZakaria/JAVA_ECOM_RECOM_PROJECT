package com.recommendation.ui.components;

import com.recommendation.model.RecommendationResult;
import com.recommendation.ui.UIStyles;
import com.recommendation.ui.core.WishlistManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Panel to display the user's wishlist.
 * Moved to components package.
 */
public class WishlistPanel extends JPanel {

    private JPanel productsContainer;
    private JPanel header;
    private JLabel title;
    private JScrollPane scrollPane;

    public WishlistPanel() {
        setLayout(new BorderLayout());
        setBackground(UIStyles.getBackgroundPrimary());

        header = new JPanel(new BorderLayout());
        header.setBackground(UIStyles.getBackgroundSecondary());
        header.setBorder(new EmptyBorder(12, 20, 12, 20));
        title = UIStyles.createLabel("My Wishlist ❤️", UIStyles.FONT_TITLE, UIStyles.getTextPrimary());
        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        productsContainer = new JPanel();
        productsContainer.setLayout(new BoxLayout(productsContainer, BoxLayout.Y_AXIS));
        productsContainer.setBackground(UIStyles.getBackgroundPrimary());
        productsContainer.setBorder(new EmptyBorder(20, 20, 20, 20));

        scrollPane = new JScrollPane(productsContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(UIStyles.getBackgroundPrimary());
        if (scrollPane.getViewport() != null)
            scrollPane.getViewport().setBackground(UIStyles.getBackgroundPrimary());
        add(scrollPane, BorderLayout.CENTER);

        WishlistManager.getInstance().addListener(this::refreshWishlist);
        UIStyles.addThemeListener(this::refreshTheme);
        refreshWishlist();
    }

    private void refreshTheme() {
        Color bgPrimary = UIStyles.getBackgroundPrimary();
        Color bgSecondary = UIStyles.getBackgroundSecondary();

        setBackground(bgPrimary);
        if (header != null) {
            header.setBackground(bgSecondary);
            UIStyles.applyDeepBackground(header, bgSecondary);
        }
        if (productsContainer != null) {
            productsContainer.setBackground(bgPrimary);
            UIStyles.applyDeepBackground(productsContainer, bgPrimary);
        }
        if (scrollPane != null) {
            scrollPane.setBackground(bgPrimary);
            if (scrollPane.getViewport() != null)
                scrollPane.getViewport().setBackground(bgPrimary);
        }
        if (title != null) {
            title.setForeground(UIStyles.getTextPrimary());
        }

        refreshWishlist();
        repaint();
    }

    private void refreshWishlist() {
        productsContainer.removeAll();
        List<RecommendationResult> wishlist = WishlistManager.getInstance().getAll();
        if (wishlist.isEmpty())
            showEmptyState();
        else {
            for (RecommendationResult r : wishlist) {
                ProductCard card = new ProductCard(r);
                productsContainer.add(card);
                productsContainer.add(Box.createVerticalStrut(15));
            }
        }
        productsContainer.revalidate();
        productsContainer.repaint();
    }

    private void showEmptyState() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(80, 40, 80, 40));
        JLabel il = new JLabel("🤍");
        il.setFont(UIStyles.getEmojiFont(64));
        il.setAlignmentX(0.5f);
        p.add(il);
        p.add(Box.createVerticalStrut(20));
        JLabel tl = UIStyles.createLabel("Your Wishlist is Empty", UIStyles.FONT_HEADING, UIStyles.getTextPrimary());
        tl.setAlignmentX(0.5f);
        p.add(tl);
        productsContainer.add(p);
    }
}
