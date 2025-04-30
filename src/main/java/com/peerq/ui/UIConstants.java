package com.peerq.ui;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

/**
 * Constants for UI styling to maintain a consistent look and feel across the application.
 * These values follow the Reddit-like modern UI guidelines.
 */
public class UIConstants {
    // Colors
    public static final Color BG_COLOR = new Color(247, 247, 247); // Light grey background
    public static final Color CARD_BG_COLOR = new Color(255, 255, 255); // White for cards
    public static final Color PRIMARY_COLOR = new Color(79, 153, 209); // Soft blue
    public static final Color SECONDARY_COLOR = new Color(100, 180, 190); // Teal accent
    public static final Color TEXT_COLOR = new Color(60, 60, 60); // Dark grey text
    public static final Color LIGHT_TEXT_COLOR = new Color(120, 120, 120); // Light grey text
    public static final Color ACCENT_COLOR = new Color(255, 86, 0); // Reddit-like orange for highlights
    public static final Color ERROR_COLOR = new Color(255, 99, 99); // Soft red for errors
    public static final Color SUCCESS_COLOR = new Color(92, 184, 92); // Soft green for success
    
    // Fonts
    public static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 20);
    public static final Font SUBTITLE_FONT = new Font("SansSerif", Font.BOLD, 16);
    public static final Font REGULAR_FONT = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font SMALL_FONT = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 14);
    
    // Borders
    public static final Border CARD_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(225, 225, 225), 1, true),
        BorderFactory.createEmptyBorder(15, 15, 15, 15)
    );
    public static final Border FIELD_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
        BorderFactory.createEmptyBorder(8, 8, 8, 8)
    );
    public static final Border BUTTON_BORDER = BorderFactory.createEmptyBorder(10, 15, 10, 15);
    public static final Border PANEL_PADDING = BorderFactory.createEmptyBorder(20, 20, 20, 20);
    
    // Spacing
    public static final int STANDARD_PADDING = 15;
    public static final int SMALL_PADDING = 8;
    public static final int LARGE_PADDING = 25;
    public static final int FORM_FIELD_HEIGHT = 35;
    
    // Rounded corners (used with custom painting)
    public static final int BORDER_RADIUS = 8;
    
    // Application title
    public static final String APP_TITLE = "PeerQ - College Community Platform";
    
    // Categories
    public static final String[] QUESTION_CATEGORIES = {
        "Academics", "Clubs", "Dorm Life", "Events", "General", "Career"
    };
}
