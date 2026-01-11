package com.recommendation.ui.components;

import com.recommendation.ui.UIStyles;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Enhanced SearchPanel with clearly visible input fields and better sizing.
 * Moved to components package.
 */
public class SearchPanel extends JPanel {

    private JTextField searchField;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> sortCombo;
    private JTextField minPriceField;
    private JTextField maxPriceField;
    private JButton searchButton;
    private JLabel statsLabel;
    private JPanel contentPanel;

    public SearchPanel(ActionListener searchAction) {
        setLayout(new BorderLayout());
        setBackground(UIStyles.getBackgroundSecondary());

        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(UIStyles.getBackgroundSecondary());
        contentPanel.setBorder(new EmptyBorder(UIStyles.PADDING_LARGE, UIStyles.PADDING_LARGE, UIStyles.PADDING_LARGE,
                UIStyles.PADDING_LARGE));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        JLabel productSearchLabel = UIStyles.createLabel("Product Search", UIStyles.FONT_TITLE,
                UIStyles.getTextPrimary());
        productSearchLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the product search label
        headerPanel.add(productSearchLabel, BorderLayout.CENTER); // Add to center of headerPanel
        contentPanel.add(headerPanel);
        contentPanel.add(Box.createVerticalStrut(UIStyles.PADDING_XLARGE));

        contentPanel.add(createSectionLabel("Keywords"));
        contentPanel.add(Box.createVerticalStrut(UIStyles.PADDING_XSMALL));
        searchField = UIStyles.createTextField("e.g. Samsung Galaxy...");
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIStyles.INPUT_HEIGHT));
        searchField.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(searchField);
        contentPanel.add(Box.createVerticalStrut(UIStyles.PADDING_LARGE));

        contentPanel.add(createSectionLabel("Category"));
        contentPanel.add(Box.createVerticalStrut(UIStyles.PADDING_XSMALL));
        categoryCombo = UIStyles.createComboBox(
                new String[] { "All Categories", "smartphones", "claviers", "casques_bluetooth", "ordinateurs" });
        UIStyles.restyleComboBox(categoryCombo);
        categoryCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIStyles.INPUT_HEIGHT));
        categoryCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(categoryCombo);
        contentPanel.add(Box.createVerticalStrut(UIStyles.PADDING_LARGE));

        contentPanel.add(createSectionLabel("Price Range (€)"));
        contentPanel.add(Box.createVerticalStrut(UIStyles.PADDING_XSMALL));
        JPanel pricePanel = new JPanel(new GridLayout(1, 2, UIStyles.PADDING_MEDIUM, 0));
        pricePanel.setOpaque(false);
        pricePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIStyles.INPUT_HEIGHT));
        pricePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        minPriceField = UIStyles.createTextField("0");
        maxPriceField = UIStyles.createTextField("Max");
        pricePanel.add(minPriceField);
        pricePanel.add(maxPriceField);
        contentPanel.add(pricePanel);
        contentPanel.add(Box.createVerticalStrut(UIStyles.PADDING_LARGE));

        contentPanel.add(createSectionLabel("Sort By"));
        contentPanel.add(Box.createVerticalStrut(UIStyles.PADDING_XSMALL));
        sortCombo = UIStyles.createComboBox(
                new String[] { "Relevance", "Price: Low → High", "Price: High → Low", "Rating", "Reviews" });
        UIStyles.restyleComboBox(sortCombo);
        sortCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIStyles.INPUT_HEIGHT));
        sortCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(sortCombo);
        contentPanel.add(Box.createVerticalStrut(UIStyles.PADDING_XLARGE));

        searchButton = UIStyles.createPrimaryButton("SEARCH PRODUCTS");
        searchButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIStyles.INPUT_HEIGHT + 5));
        searchButton.setPreferredSize(new Dimension(Integer.MAX_VALUE, UIStyles.INPUT_HEIGHT + 5));
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        searchButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        searchButton.addActionListener(searchAction);
        contentPanel.add(searchButton);

        searchField.addActionListener(searchAction);
        minPriceField.addActionListener(searchAction);
        maxPriceField.addActionListener(searchAction);

        contentPanel.add(Box.createVerticalGlue());
        statsLabel = UIStyles.createLabel("Press Enter to search", UIStyles.FONT_SMALL, UIStyles.getTextMuted());
        statsLabel.setAlignmentX(0.5f);
        contentPanel.add(statsLabel);

        add(contentPanel, BorderLayout.CENTER);
        UIStyles.addThemeListener(this::refreshTheme);
    }

    private JLabel createSectionLabel(String text) {
        JLabel label = UIStyles.createLabel(text, UIStyles.FONT_SUBHEADING, UIStyles.getTextSecondary());
        label.setAlignmentX(0.5f);
        return label;
    }

    private void refreshTheme() {
        Color bg = UIStyles.getBackgroundSecondary();
        setBackground(bg);
        if (contentPanel != null) {
            contentPanel.setBackground(bg);
            UIStyles.applyDeepBackground(this, bg);
        }
        repaint();
    }

    public void updateCategories(Map<String, Integer> categoryStats) {
        categoryCombo.removeAllItems();
        categoryCombo.addItem("All Categories");

        for (Map.Entry<String, Integer> entry : categoryStats.entrySet()) {
            categoryCombo.addItem(entry.getKey() + " (" + entry.getValue() + ")");
        }
    }

    public String getSearchQuery() {
        String t = searchField.getText().trim();
        return (t.equals("e.g. Samsung Galaxy...") || t.isEmpty()) ? "" : t;
    }

    public String getSelectedCategory() {
        String c = (String) categoryCombo.getSelectedItem();
        if (c == null || c.equals("All Categories"))
            return "";
        // Remove the " (count)" part if present
        if (c.contains(" (")) {
            return c.substring(0, c.lastIndexOf(" (")).trim();
        }
        return c;
    }

    public double getMinPrice() {
        try {
            return Double.parseDouble(minPriceField.getText().trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    public double getMaxPrice() {
        try {
            String t = maxPriceField.getText().trim();
            if (t.equalsIgnoreCase("Max") || t.isEmpty())
                return Double.MAX_VALUE;
            return Double.parseDouble(t);
        } catch (Exception e) {
            return Double.MAX_VALUE;
        }
    }

    public String getSortOption() {
        return (String) sortCombo.getSelectedItem();
    }
}
