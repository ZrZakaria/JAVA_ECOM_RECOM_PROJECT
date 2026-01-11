package com.recommendation.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Centralized UI styling implementing User's Custom Teal Palette.
 * RESTORED: Includes all premium UI overrides and factory methods.
 */
public class UIStyles {

    // Initial theme state
    private static boolean isDarkMode = true;
    private static List<Runnable> themeListeners = new ArrayList<>();

    static {
        // Force initialization of UIManager defaults on class load
        updateStaticColors();
        updateUIManagerDefaults();
    }

    // ============== DARK THEME ==============
    private static class DarkTheme {
        static final Color BACKGROUND_PRIMARY = new Color(0x0F1F22);
        static final Color BACKGROUND_SECONDARY = new Color(0x162D31);
        static final Color BACKGROUND_CARD = new Color(0x162D31);
        static final Color BACKGROUND_INPUT = new Color(0x0F1F22);
        static final Color BACKGROUND_TABS = new Color(0x0A1416); // Darker for headers

        static final Color TEXT_PRIMARY = new Color(0xE6F4F3);
        static final Color TEXT_SECONDARY = new Color(0xA9C9C7);
        static final Color TEXT_MUTED = new Color(0x6F8F8D);
        static final Color TEXT_ON_ACCENT = new Color(0x0F1F22);

        static final Color ACCENT_PRIMARY = new Color(0x2FB9B3);
        static final Color ACCENT_HOVER = new Color(0x6ADBD4);
        static final Color ACCENT_SECONDARY = new Color(0x6ADBD4);

        static final Color BORDER_COLOR = new Color(0x2A3F43);

        static final Color SUCCESS = new Color(0x3CCF91);
        static final Color WARNING = new Color(0xF2B84B);
        static final Color ERROR = new Color(0xE06C75);

        static final Color GOLD = new Color(0xF2B84B);
        static final Color SILVER = new Color(0xA9C9C7);
        static final Color BRONZE = new Color(0xE09F3E);
    }

    // ============== LIGHT THEME ==============
    private static class LightTheme {
        static final Color BACKGROUND_PRIMARY = new Color(0xA9C1BF);
        static final Color BACKGROUND_SECONDARY = new Color(0xC5D8D6);
        static final Color BACKGROUND_CARD = new Color(0xDDE8E7);
        static final Color BACKGROUND_INPUT = new Color(0xA9C1BF);
        static final Color BACKGROUND_TABS = new Color(0xB5C8C6); // Slightly darker for headers

        static final Color TEXT_PRIMARY = new Color(0x0E2A2A);
        static final Color TEXT_SECONDARY = new Color(0x2A4D4A);
        static final Color TEXT_MUTED = new Color(0x4F7C7A);
        static final Color TEXT_ON_ACCENT = new Color(0xFFFFFF);

        static final Color ACCENT_PRIMARY = new Color(0x1FA6A0);
        static final Color ACCENT_HOVER = new Color(0x188F8A);
        static final Color ACCENT_SECONDARY = new Color(0x7EDDD6);

        static final Color BORDER_COLOR = new Color(0x8FAEA9);

        static final Color SUCCESS = new Color(0x2FBF8F);
        static final Color WARNING = new Color(0xF4B860);
        static final Color ERROR = new Color(0xD64550);

        static final Color GOLD = new Color(0xD4A017);
        static final Color SILVER = new Color(0x828A8A);
        static final Color BRONZE = new Color(0x9E7E53);
    }

    // ============== DYNAMIC GETTERS ==============
    public static Color getBackgroundPrimary() {
        return isDarkMode ? DarkTheme.BACKGROUND_PRIMARY : LightTheme.BACKGROUND_PRIMARY;
    }

    public static Color getBackgroundSecondary() {
        return isDarkMode ? DarkTheme.BACKGROUND_SECONDARY : LightTheme.BACKGROUND_SECONDARY;
    }

    public static Color getBackgroundCard() {
        return isDarkMode ? DarkTheme.BACKGROUND_CARD : LightTheme.BACKGROUND_CARD;
    }

    public static Color getBackgroundInput() {
        return isDarkMode ? DarkTheme.BACKGROUND_INPUT : LightTheme.BACKGROUND_INPUT;
    }

