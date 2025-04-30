package com.peerq;

import com.peerq.ui.LoginForm;
import com.peerq.util.DBConnection;

import javax.swing.*;
import java.awt.*;

/**
 * Main entry point for the PeerQ application.
 */
public class Main {
    /**
     * Main method that starts the application
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Set application look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Enhance Swing UI settings
            enhanceSwingSettings();
            
        } catch (Exception e) {
            System.out.println("Error setting look and feel: " + e.getMessage());
        }
        
        // Initialize database connection
        try {
            // Test database connection
            DBConnection.getConnection().close();
            System.out.println("Database connection successful.");
        } catch (Exception e) {
            System.err.println("Error connecting to database: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                    "Failed to connect to the database. Please check your database settings.\n\nError: " + e.getMessage(),
                    "Database Connection Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        // Launch the application
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginForm loginForm = new LoginForm();
                loginForm.setVisible(true);
            }
        });
        
        // Add shutdown hook to close database connections
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                DBConnection.closeAllConnections();
                System.out.println("Application shutting down, closed database connections.");
            }
        });
    }
    
    /**
     * Enhances default Swing UI settings for better appearance
     */
    private static void enhanceSwingSettings() {
        // Improve font rendering
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        
        // Set default button padding
        UIManager.put("Button.margin", new Insets(5, 15, 5, 15));
        
        // Set default font
        Font defaultFont = new Font("SansSerif", Font.PLAIN, 14);
        UIManager.put("Button.font", defaultFont);
        UIManager.put("Label.font", defaultFont);
        UIManager.put("TextField.font", defaultFont);
        UIManager.put("TextArea.font", defaultFont);
        UIManager.put("ComboBox.font", defaultFont);
        UIManager.put("CheckBox.font", defaultFont);
        
        // Set focus behavior
        UIManager.put("Button.focusPainted", false);
        
        // Set other UI properties
        UIManager.put("ScrollBar.width", 12);
        UIManager.put("ScrollBar.thumbDarkShadow", Color.LIGHT_GRAY);
        UIManager.put("ScrollBar.thumbHighlight", Color.WHITE);
        UIManager.put("ScrollBar.thumbShadow", Color.LIGHT_GRAY);
    }
}
