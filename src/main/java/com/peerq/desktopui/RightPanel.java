package com.peerq.desktopui;

import javax.swing.*;
import java.awt.*;

public class RightPanel extends JPanel {
    public RightPanel() {
        setPreferredSize(new Dimension(300, 0));
        setBackground(new Color(36, 37, 42));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 16, 20, 16));

        // Notifications
        JLabel notifLabel = new JLabel("Notifications");
        notifLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        notifLabel.setForeground(new Color(0, 255, 127));
        add(notifLabel);
        add(Box.createVerticalStrut(10));
        for (int i = 0; i < 3; i++) {
            JLabel notif = new JLabel("• You have a new answer on Q" + (i+1));
            notif.setForeground(Color.WHITE);
            notif.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            add(notif);
        }
        add(Box.createVerticalStrut(30));

        // Productivity tools
        JLabel todoLabel = new JLabel("To-Do / Drafts");
        todoLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        todoLabel.setForeground(new Color(0, 255, 127));
        add(todoLabel);
        add(Box.createVerticalStrut(10));
        for (int i = 0; i < 2; i++) {
            JLabel todo = new JLabel("• Draft: Unsubmitted Q" + (i+1));
            todo.setForeground(Color.WHITE);
            todo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            add(todo);
        }
        add(Box.createVerticalStrut(30));

        // Trending/Leaderboard
        JLabel trendLabel = new JLabel("Trending / Leaderboard");
        trendLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        trendLabel.setForeground(new Color(0, 255, 127));
        add(trendLabel);
        add(Box.createVerticalStrut(10));
        for (int i = 0; i < 2; i++) {
            JLabel trend = new JLabel("#Tag" + (i+1) + " - 12 Qs");
            trend.setForeground(Color.WHITE);
            trend.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            add(trend);
        }
        add(Box.createVerticalGlue());
    }
} 