    public static Color getBackgroundTabs() {
        return isDarkMode ? DarkTheme.BACKGROUND_TABS : LightTheme.BACKGROUND_TABS;
    }

    public static Color getAccentPrimary() {
        return isDarkMode ? DarkTheme.ACCENT_PRIMARY : LightTheme.ACCENT_PRIMARY;
    }

    public static Color getAccentHover() {
        return isDarkMode ? DarkTheme.ACCENT_HOVER : LightTheme.ACCENT_HOVER;
    }

    public static Color getAccentSecondary() {
        return isDarkMode ? DarkTheme.ACCENT_SECONDARY : LightTheme.ACCENT_SECONDARY;
    }

    public static Color getTextPrimary() {
        return isDarkMode ? DarkTheme.TEXT_PRIMARY : LightTheme.TEXT_PRIMARY;
    }

    public static Color getTextSecondary() {
        return isDarkMode ? DarkTheme.TEXT_SECONDARY : LightTheme.TEXT_SECONDARY;
    }

    public static Color getTextMuted() {
        return isDarkMode ? DarkTheme.TEXT_MUTED : LightTheme.TEXT_MUTED;
    }

    public static Color getTextOnAccent() {
        return isDarkMode ? DarkTheme.TEXT_ON_ACCENT : LightTheme.TEXT_ON_ACCENT;
    }

    public static Color getBorderColor() {
        return isDarkMode ? DarkTheme.BORDER_COLOR : LightTheme.BORDER_COLOR;
    }

    public static Color getSuccess() {
        return isDarkMode ? DarkTheme.SUCCESS : LightTheme.SUCCESS;
    }

    public static Color getWarning() {
        return isDarkMode ? DarkTheme.WARNING : LightTheme.WARNING;
    }

    public static Color getError() {
        return isDarkMode ? DarkTheme.ERROR : LightTheme.ERROR;
    }

    public static Color getGold() {
        return isDarkMode ? DarkTheme.GOLD : LightTheme.GOLD;
    }

    public static Color getSilver() {
        return isDarkMode ? DarkTheme.SILVER : LightTheme.SILVER;
    }

    public static Color getBronze() {
        return isDarkMode ? DarkTheme.BRONZE : LightTheme.BRONZE;
    }

    // Legacy mapping for immediate compatibility
    public static Color BACKGROUND_PRIMARY = getBackgroundPrimary();
    public static Color BACKGROUND_SECONDARY = getBackgroundSecondary();
    public static Color TEXT_PRIMARY = getTextPrimary();
    public static Color TEXT_SECONDARY = getTextSecondary();
    public static Color TEXT_MUTED = getTextMuted();
    public static Color BACKGROUND_CARD = getBackgroundCard();
    public static Color BORDER_COLOR = getBorderColor();
    public static Color ACCENT_PRIMARY = getAccentPrimary();
    public static Color ACCENT_SECONDARY = getAccentSecondary();
    public static Color SUCCESS = getSuccess();
    public static Color WARNING = getWarning();
    public static Color ERROR = getError();

    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_SUBHEADING = new Font("Segoe UI Semibold", Font.PLAIN, 14);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_INPUT = new Font("Segoe UI", Font.PLAIN, 15);

    private static final Font EMOJI_FONT = getDetectedEmojiFont();

    private static Font getDetectedEmojiFont() {
        String[] fonts = { "Segoe UI Emoji", "Segoe UI Symbol", "Apple Color Emoji", "Noto Color Emoji", "Symbola",
                "SansSerif" };
        for (String fName : fonts) {
            Font f = new Font(fName, Font.PLAIN, 18);
            if (f.canDisplay('\u2605')) // Full Star
                return f;
        }
        return new Font("SansSerif", Font.PLAIN, 18);
    }

    public static Font getEmojiFont(int size) {
        return EMOJI_FONT.deriveFont((float) size);
    }

    // Spacing
    public static final int PADDING_XSMALL = 6;
    public static final int PADDING_SMALL = 12;
    public static final int PADDING_MEDIUM = 18;
    public static final int PADDING_LARGE = 24;
    public static final int PADDING_XLARGE = 36;
    public static final int INPUT_HEIGHT = 45;
    public static final int BUTTON_HEIGHT = 48;

    // ============== THEME TOGGLE ==============
    public static boolean isDarkMode() {
        return isDarkMode;
    }

