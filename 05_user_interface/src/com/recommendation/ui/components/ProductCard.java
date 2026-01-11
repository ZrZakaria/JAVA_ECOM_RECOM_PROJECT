package com.recommendation.ui.components;

import com.recommendation.model.RecommendationResult;
import com.recommendation.ui.UIStyles;
import com.recommendation.ui.core.ImageCache;
import com.recommendation.ui.core.WishlistManager;
import com.recommendation.ui.core.ComparisonManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Path2D;

/**
 * Improved product card with better visual design.
 * Now includes HeartIcon as an inner class.
 */
public class ProductCard extends JPanel {

    private RecommendationResult result;
    private int totalResults;
    private double maxBudget = Double.MAX_VALUE;

    public ProductCard(RecommendationResult result) {
        this(result, 1, Double.MAX_VALUE);
    }

    public ProductCard(RecommendationResult result, int totalResults, double maxBudget) {
        this.result = result;
        this.totalResults = totalResults;
        this.maxBudget = maxBudget;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(UIStyles.PADDING_MEDIUM, 0));
        setBackground(UIStyles.getBackgroundCard());
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.getBorderColor(), 1),
                new EmptyBorder(UIStyles.PADDING_MEDIUM, UIStyles.PADDING_MEDIUM,
                        UIStyles.PADDING_MEDIUM, UIStyles.PADDING_MEDIUM)));

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UIStyles.getAccentPrimary(), 2),
                        new EmptyBorder(UIStyles.PADDING_MEDIUM - 1, UIStyles.PADDING_MEDIUM - 1,
                                UIStyles.PADDING_MEDIUM - 1, UIStyles.PADDING_MEDIUM - 1)));
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UIStyles.getBorderColor(), 1),
                        new EmptyBorder(UIStyles.PADDING_MEDIUM, UIStyles.PADDING_MEDIUM,
                                UIStyles.PADDING_MEDIUM, UIStyles.PADDING_MEDIUM)));
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.X_AXIS));
        leftPanel.setOpaque(false);

        leftPanel.add(createRankBadge());
        leftPanel.add(Box.createHorizontalStrut(10));

        JLabel imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(160, 160));
        imageLabel.setMinimumSize(new Dimension(160, 160));
        imageLabel.setMaximumSize(new Dimension(160, 160));
        imageLabel.setOpaque(true);
        imageLabel.setBackground(UIStyles.getBackgroundCard());
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setText("...");
        leftPanel.add(imageLabel);

        if (result.getImageUrl() != null && !result.getImageUrl().isEmpty()) {
            ImageCache.loadImage(result.getImageUrl(), result.getLink(), 160, 160, true, icon -> {
                if (icon != null) {
                    imageLabel.setText("");
                    imageLabel.setIcon(icon);
                    revalidate();
                    repaint();
                }
            });
        }
        add(leftPanel, BorderLayout.WEST);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(0, UIStyles.PADDING_SMALL, 0, 0));

        JLabel titleLabel = new JLabel(
                "<html><body style='width: 450px'>" + escapeHtml(truncate(result.getTitle(), 85)) + "</body></html>");
        titleLabel.setFont(UIStyles.FONT_HEADING);
        titleLabel.setForeground(UIStyles.getTextPrimary());
        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(8));

        JLabel categoryLabel = createBadge(result.getCategory().toUpperCase());
        categoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(categoryLabel);
        infoPanel.add(Box.createVerticalStrut(10));

        JPanel ratingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        ratingPanel.setOpaque(false);
        ratingPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel starsLabel = new JLabel(UIStyles.getStarRating(result.getAvgRating()));
        starsLabel.setFont(UIStyles.getEmojiFont(16));
        starsLabel.setForeground(UIStyles.getWarning());
        ratingPanel.add(starsLabel);
        JLabel ratingText = new JLabel(
                String.format("  %.1f  •  %d reviews", result.getAvgRating(), result.getReviewCount()));
        ratingText.setFont(UIStyles.FONT_BODY);
        ratingText.setForeground(UIStyles.getTextSecondary());
        ratingPanel.add(ratingText);
        infoPanel.add(ratingPanel);
        infoPanel.add(Box.createVerticalStrut(10));

        String descText = truncate(result.getDescription(), 140);
        if (!descText.isEmpty()) {
            JLabel descLabel = new JLabel(
                    "<html><body style='width: 450px; color: #9CA3AF'>" + escapeHtml(descText) + "</body></html>");
            descLabel.setFont(UIStyles.FONT_SMALL);
            descLabel.setForeground(UIStyles.getTextMuted());
            infoPanel.add(descLabel);
            infoPanel.add(Box.createVerticalStrut(12));
        }

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bottomPanel.setOpaque(false);
        bottomPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        double price = result.getPrice();
        JLabel priceLabel = new JLabel(String.format("%.2f €", price));
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        if (maxBudget < Double.MAX_VALUE && maxBudget > 0) {
            if (price <= maxBudget)
                priceLabel.setForeground(UIStyles.getSuccess());
            else if (price <= maxBudget * 1.15)
                priceLabel.setForeground(UIStyles.getWarning());
            else
                priceLabel.setForeground(UIStyles.getError());
        } else {
            if (price < 50)
                priceLabel.setForeground(UIStyles.getSuccess());
            else if (price < 200)
                priceLabel.setForeground(UIStyles.getWarning());
            else
                priceLabel.setForeground(UIStyles.getError());
        }
        bottomPanel.add(priceLabel);
        bottomPanel.add(Box.createHorizontalStrut(20));

        JButton viewButton = UIStyles.createPrimaryButton("View on Cdiscount →");
        viewButton.setPreferredSize(new Dimension(190, 40));
        viewButton.addActionListener(e -> openProductLink());
        bottomPanel.add(viewButton);
        bottomPanel.add(Box.createHorizontalStrut(15));

        JToggleButton wishlistBtn = new JToggleButton();
        wishlistBtn.setPreferredSize(new Dimension(40, 40));
        wishlistBtn.setFocusPainted(false);
        wishlistBtn.setBorderPainted(false);
        wishlistBtn.setContentAreaFilled(false);
        wishlistBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        wishlistBtn.setIcon(new HeartIcon(false, 24, UIStyles.getTextSecondary()));
        wishlistBtn.setSelectedIcon(new HeartIcon(true, 24, UIStyles.getError()));
        if (WishlistManager.getInstance().contains(result.getProductId()))
            wishlistBtn.setSelected(true);
        wishlistBtn.addActionListener(e -> {
            if (wishlistBtn.isSelected())
                WishlistManager.getInstance().add(result);
            else
                WishlistManager.getInstance().remove(result.getProductId());
        });
        bottomPanel.add(wishlistBtn);

        JCheckBox compareCheck = new JCheckBox("Compare");
        compareCheck.setOpaque(false);
        compareCheck.setFont(UIStyles.FONT_SMALL);
        compareCheck.setForeground(UIStyles.getTextSecondary());
        compareCheck.setSelected(ComparisonManager.getInstance().contains(result.getProductId()));
        compareCheck.addActionListener(e -> {
            if (compareCheck.isSelected()) {
                if (!ComparisonManager.getInstance().add(result)) {
                    compareCheck.setSelected(false);
                    JOptionPane.showMessageDialog(this, "Limit Reached (" + ComparisonManager.MAX_SELECTION + ")",
                            "Limit Reached", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                ComparisonManager.getInstance().remove(result.getProductId());
            }
        });
        bottomPanel.add(compareCheck);
        infoPanel.add(bottomPanel);
        add(infoPanel, BorderLayout.CENTER);
        add(createScorePanel(), BorderLayout.EAST);

        add(createScorePanel(), BorderLayout.EAST);
    }

    private JPanel createRankBadge() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(60, 160));
        int rank = result.getRank();
        String rankText = "#" + rank;
        Color rankColor = UIStyles.getTextMuted();
        String emoji = "";
        if (rank == 1) {
            rankColor = UIStyles.getGold();
            emoji = "🥇";
        } else if (rank == 2) {
            rankColor = UIStyles.getSilver();
            emoji = "🥈";
        } else if (rank == 3) {
            rankColor = UIStyles.getBronze();
            emoji = "🥉";
        } else if (rank == totalResults && totalResults > 1) {
            rankColor = UIStyles.getError();
            emoji = "⚠️";
        }

        if (!emoji.isEmpty()) {
            JLabel el = new JLabel(emoji);
            el.setFont(UIStyles.getEmojiFont(24));
            el.setAlignmentX(0.5f);
            panel.add(el);
            panel.add(Box.createVerticalStrut(4));
        }
        JLabel rl = new JLabel(rankText);
        rl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        rl.setForeground(rankColor);
        rl.setAlignmentX(0.5f);
        panel.add(rl);
        return panel;
    }

    private JPanel createScorePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(80, 160));
        JLabel sl = new JLabel("Score");
        sl.setFont(UIStyles.FONT_SMALL);
        sl.setForeground(UIStyles.getTextMuted());
        sl.setAlignmentX(0.5f);
        panel.add(sl);
        int sp = (int) (result.getScore() * 100);
        JLabel sv = new JLabel(sp + "%");
        sv.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sv.setAlignmentX(0.5f);
        if (sp >= 70)
            sv.setForeground(UIStyles.getSuccess());
        else if (sp >= 40)
            sv.setForeground(UIStyles.getWarning());
        else
            sv.setForeground(UIStyles.getError());
        panel.add(sv);
        panel.setToolTipText("Similarity: 40% | Rating: 30% | Reviews: 15% | Price: 15%");
        return panel;
    }

    private JLabel createBadge(String text) {
        JLabel badge = new JLabel(" " + text + " ");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badge.setForeground(UIStyles.getAccentPrimary());
        badge.setBackground(new Color(UIStyles.getAccentPrimary().getRed(), UIStyles.getAccentPrimary().getGreen(),
                UIStyles.getAccentPrimary().getBlue(), 30));
        badge.setOpaque(true);
        badge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIStyles.getAccentPrimary(), 1), new EmptyBorder(2, 6, 2, 6)));
        return badge;
    }

    private String truncate(String t, int max) {
        return (t == null || t.length() <= max) ? t : t.substring(0, max) + "...";
    }

    private String escapeHtml(String t) {
        return t.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private void openProductLink() {
        try {
            // Robust URI creation: handles spaces and the pipe character '|' which is
            // common in Cdiscount links
            String link = result.getLink();
            if (link == null || link.isEmpty())
                return;

            // Basic encoding for common illegal characters rejected by Desktop.browse()
            String encodedLink = link.replace(" ", "%20")
                    .replace("|", "%7C")
                    .replace("[", "%5B")
                    .replace("]", "%5D");

            java.net.URI uri = new java.net.URI(encodedLink);
            Desktop.getDesktop().browse(uri);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Link Error: " + ex.getMessage() + "\nLink: " + result.getLink());
        }
    }

    /** Inner class for Heart Icon */
    private static class HeartIcon implements Icon {
        private final boolean filled;
        private final int size;
        private final Color color;

        public HeartIcon(boolean filled, int size, Color color) {
            this.filled = filled;
            this.size = size;
            this.color = color;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            double s = size / 24.0;
            g2.translate(x, y);
            g2.scale(s, s);
            g2.setColor(color);
            Path2D.Double h = new Path2D.Double();
            h.moveTo(12, 21.35);
            h.curveTo(12, 21.35, 2.5, 13, 2.5, 7.5);
            h.curveTo(2.5, 3.5, 5.5, 1.5, 8.5, 1.5);
            h.curveTo(10.5, 1.5, 12, 3.5, 12, 3.5);
            h.curveTo(12, 3.5, 13.5, 1.5, 15.5, 1.5);
            h.curveTo(18.5, 1.5, 21.5, 3.5, 21.5, 7.5);
            h.curveTo(21.5, 13, 12, 21.35, 12, 21.35);
            h.closePath();
            if (filled)
                g2.fill(h);
            else {
                g2.setStroke(new BasicStroke(2.0f));
                g2.draw(h);
            }
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }
    }
}
