package com.recommendation.ui.components;

import com.recommendation.model.RecommendationResult;
import com.recommendation.ui.UIStyles;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.util.List;

/**
 * Improved results panel with refined theming and result persistence.
 * Now includes LoadingSpinner as an inner class.
 */
public class ResultsPanel extends JPanel {

    private JPanel resultsContainer;
    private JScrollPane scrollPane;
    private JLabel statusLabel;
    private JLabel countLabel;
    private int totalResults = 0;

    private List<RecommendationResult> lastResults;
    private String lastError;
    private double currentMinPrice = 0;
    private double currentMaxPrice = Double.MAX_VALUE;

    private JPanel headerPanel;

    public ResultsPanel() {
        setLayout(new BorderLayout());
        setBackground(UIStyles.getBackgroundPrimary());

        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIStyles.getBackgroundSecondary());
        headerPanel.setBorder(new EmptyBorder(12, 20, 12, 20));

        statusLabel = UIStyles.createLabel("Ready to search", UIStyles.FONT_HEADING, UIStyles.getTextPrimary());
        headerPanel.add(statusLabel, BorderLayout.WEST);
        countLabel = UIStyles.createLabel("", UIStyles.FONT_BODY, UIStyles.getTextSecondary());
        headerPanel.add(countLabel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        resultsContainer = new JPanel();
        resultsContainer.setLayout(new BoxLayout(resultsContainer, BoxLayout.Y_AXIS));
        resultsContainer.setBackground(UIStyles.getBackgroundPrimary());
        resultsContainer.setBorder(new EmptyBorder(20, 20, 20, 20));

        showWelcomeMessage();

        scrollPane = new JScrollPane(resultsContainer);
        scrollPane.setBackground(UIStyles.getBackgroundPrimary());
        if (scrollPane.getViewport() != null)
            scrollPane.getViewport().setBackground(UIStyles.getBackgroundPrimary());
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        UIStyles.addThemeListener(this::refreshTheme);
    }

    private void showWelcomeMessage() {
        resultsContainer.removeAll();
        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.setOpaque(false);
        welcomePanel.setBorder(new EmptyBorder(80, 40, 80, 40));

        JLabel iconLabel = new JLabel("🛍️");
        iconLabel.setFont(UIStyles.getEmojiFont(48));
        iconLabel.setAlignmentX(0.5f);
        welcomePanel.add(iconLabel);
        welcomePanel.add(Box.createVerticalStrut(20));

        JLabel titleLabel = UIStyles.createLabel("Find Your Perfect Product", UIStyles.FONT_TITLE,
                UIStyles.getTextPrimary());
        titleLabel.setAlignmentX(0.5f);
        welcomePanel.add(titleLabel);
        welcomePanel.add(Box.createVerticalStrut(10));

        JLabel sub = UIStyles.createLabel("Use the search panel to discover recommended products", UIStyles.FONT_BODY,
                UIStyles.getTextMuted());
        sub.setAlignmentX(0.5f);
        welcomePanel.add(sub);
        welcomePanel.add(Box.createVerticalStrut(30));

        String[] tips = { "💡 Search by keywords", "🏷️ Filter by category", "💰 Set your budget",
                "⭐ Ranked by relevance" };
        for (String tip : tips) {
            JLabel tl = UIStyles.createLabel(tip, UIStyles.FONT_BODY, UIStyles.getTextSecondary());
            tl.setAlignmentX(0.5f);
            welcomePanel.add(tl);
            welcomePanel.add(Box.createVerticalStrut(8));
        }

        resultsContainer.add(welcomePanel);
        resultsContainer.revalidate();
        resultsContainer.repaint();
    }

    public void displayResults(List<RecommendationResult> results, double min, double max) {
        this.lastResults = results;
        this.currentMinPrice = min;
        this.currentMaxPrice = max;
        this.lastError = null;
        resultsContainer.removeAll();
        if (results == null) {
            showWelcomeMessage();
            return;
        }
        if (results.isEmpty()) {
            showNoResults();
            return;
        }

        totalResults = results.size();
        statusLabel.setText("✅ Search complete");
        statusLabel.setForeground(UIStyles.getSuccess());
        countLabel.setText("✓ " + totalResults + " products found");

        resultsContainer.add(createSummaryPanel(results));
        resultsContainer.add(Box.createVerticalStrut(UIStyles.PADDING_MEDIUM));

        for (RecommendationResult r : results) {
            ProductCard card = new ProductCard(r, totalResults, currentMaxPrice);
            card.setAlignmentX(0.0f);
            resultsContainer.add(card);
            resultsContainer.add(Box.createVerticalStrut(UIStyles.PADDING_SMALL));
        }
        resultsContainer.revalidate();
        resultsContainer.repaint();
        SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
    }

