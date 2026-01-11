package com.recommendation.ui;

import com.recommendation.model.RecommendationEngine;
import com.recommendation.model.RecommendationResult;
import com.recommendation.preprocessing.DataCleaner;
import com.recommendation.preprocessing.Product;
import com.recommendation.ui.components.*;
import com.recommendation.ui.core.ComparisonManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Main application window for the Product Recommendation System.
 * Features improved UI with theme support.
 */
public class MainFrame extends JFrame {

    private RecommendationEngine engine;
    private SearchPanel searchPanel;
    private ResultsPanel resultsPanel;
    private int totalProducts = 0;

    private JButton compareButton;
    private JButton themeButton;

    // Navigation Components
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JPanel navBar;
    private JButton navSearch;
    private JButton navWishlist;
    private JButton navCompare;
    private String currentTab = "SEARCH";

    private ComparisonPanel comparisonPanel;
    private JLabel loadingStatusLabel;
    private JPanel statusBar;

    public MainFrame() {
        super("E-Commerce Project");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 850);
        setLocationRelativeTo(null);

        // Set system look and feel for better native appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initial theme setup - add listener FIRST so it runs before child components
        UIStyles.addThemeListener(this::refreshAllComponents);

        // Initialize UI
        initializeGlobalControls();
        initializeComponents();

        // Load data in background
        loadDataAsync();

        // Register theme listener to refresh UI
        UIStyles.addThemeListener(() -> refreshAllComponents());

