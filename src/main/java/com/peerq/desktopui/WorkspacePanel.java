package com.peerq.desktopui;

import javax.swing.*;
import java.awt.*;

public class WorkspacePanel extends JPanel {
    public WorkspacePanel() {
        setBackground(new Color(40, 44, 52));
        setLayout(new BorderLayout());

        // Header bar
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(36, 37, 42));
        header.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));
        JTextField search = new JTextField();
        search.setPreferredSize(new Dimension(260, 36));
        search.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        search.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 70), 1, true),
            BorderFactory.createEmptyBorder(6, 16, 6, 16)
        ));
        search.setBackground(new Color(50, 52, 60));
        search.setForeground(Color.WHITE);
        search.setCaretColor(new Color(0, 255, 127));
        search.setText("Search questions, topics, or users...");
        header.add(search, BorderLayout.WEST);

        // Filter chips and sort dropdown (placeholders)
        JPanel filters = new JPanel();
        filters.setOpaque(false);
        filters.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filters.add(new JLabel("All Categories"));
        filters.add(new JLabel("Answered"));
        filters.add(new JLabel("Newest"));
        JComboBox<String> sort = new JComboBox<>(new String[]{"Relevance", "Newest", "Most Answered", "Trending"});
        filters.add(sort);
        header.add(filters, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);

        // Card feed area (placeholder)
        JPanel feed = new JPanel();
        feed.setBackground(new Color(40, 44, 52));
        feed.setLayout(new BoxLayout(feed, BoxLayout.Y_AXIS));
        feed.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));
        for (int i = 0; i < 5; i++) {
            JPanel card = new JPanel();
            card.setBackground(new Color(50, 52, 60));
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 70), 1, true),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
            ));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
            card.setLayout(new BorderLayout());
            JLabel title = new JLabel("Sample Question Title " + (i+1));
            title.setFont(new Font("Segoe UI", Font.BOLD, 16));
            title.setForeground(Color.WHITE);
            card.add(title, BorderLayout.NORTH);
            JLabel meta = new JLabel("Tags | Author | 2h ago | 3 answers");
            meta.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            meta.setForeground(new Color(180, 180, 180));
            card.add(meta, BorderLayout.SOUTH);
            feed.add(card);
            feed.add(Box.createVerticalStrut(16));
        }
        JScrollPane scroll = new JScrollPane(feed);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);
    }
} 