    public static void toggleTheme() {
        isDarkMode = !isDarkMode;
        updateStaticColors();
        updateUIManagerDefaults();
        notifyThemeListeners();
    }

    public static void setDarkMode(boolean dark) {
        if (isDarkMode != dark) {
            isDarkMode = dark;
            updateStaticColors();
            updateUIManagerDefaults();
            notifyThemeListeners();
        }
    }

    private static void updateUIManagerDefaults() {
        Color bgPrimary = getBackgroundPrimary();
        Color bgSecondary = getBackgroundSecondary();
        Color bgTabs = getBackgroundTabs();
        Color textPrimary = getTextPrimary();
        Color accent = getAccentPrimary();

        UIManager.put("Panel.background", bgPrimary);
        UIManager.put("Label.background", bgPrimary);
        UIManager.put("Label.foreground", textPrimary);
        UIManager.put("TabbedPane.background", bgTabs);
        UIManager.put("TabbedPane.foreground", textPrimary);
        UIManager.put("TabbedPane.selected", accent);
        UIManager.put("TabbedPane.focus", new Color(0, 0, 0, 0)); // Remove focus ring
        UIManager.put("TabbedPane.unselectedForeground", textPrimary);
        UIManager.put("TabbedPane.selectedForeground", getTextOnAccent());
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
        UIManager.put("TabbedPane.tabInsets", new Insets(10, 24, 10, 24));
        UIManager.put("TabbedPane.tabsOpaque", true);
        UIManager.put("TabbedPane.highlight", bgTabs);
        UIManager.put("TabbedPane.light", bgTabs);
        UIManager.put("TabbedPane.shadow", bgTabs);
        UIManager.put("TabbedPane.darkShadow", bgTabs);
        UIManager.put("TabbedPane.borderHighlightColor", bgTabs);

        UIManager.put("ScrollPane.background", bgPrimary);
        UIManager.put("Viewport.background", bgPrimary);
        UIManager.put("TextField.background", getBackgroundInput());
        UIManager.put("TextField.foreground", textPrimary);
        UIManager.put("Button.background", bgSecondary);
        UIManager.put("Button.foreground", textPrimary);

        UIManager.put("CheckBox.background", bgSecondary);
        UIManager.put("CheckBox.foreground", textPrimary);
        UIManager.put("CheckBox.icon", null); // Let it use default but with our colors

        UIManager.put("RadioButton.background", bgSecondary);
        UIManager.put("RadioButton.foreground", textPrimary);

        UIManager.put("ProgressBar.background", bgSecondary);
        UIManager.put("ProgressBar.foreground", accent);
        UIManager.put("ProgressBar.selectionBackground", Color.WHITE);
        UIManager.put("ProgressBar.selectionForeground", accent);
        UIManager.put("ComboBox.background", getBackgroundInput());
        UIManager.put("ComboBox.foreground", textPrimary);
        UIManager.put("ComboBox.selectionBackground", accent);
        UIManager.put("ComboBox.selectionForeground", getTextOnAccent());
        UIManager.put("ComboBox.buttonBackground", getBackgroundInput());
        UIManager.put("ComboBox.buttonShadow", getBackgroundInput());
        UIManager.put("ComboBox.buttonHighlight", getBackgroundInput());
    }

    private static void updateStaticColors() {
        BACKGROUND_PRIMARY = getBackgroundPrimary();
        BACKGROUND_SECONDARY = getBackgroundSecondary();
        BACKGROUND_CARD = getBackgroundCard();
        TEXT_PRIMARY = getTextPrimary();
        TEXT_SECONDARY = getTextSecondary();
        TEXT_MUTED = getTextMuted();
        BORDER_COLOR = getBorderColor();
        ACCENT_PRIMARY = getAccentPrimary();
        ACCENT_SECONDARY = getAccentSecondary();
        SUCCESS = getSuccess();
        WARNING = getWarning();
        ERROR = getError();
    }

    public static void addThemeListener(Runnable listener) {
        themeListeners.add(listener);
    }

    private static void notifyThemeListeners() {
        for (Runnable l : themeListeners)
            l.run();
    }

    // ============== COMPONENT FACTORY ==============

