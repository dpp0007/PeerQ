package com.peerq.desktopui;

import javax.swing.*;
import java.awt.*;

public class SidebarPanel extends JPanel {
    public SidebarPanel() {
        setPreferredSize(new Dimension(90, 0));
        setBackground(new Color(30, 32, 40));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Profile section
        JPanel profilePanel = new JPanel();
        profilePanel.setBackground(new Color(30, 32, 40));
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        JLabel avatar = new JLabel("👤", SwingConstants.CENTER);
        avatar.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel name = new JLabel("Student");
        name.setForeground(Color.WHITE);
        name.setFont(new Font("Segoe UI", Font.BOLD, 14));
        name.setAlignmentX(Component.CENTER_ALIGNMENT);
        profilePanel.add(avatar);
        profilePanel.add(Box.createVerticalStrut(8));
        profilePanel.add(name);

        // Navigation section
        JPanel navPanel = new JPanel();
        navPanel.setBackground(new Color(30, 32, 40));
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        String[] navItems = {"Home", "My Qs", "Answers", "Bookmarks", "Explore", "Leaderboard"};
        for (String item : navItems) {
            JButton btn = new JButton(item);
            btn.setMaximumSize(new Dimension(80, 40));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setFocusPainted(false);
            btn.setBackground(new Color(40, 44, 52));
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            btn.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            navPanel.add(btn);
            navPanel.add(Box.createVerticalStrut(6));
        }

        // Quick action (Ask Question)
        JButton askBtn = new JButton("+ Ask");
        askBtn.setMaximumSize(new Dimension(80, 40));
        askBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        askBtn.setBackground(new Color(0, 255, 127));
        askBtn.setForeground(Color.BLACK);
        askBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        askBtn.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));

        add(profilePanel);
        add(Box.createVerticalStrut(20));
        add(navPanel);
        add(Box.createVerticalGlue());
        add(askBtn);
        add(Box.createVerticalStrut(20));
    }
} 