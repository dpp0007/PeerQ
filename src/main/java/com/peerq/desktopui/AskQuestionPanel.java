package com.peerq.desktopui;

import javax.swing.*;
import java.awt.*;

public class AskQuestionPanel extends JPanel {
    private final PeerQMainFrame mainFrame;
    
    public AskQuestionPanel(PeerQMainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(new Color(255, 255, 255));
        
        // Create header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(255, 255, 255));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Ask a Question");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Create form panel
        JPanel formPanel = createFormPanel();
        
        // Add components to panel
        add(headerPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(255, 255, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        
        // Title field
        JLabel titleLabel = new JLabel("Title");
        JTextField titleField = new JTextField(40);
        titleField.setPreferredSize(new Dimension(600, 30));
        
        // Details field
        JLabel detailsLabel = new JLabel("Details");
        JTextArea detailsArea = new JTextArea(8, 40);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setLineWrap(true);
        JScrollPane detailsScroll = new JScrollPane(detailsArea);
        detailsScroll.setPreferredSize(new Dimension(600, 200));
        
        // Category dropdown
        JLabel categoryLabel = new JLabel("Category");
        String[] categories = {"Select a category", "Academics", "Campus Life", "Career", "Technology", "Miscellaneous"};
        JComboBox<String> categoryCombo = new JComboBox<>(categories);
        categoryCombo.setPreferredSize(new Dimension(200, 30));
        
        // Tags field
        JLabel tagsLabel = new JLabel("Tags (optional, comma separated)");
        JTextField tagsField = new JTextField(40);
        tagsField.setPreferredSize(new Dimension(600, 30));
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(new Color(255, 255, 255));
        
        JButton postButton = new JButton("Post Question");
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBorderPainted(false);
        cancelButton.setContentAreaFilled(false);
        cancelButton.setForeground(new Color(0, 120, 212));
        
        postButton.addActionListener(e -> {
            // TODO: Implement question posting
            JOptionPane.showMessageDialog(this, "Question posted successfully!");
            mainFrame.showQuestionList();
        });
        
        cancelButton.addActionListener(e -> mainFrame.showQuestionList());
        
        buttonPanel.add(postButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(cancelButton);
        
        // Add components to panel
        panel.add(titleLabel, gbc);
        panel.add(titleField, gbc);
        panel.add(detailsLabel, gbc);
        panel.add(detailsScroll, gbc);
        panel.add(categoryLabel, gbc);
        panel.add(categoryCombo, gbc);
        panel.add(tagsLabel, gbc);
        panel.add(tagsField, gbc);
        panel.add(buttonPanel, gbc);
        
        return panel;
    }
} 