    public static JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        button.setFont(FONT_BUTTON);
        button.setForeground(Color.WHITE);
        button.setBackground(getAccentPrimary());
        button.setBorder(new EmptyBorder(10, 24, 10, 24));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(getAccentHover());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(getAccentPrimary());
            }
        });
        return button;
    }

    public static JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        button.setFont(FONT_BUTTON);
        button.setForeground(getTextPrimary());
        button.setBackground(getBackgroundCard());
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(getBorderColor(), 1),
                new EmptyBorder(8, 20, 8, 20)));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        return button;
    }

    public static JTextField createTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(FONT_INPUT);
        field.setForeground(getTextPrimary());
        field.setBackground(getBackgroundInput());
        field.setCaretColor(getTextPrimary());
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(getBorderColor(), 1),
                new EmptyBorder(8, 12, 8, 12)));
        field.setPreferredSize(new Dimension(220, INPUT_HEIGHT));
        if (placeholder != null)
            field.setToolTipText(placeholder);
        return field;
    }

    public static JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setUI(new CustomComboBoxUI());
        comboBox.setFont(FONT_INPUT);
        comboBox.setForeground(getTextPrimary());
        comboBox.setBackground(getBackgroundInput());
        comboBox.setPreferredSize(new Dimension(220, INPUT_HEIGHT));
        comboBox.setOpaque(true);

        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                l.setBorder(new EmptyBorder(8, 12, 8, 12));
                l.setOpaque(true);
                if (isSelected) {
                    l.setBackground(getAccentPrimary());
                    l.setForeground(getTextOnAccent());
                } else {
                    l.setBackground(getBackgroundInput());
                    l.setForeground(getTextPrimary());
                }
                return l;
            }
        });

        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(getAccentPrimary(), 1),
                new EmptyBorder(4, 8, 4, 4)));

        comboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                comboBox.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(getAccentHover(), 2),
                        new EmptyBorder(3, 7, 3, 3)));
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                comboBox.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(getAccentPrimary(), 1),
                        new EmptyBorder(4, 8, 4, 4)));
            }
        });

        return comboBox;
    }

    private static class CustomComboBoxUI extends javax.swing.plaf.basic.BasicComboBoxUI {
        @Override
        protected javax.swing.plaf.basic.ComboPopup createPopup() {
            javax.swing.plaf.basic.BasicComboPopup popup = (javax.swing.plaf.basic.BasicComboPopup) super.createPopup();
            popup.getList().setBackground(getBackgroundInput());
            popup.getList().setSelectionBackground(getAccentPrimary());
            popup.getList().setSelectionForeground(getTextOnAccent());
            popup.setBorder(BorderFactory.createLineBorder(getAccentPrimary(), 1));
            return popup;
        }

        @Override
        protected JButton createArrowButton() {
            JButton button = new JButton() {
                @Override
                public void paint(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(comboBox.getBackground());
                    g2.fillRect(0, 0, getWidth(), getHeight());

                    int w = getWidth(), h = getHeight(), size = 8;
                    int x = (w - size) / 2, y = (h - size) / 2;
                    Path2D arrow = new Path2D.Double();
                    arrow.moveTo(x, y + 2);
                    arrow.lineTo(x + size / 2.0, y + size - 2);
                    arrow.lineTo(x + size, y + 2);

                    g2.setColor(getTextPrimary());
                    g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.draw(arrow);
                    g2.dispose();
                }
            };
            button.setBorder(null);
            button.setFocusable(false);
            button.setOpaque(true);
            button.setContentAreaFilled(false);
            return button;
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            super.paint(g, c);
        }
    }

    public static void applyDeepBackground(Container container, Color bg) {
        if (container == null)
            return;
        container.setBackground(bg);
        if (container instanceof JComponent) {
            ((JComponent) container).setOpaque(true);
        }

        for (Component c : container.getComponents()) {
            if (c instanceof JPanel || c instanceof JScrollPane || c instanceof JViewport || c instanceof JTabbedPane) {
                applyDeepBackground((Container) c, bg);
            } else if (c instanceof JLabel) {
                JLabel l = (JLabel) c;
                Color fg = l.getForeground();
                // Preserve special colors from either mode
                boolean isSpecial = fg.equals(getAccentPrimary()) || fg.equals(getAccentHover()) ||
                        fg.equals(new Color(52, 211, 153)) || fg.equals(new Color(5, 150, 105)) || // Success
                        fg.equals(new Color(251, 191, 36)) || fg.equals(new Color(217, 119, 6)) || // Warning
                        fg.equals(new Color(248, 113, 113)) || fg.equals(new Color(220, 38, 38)) || // Error
                        fg.equals(new Color(251, 191, 36)) || fg.equals(new Color(180, 83, 9)); // Gold/Bronze

                if (!isSpecial) {
                    l.setForeground(getTextPrimary());
                }
            } else if (c instanceof JButton) {
                restyleButton((JButton) c, bg);
            } else if (c instanceof JComboBox) {
                restyleComboBox((JComboBox<?>) c);
            } else if (c instanceof JTextField) {
                c.setBackground(getBackgroundInput());
                c.setForeground(getTextPrimary());
                ((JComponent) c).setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(getBorderColor(), 1),
                        new EmptyBorder(8, 12, 8, 12)));
            }
        }
    }

    public static void restyleButton(JButton b, Color containerBg) {
        // If it's a primary (accent) button, keep its accent colors
        if (b.getBackground().equals(getAccentPrimary()) || b.getBackground().equals(getAccentHover()) ||
                b.getForeground().equals(Color.WHITE)) {

            b.setUI(new javax.swing.plaf.basic.BasicButtonUI());
            b.setBackground(getAccentPrimary());
            b.setForeground(Color.WHITE);
            b.setBorder(new EmptyBorder(10, 24, 10, 24));
        } else if (b.isOpaque()) {
            b.setUI(new javax.swing.plaf.basic.BasicButtonUI());
            b.setBackground(UIStyles.getBackgroundCard());
            b.setForeground(getTextPrimary());
            b.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(getBorderColor(), 1),
                    new EmptyBorder(8, 20, 8, 20)));
        }
    }

    public static void restyleComboBox(JComboBox<?> comboBox) {
        comboBox.setUI(new CustomComboBoxUI());
        comboBox.setBackground(getBackgroundInput());
        comboBox.setForeground(getTextPrimary());
        if (comboBox.getRenderer() instanceof JComponent) {
            ((JComponent) comboBox.getRenderer()).setBackground(getBackgroundInput());
            ((JComponent) comboBox.getRenderer()).setForeground(getTextPrimary());
        }
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(getAccentPrimary(), 1),
                new EmptyBorder(4, 8, 4, 4)));
    }

    public static JLabel createLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    public static JButton createThemeToggleButton() {
        JButton button = new JButton(isDarkMode ? "☀" : "☾");
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        button.setFont(getEmojiFont(18));
        button.setToolTipText(isDarkMode ? "Switch to Light Mode" : "Switch to Dark Mode");
        button.setForeground(getTextOnAccent());
        button.setBackground(getAccentPrimary());
        button.setBorder(new EmptyBorder(6, 12, 6, 12));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addActionListener(e -> {
            toggleTheme();
            button.setText(isDarkMode ? "☀" : "☾");
            button.setToolTipText(isDarkMode ? "Switch to Light Mode" : "Switch to Dark Mode");
            button.setForeground(getTextOnAccent());
            button.setBackground(getAccentPrimary());
        });
        return button;
    }

    public static String getStarRating(double rating) {
        int fullStars = (int) rating;
        boolean halfStar = (rating - fullStars) >= 0.5;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            if (i < fullStars)
                sb.append("★");
            else if (i == fullStars && halfStar)
                sb.append("★");
            else
                sb.append("☆");
        }
        return sb.toString();
    }

    public static JButton createNavButton(String text, boolean isSelected) {
        JButton btn = new JButton(text);
        btn.setFont(isSelected ? FONT_BUTTON : FONT_BODY);
        btn.setForeground(isSelected ? getAccentPrimary() : getTextSecondary());
        btn.setBackground(getBackgroundSecondary()); // Match header background
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add bottom border for selection indication
        if (isSelected) {
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, getAccentPrimary()),
                    new EmptyBorder(8, 16, 6, 16)));
        } else {
            btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        }

        // Add simple hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!btn.getForeground().equals(getAccentPrimary())) { // If not selected
                    btn.setForeground(getTextPrimary());
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!btn.getForeground().equals(getAccentPrimary())) { // If not selected
                    btn.setForeground(getTextSecondary());
                }
            }
        });

        return btn;
    }
}