        // Timer for global compare button
        new Timer(200, e -> updateCompareButton()).start();
    }

    private JPanel headerPanel;

    private void initializeGlobalControls() {
        // Global Header
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIStyles.getBackgroundSecondary());
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        JLabel logoLabel = UIStyles.createLabel("🛍️ E-COMMERCE PROJECT", UIStyles.FONT_HEADING,
                UIStyles.getAccentPrimary());
        headerPanel.add(logoLabel, BorderLayout.WEST);

        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightActions.setOpaque(false);

        // Compare Button
        compareButton = UIStyles.createPrimaryButton("Select products to compare");
        compareButton.setFont(UIStyles.FONT_SMALL);
        compareButton.setEnabled(false);
        compareButton.addActionListener(e -> {
            switchTab("COMPARISON");
        });

        // Theme Toggle
        themeButton = UIStyles.createThemeToggleButton();

        rightActions.add(compareButton);
        rightActions.add(themeButton);

        headerPanel.add(rightActions, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
    }

    private void initializeComponents() {
        // Main container holding Nav + Content
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(UIStyles.getBackgroundPrimary());

        // Create panels
        searchPanel = new SearchPanel(e -> performSearch());
        resultsPanel = new ResultsPanel();
        searchPanel.setPreferredSize(new Dimension(380, getHeight()));

        WishlistPanel wishlistPanel = new WishlistPanel();
        comparisonPanel = new ComparisonPanel();

        // Split pane for Search View
        JSplitPane searchViewSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, searchPanel, resultsPanel);
        searchViewSplit.setDividerLocation(380);
        searchViewSplit.setDividerSize(1);
        searchViewSplit.setBorder(null);
        searchViewSplit.setBackground(UIStyles.getBorderColor());

        // --- Custom Navigation Bar ---
        navBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        navBar.setBackground(UIStyles.getBackgroundSecondary());
        navBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIStyles.getBorderColor()));

        navSearch = UIStyles.createNavButton("🔍 Search Products", true);
        navWishlist = UIStyles.createNavButton("❤️ My Wishlist", false);
        navCompare = UIStyles.createNavButton("⚖️ Comparison", false);

        navSearch.addActionListener(e -> switchTab("SEARCH"));
        navWishlist.addActionListener(e -> switchTab("WISHLIST"));
        navCompare.addActionListener(e -> switchTab("COMPARISON"));

        navBar.add(navSearch);
        navBar.add(navWishlist);
        navBar.add(navCompare);

        // --- Content Area (CardLayout) ---
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.add(searchViewSplit, "SEARCH");
        contentPanel.add(wishlistPanel, "WISHLIST");
        contentPanel.add(comparisonPanel, "COMPARISON");

        // Assemble
        mainContainer.add(navBar, BorderLayout.NORTH);
        mainContainer.add(contentPanel, BorderLayout.CENTER);

        add(mainContainer, BorderLayout.CENTER);

        statusBar = createStatusBar();
        add(statusBar, BorderLayout.SOUTH);
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setOpaque(true);
        statusBar.setBackground(UIStyles.getBackgroundSecondary());
        // Add a distinct top border for visibility separation
        statusBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UIStyles.getBorderColor()),
                new EmptyBorder(8, 16, 8, 16)));

        loadingStatusLabel = UIStyles.createLabel("Loading products...",
                UIStyles.FONT_SMALL, UIStyles.getTextMuted());
        statusBar.add(loadingStatusLabel, BorderLayout.WEST);

        JLabel versionLabel = UIStyles.createLabel("v1.0 • JAVA S7 Project",
                UIStyles.FONT_SMALL, UIStyles.getTextMuted());
        statusBar.add(versionLabel, BorderLayout.EAST);

        return statusBar;
    }

    /**
     * Load product data asynchronously.
     */
    private void loadDataAsync() {
        resultsPanel.showLoading();

        SwingWorker<List<Product>, Void> worker = new SwingWorker<List<Product>, Void>() {
            @Override
            protected List<Product> doInBackground() throws Exception {
                // Load all CSV files - SPECIFIC CATEGORIES ONLY
                String dataDir = "..\\02_data_collection\\raw\\";

                String[] csvFiles = {
                        dataDir + "cdiscount_smartphones.csv",
                        dataDir + "cdiscount_claviers.csv",
                        dataDir + "cdiscount_casques_bluetooth.csv",
                        dataDir + "cdiscount_ordinateurs.csv"
                        // Product files removed as requested
                };

                return DataCleaner.processMultipleCSVs(csvFiles);
            }

            @Override
            protected void done() {
                try {
                    List<Product> products = get();
                    totalProducts = products.size();
                    engine = new RecommendationEngine(products);

                    // Update categories in search panel
                    searchPanel.updateCategories(engine.getCategoryStats());

                    // Update status bar
                    updateStatusBar();

                    // Show welcome screen
                    resultsPanel.displayResults(null, 0, Double.MAX_VALUE);

                    JOptionPane.showMessageDialog(MainFrame.this,
                            String.format("✅ Successfully loaded %d products!\n\nReady to search.", products.size()),
                            "Data Loaded",
                            JOptionPane.INFORMATION_MESSAGE);

                } catch (Exception e) {
                    e.printStackTrace();
                    resultsPanel.showError("Failed to load data: " + e.getMessage());

                    JOptionPane.showMessageDialog(MainFrame.this,
                            "Failed to load product data:\n" + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    private void updateStatusBar() {
        if (loadingStatusLabel != null) {
            loadingStatusLabel.setText("✓ " + totalProducts + " products loaded successfully");
            // Use secondary text color for a neutral 'default' appearance
            loadingStatusLabel.setForeground(UIStyles.getTextSecondary());
        }
    }

    /**
     * Perform search with current parameters.
     */
    private void performSearch() {
        if (engine == null) {
            JOptionPane.showMessageDialog(this,
                    "Please wait for data to load first.",
                    "Not Ready",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get search parameters
        String query = searchPanel.getSearchQuery();
        String category = searchPanel.getSelectedCategory();
        double minPrice = searchPanel.getMinPrice();
        double maxPrice = searchPanel.getMaxPrice();

        // Show loading
        resultsPanel.showLoading();

        // Perform search in background
        SwingWorker<List<RecommendationResult>, Void> worker = new SwingWorker<List<RecommendationResult>, Void>() {
            @Override
            protected List<RecommendationResult> doInBackground() throws Exception {
                return engine.getRecommendations(query, minPrice, maxPrice, category, 30);
            }

            @Override
            protected void done() {
                try {
                    List<RecommendationResult> results = get();

                    // Apply sorting based on user selection
                    String sortOption = searchPanel.getSortOption();
                    if (sortOption != null) {
                        switch (sortOption) {
                            case "Price: Low → High":
                                results.sort(Comparator.comparingDouble(RecommendationResult::getPrice));
                                break;
                            case "Price: High → Low":
                                results.sort(Comparator.comparingDouble(RecommendationResult::getPrice).reversed());
                                break;
                            case "Rating":
                                results.sort(Comparator.comparingDouble(RecommendationResult::getAvgRating).reversed());
                                break;
                            case "Reviews":
                                results.sort(Comparator.comparingInt(RecommendationResult::getReviewCount).reversed());
                                break;
                            // "Relevance" is default - engine already sorted by score
                        }
                        // Re-assign ranks after sorting
                        for (int i = 0; i < results.size(); i++) {
                            results.get(i).setRank(i + 1);
                        }
                    }

                    resultsPanel.displayResults(results, minPrice, maxPrice);
                } catch (Exception e) {
                    e.printStackTrace();
                    resultsPanel.showError(e.getMessage());
                }
            }
        };

        worker.execute();
    }

    /**
     * Refresh all components when theme changes.
     */
    private void refreshAllComponents() {
        Color bgPrimary = UIStyles.getBackgroundPrimary();
        Color bgSecondary = UIStyles.getBackgroundSecondary();

        getContentPane().setBackground(bgPrimary);

        if (headerPanel != null) {
            headerPanel.setBackground(bgSecondary);
            UIStyles.applyDeepBackground(headerPanel, bgSecondary);
        }

        // Header and Theme Toggle
        if (themeButton != null) {
            themeButton.setBackground(UIStyles.getAccentPrimary());
            themeButton.setForeground(UIStyles.getTextOnAccent());
            themeButton.setText(UIStyles.isDarkMode() ? "☀" : "☾");
        }
        if (compareButton != null) {
            compareButton.setBackground(UIStyles.getAccentPrimary());
            compareButton.setForeground(UIStyles.getTextOnAccent());
        }

        // Parent tabbed pane
        // Custom Navigation Refresh
        if (navBar != null) {
            navBar.setBackground(bgSecondary);
            navBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIStyles.getBorderColor()));

            // Re-create buttons to apply new theme styles fully
            updateNavState();
        }

        if (statusBar != null) {
            statusBar.setBackground(bgSecondary);
            // Refresh border color
            statusBar.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 0, 0, 0, UIStyles.getBorderColor()),
                    new EmptyBorder(8, 16, 8, 16)));
            updateStatusBar(); // Refresh label color
        }

        repaint();
    }

    private void updateCompareButton() {
        if (compareButton == null)
            return;

        int count = ComparisonManager.getInstance().getSelected().size();
        compareButton.setEnabled(count >= 2);

        if (count == 0) {
            compareButton.setText("Select products to compare");
        } else if (count == 1) {
            compareButton.setText("Select 1 more to compare");
        } else {
            compareButton.setText("Compare Selected (" + count + ")");
        }

        compareButton.repaint();
    }

    /**
     * Main entry point.
     */
    public static void main(String[] args) {
        // Enable anti-aliasing for better text rendering
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }

    /**
     * Replaces standard tab titles with custom JLabel components.
     * This bypasses L&F rendering issues and guarantees visibility.
     */
    private void switchTab(String tabName) {
        currentTab = tabName;
        cardLayout.show(contentPanel, tabName);
        updateNavState();
    }

    private void updateNavState() {
        // Re-generate styled buttons to ensure correct theme colors
        // We preserve listeners by re-adding them or just updating properties
        // For simplicity with the factory, we update separate properties

        updateNavButton(navSearch, "SEARCH".equals(currentTab));
        updateNavButton(navWishlist, "WISHLIST".equals(currentTab));
        updateNavButton(navCompare, "COMPARISON".equals(currentTab));
    }

    private void updateNavButton(JButton btn, boolean isSelected) {
        btn.setFont(isSelected ? UIStyles.FONT_BUTTON : UIStyles.FONT_BODY);
        btn.setForeground(isSelected ? UIStyles.getAccentPrimary() : UIStyles.getTextSecondary());
        btn.setBackground(UIStyles.getBackgroundSecondary());

        if (isSelected) {
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, UIStyles.getAccentPrimary()),
                    new EmptyBorder(8, 16, 6, 16)));
            // Keep specialized listener logic in the factory or main nav setup
        } else {
            btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        }
    }
}