    private JPanel createSummaryPanel(List<RecommendationResult> results) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        panel.setAlignmentX(0.0f);
        panel.setBackground(UIStyles.getBackgroundSecondary());
        panel.setBorder(new EmptyBorder(12, 16, 12, 16));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        if (!results.isEmpty()) {
            RecommendationResult best = results.get(0);
            panel.add(UIStyles.createLabel("🥇 Best: " + truncate(best.getTitle(), 40), UIStyles.FONT_BODY,
                    UIStyles.getGold()));
        }
        double minP = results.stream().mapToDouble(RecommendationResult::getPrice).min().orElse(0);
        double maxP = results.stream().mapToDouble(RecommendationResult::getPrice).max().orElse(0);
        panel.add(UIStyles.createLabel(String.format("💰 %.0f€ - %.0f€", minP, maxP), UIStyles.FONT_BODY,
                UIStyles.getTextSecondary()));
        return panel;
    }

    private void showNoResults() {
        resultsContainer.removeAll();
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(60, 40, 60, 40));
        JLabel il = new JLabel("🔍");
        il.setFont(UIStyles.getEmojiFont(48));
        il.setAlignmentX(0.5f);
        p.add(il);
        p.add(Box.createVerticalStrut(20));
        JLabel tl = UIStyles.createLabel("No Matching Products Found", UIStyles.FONT_HEADING,
                UIStyles.getTextPrimary());
        tl.setAlignmentX(0.5f);
        p.add(tl);
        statusLabel.setText("No results");
        statusLabel.setForeground(UIStyles.getWarning());
        resultsContainer.add(p);
        resultsContainer.revalidate();
        resultsContainer.repaint();
    }

    public void showLoading() {
        this.lastResults = null;
        resultsContainer.removeAll();
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(40, 40, 20, 40));
        LoadingSpinner s = new LoadingSpinner(48);
        s.setAlignmentX(0.5f);
        s.start();
        p.add(s);
        p.add(Box.createVerticalStrut(20));
        JLabel tl = UIStyles.createLabel("Searching...", UIStyles.FONT_HEADING, UIStyles.getTextPrimary());
        tl.setAlignmentX(0.5f);
        p.add(tl);
        resultsContainer.add(p);
        statusLabel.setText("Loading...");
        statusLabel.setForeground(UIStyles.getAccentPrimary());
        resultsContainer.revalidate();
        resultsContainer.repaint();
    }

    public void showError(String msg) {
        this.lastError = msg;
        resultsContainer.removeAll();
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(80, 40, 80, 40));
        JLabel il = new JLabel("❌");
        il.setFont(UIStyles.getEmojiFont(48));
        il.setAlignmentX(0.5f);
        p.add(il);
        p.add(Box.createVerticalStrut(20));
        JLabel tl = UIStyles.createLabel(msg, UIStyles.FONT_BODY, UIStyles.getError());
        tl.setAlignmentX(0.5f);
        p.add(tl);
        statusLabel.setText("Error");
        statusLabel.setForeground(UIStyles.getError());
        resultsContainer.add(p);
        resultsContainer.revalidate();
        resultsContainer.repaint();
    }

    private void refreshTheme() {
        Color bgPrimary = UIStyles.getBackgroundPrimary();
        Color bgSecondary = UIStyles.getBackgroundSecondary();

        setBackground(bgPrimary);
        if (headerPanel != null) {
            headerPanel.setBackground(bgSecondary);
            UIStyles.applyDeepBackground(headerPanel, bgSecondary);
        }
        if (resultsContainer != null) {
            resultsContainer.setBackground(bgPrimary);
            UIStyles.applyDeepBackground(resultsContainer, bgPrimary);
        }
        if (scrollPane != null) {
            scrollPane.setBackground(bgPrimary);
            if (scrollPane.getViewport() != null)
                scrollPane.getViewport().setBackground(bgPrimary);
        }

        statusLabel.setForeground(UIStyles.getTextPrimary());
        countLabel.setForeground(UIStyles.getTextSecondary());

        if (lastError != null)
            showError(lastError);
        else if (lastResults != null)
            displayResults(lastResults, currentMinPrice, currentMaxPrice);
        else
            showWelcomeMessage();

        repaint();
    }

    private String truncate(String t, int max) {
        return (t == null || t.length() <= max) ? t : t.substring(0, max) + "...";
    }

    /** Inner class for Loading Spinner */
    private static class LoadingSpinner extends JPanel {
        private int angle = 0;
        private Timer timer;
        private int size;

        public LoadingSpinner(int size) {
            this.size = size;
            setOpaque(false);
            setPreferredSize(new Dimension(size, size));
            timer = new Timer(50, e -> {
                angle = (angle + 15) % 360;
                repaint();
            });
        }

        public void start() {
            timer.start();
        }

        public void stop() {
            timer.stop();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int p = 4;
            int as = size - (p * 2);
            g2.setColor(UIStyles.getBorderColor());
            g2.setStroke(new BasicStroke(3));
            g2.drawOval(p, p, as, as);
            g2.setColor(UIStyles.getAccentPrimary());
            g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(new Arc2D.Double(p, p, as, as, angle, 90, Arc2D.OPEN));
            g2.dispose();
        }
    }
}
