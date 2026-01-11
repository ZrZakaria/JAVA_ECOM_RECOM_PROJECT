package com.recommendation.ui.components;

import com.recommendation.model.RecommendationResult;
import com.recommendation.ui.UIStyles;
import com.recommendation.ui.core.ComparisonManager;
import com.recommendation.ui.core.ImageCache;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Panel to display selected products side-by-side in a dedicated tab.
 * Moved to components package.
 */
public class ComparisonPanel extends JPanel {

    private JPanel gridContainer;
    private JLabel titleLabel;
    private JPanel headerPanel;

    public ComparisonPanel() {
        setLayout(new BorderLayout());
        setBackground(UIStyles.getBackgroundPrimary());

        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIStyles.getBackgroundSecondary());
        headerPanel.setBorder(new EmptyBorder(12, 20, 12, 20));
        titleLabel = UIStyles.createLabel("Product Comparison ⚖️", UIStyles.FONT_TITLE, UIStyles.getTextPrimary());
        headerPanel.add(titleLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        gridContainer = new JPanel();
        gridContainer.setBackground(UIStyles.getBackgroundPrimary());
        add(gridContainer, BorderLayout.CENTER);

        ComparisonManager.getInstance().addListener(this::refreshComparison);
        UIStyles.addThemeListener(this::refreshTheme);
        refreshComparison();
    }

    private void refreshComparison() {
        gridContainer.removeAll();
        List<RecommendationResult> products = ComparisonManager.getInstance().getSelected();
        if (products.isEmpty())
            showEmptyState();
        else
            renderComparisonGrid(products);
        gridContainer.revalidate();
        gridContainer.repaint();
    }

    private void renderComparisonGrid(List<RecommendationResult> products) {
        gridContainer.setLayout(new GridBagLayout());
        gridContainer.setBackground(UIStyles.getBorderColor());
        gridContainer.setBorder(new EmptyBorder(1, 1, 1, 1));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0;
        gbc.gridy = 0;

        // Label Column
        gbc.gridx = 0;
        gbc.weightx = 0;
        JPanel empty = new JPanel();
        empty.setBackground(UIStyles.getBackgroundSecondary());
        addGridCell(gridContainer, empty, gbc);

        // Product Columns
        gbc.weightx = 1.0;
        for (int i = 0; i < products.size(); i++) {
            RecommendationResult p = products.get(i);
            gbc.gridx = i + 1;
            JPanel cell = new JPanel(new BorderLayout(0, 15));
            cell.setBackground(UIStyles.getBackgroundCard());
            cell.setBorder(new EmptyBorder(25, 20, 25, 20));
            JLabel img = new JLabel("Loading...");
            img.setHorizontalAlignment(0);
            img.setPreferredSize(new Dimension(200, 200));
            ImageCache.loadImage(p.getImageUrl(), p.getLink(), 200, 200, true, icon -> {
                if (icon != null) {
                    img.setText("");
                    img.setIcon(icon);
                } else
                    img.setText("No Image");
                img.revalidate();
                img.repaint();
            });
            cell.add(img, BorderLayout.CENTER);
            JLabel title = UIStyles.createLabel("<html><center>" + truncate(p.getTitle(), 80) + "</center></html>",
                    UIStyles.FONT_SUBHEADING, UIStyles.getTextPrimary());
            title.setHorizontalAlignment(0);
            cell.add(title, BorderLayout.SOUTH);
            addGridCell(gridContainer, cell, gbc);
        }

        addRow(gridContainer, "Price", products, 1,
                p -> new ValueCell(String.format("%.2f €", p.getPrice()), p.getPrice() < 50 ? UIStyles.getSuccess()
                        : (p.getPrice() < 200 ? UIStyles.getWarning() : UIStyles.getError()), true));
        addRow(gridContainer, "Rating", products, 2,
                p -> new ValueCell("★ " + p.getAvgRating(), UIStyles.getWarning(), true));
        addRow(gridContainer, "Reviews", products, 3,
                p -> new ValueCell(p.getReviewCount() + "", UIStyles.getTextSecondary(), false));
        addRow(gridContainer, "Match Score", products, 4,
                p -> new ValueCell(String.format("%.1f%%", p.getScore() * 100), UIStyles.getAccentPrimary(), true));
    }

    private void addRow(JPanel grid, String label, List<RecommendationResult> products, int row, ValueExtractor ex) {
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.BOTH;
        g.gridy = row;
        g.gridx = 0;
        g.weightx = 0;
        JPanel lp = new JPanel(new BorderLayout());
        lp.setBackground(UIStyles.getBackgroundSecondary());
        lp.setBorder(new EmptyBorder(12, 20, 12, 20));
        lp.add(UIStyles.createLabel(label, UIStyles.FONT_SUBHEADING, UIStyles.getTextSecondary()));
        addGridCell(grid, lp, g);
        g.weightx = 1.0;
        for (int i = 0; i < products.size(); i++) {
            g.gridx = i + 1;
            ValueCell vc = ex.extract(products.get(i));
            JPanel p = new JPanel(new GridBagLayout());
            p.setBackground(UIStyles.getBackgroundCard());
            p.setBorder(new EmptyBorder(12, 15, 12, 15));
            p.add(UIStyles.createLabel(vc.text, vc.bold ? new Font("Segoe UI", Font.BOLD, 15) : UIStyles.FONT_BODY,
                    vc.color));
            addGridCell(grid, p, g);
        }
    }

    private void addGridCell(JPanel grid, JComponent c, GridBagConstraints g) {
        g.insets = new Insets(0, 0, 1, 1);
        grid.add(c, g);
    }

    private void showEmptyState() {
        gridContainer.setLayout(new GridBagLayout());
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        JLabel il = new JLabel("⚖️");
        il.setFont(UIStyles.getEmojiFont(64));
        il.setAlignmentX(0.5f);
        p.add(il);
        p.add(Box.createVerticalStrut(20));
        JLabel tl = UIStyles.createLabel("No Products Selected", UIStyles.FONT_HEADING, UIStyles.getTextPrimary());
        tl.setAlignmentX(0.5f);
        p.add(tl);
        gridContainer.add(p);
    }

    private void refreshTheme() {
        Color bgPrimary = UIStyles.getBackgroundPrimary();
        Color bgSecondary = UIStyles.getBackgroundSecondary();

        setBackground(bgPrimary);
        if (headerPanel != null) {
            headerPanel.setBackground(bgSecondary);
            UIStyles.applyDeepBackground(headerPanel, bgSecondary);
        }
        if (gridContainer != null) {
            gridContainer.setBackground(bgPrimary);
            UIStyles.applyDeepBackground(gridContainer, bgPrimary);
        }
        if (titleLabel != null) {
            titleLabel.setForeground(UIStyles.getTextPrimary());
        }

        refreshComparison();
        repaint();
    }

    private String truncate(String t, int m) {
        return (t != null && t.length() > m) ? t.substring(0, m) + "..." : t;
    }

    private interface ValueExtractor {
        ValueCell extract(RecommendationResult p);
    }

    private static class ValueCell {
        String text;
        Color color;
        boolean bold;

        ValueCell(String t, Color c, boolean b) {
            text = t;
            color = c;
            bold = b;
        }
    }
}
