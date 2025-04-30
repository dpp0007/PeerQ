package com.peerq.ui.components;

import com.peerq.model.Question;
import com.peerq.ui.UIConstants;
import com.peerq.ui.ViewQuestion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Component that displays a question as a card in the dashboard.
 */
public class QuestionCard extends JPanel {
    private final Question question;
    private final int currentUserId;
    
    /**
     * Constructor for creating a question card
     * 
     * @param question The Question object to display
     * @param currentUserId The ID of the currently logged in user
     */
    public QuestionCard(Question question, int currentUserId) {
        this.question = question;
        this.currentUserId = currentUserId;
        
        initializeUI();
    }
    
    /**
     * Sets up the UI components of the question card
     */
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIConstants.CARD_BG_COLOR);
        setBorder(UIConstants.CARD_BORDER);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Make the card clickable to view the full question
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ViewQuestion viewQuestion = new ViewQuestion(question.getId(), currentUserId);
                viewQuestion.setVisible(true);
                
                // Find the parent window and dispose it if it's not the main dashboard
                Component parent = SwingUtilities.getWindowAncestor(QuestionCard.this);
                if (parent instanceof JDialog) {
                    ((JDialog) parent).dispose();
                }
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(new Color(250, 250, 250));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(UIConstants.CARD_BG_COLOR);
            }
        });
        
        // Title panel (Title + Solved status)
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(question.getTitle());
        titleLabel.setFont(UIConstants.SUBTITLE_FONT);
        titleLabel.setForeground(UIConstants.TEXT_COLOR);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        if (question.isSolved()) {
            JLabel solvedLabel = new JLabel("[Solved]");
            solvedLabel.setFont(UIConstants.SMALL_FONT);
            solvedLabel.setForeground(UIConstants.SUCCESS_COLOR);
            titlePanel.add(solvedLabel, BorderLayout.EAST);
        }
        
        add(titlePanel, BorderLayout.NORTH);
        
        // Body preview
        JLabel bodyLabel = new JLabel("<html><body>" + question.getBodyPreview(150) + "</body></html>");
        bodyLabel.setFont(UIConstants.REGULAR_FONT);
        bodyLabel.setForeground(UIConstants.TEXT_COLOR);
        add(bodyLabel, BorderLayout.CENTER);
        
        // Footer panel (Tags, Posted by, Timestamp, Answer count)
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        
        // Tags on the left
        JPanel tagsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        tagsPanel.setOpaque(false);
        
        if (question.getTags() != null && !question.getTags().isEmpty()) {
            String[] tags = question.getTags().split(",");
            for (String tag : Arrays.stream(tags).limit(3).collect(Collectors.toList())) {
                JLabel tagLabel = new JLabel(tag.trim());
                tagLabel.setFont(UIConstants.SMALL_FONT);
                tagLabel.setForeground(UIConstants.PRIMARY_COLOR);
                tagLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UIConstants.PRIMARY_COLOR, 1, true),
                    BorderFactory.createEmptyBorder(2, 5, 2, 5)
                ));
                tagsPanel.add(tagLabel);
            }
            
            // If there are more tags than shown
            if (tags.length > 3) {
                JLabel moreLabel = new JLabel("+" + (tags.length - 3) + " more");
                moreLabel.setFont(UIConstants.SMALL_FONT);
                moreLabel.setForeground(UIConstants.LIGHT_TEXT_COLOR);
                tagsPanel.add(moreLabel);
            }
        }
        
        footerPanel.add(tagsPanel, BorderLayout.WEST);
        
        // Info panel on the right (Posted by, Timestamp, Answer count)
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        infoPanel.setOpaque(false);
        
        // Answer count
        JLabel answerCountLabel = new JLabel(question.getAnswerCount() + " answers");
        answerCountLabel.setFont(UIConstants.SMALL_FONT);
        answerCountLabel.setForeground(UIConstants.LIGHT_TEXT_COLOR);
        infoPanel.add(answerCountLabel);
        
        // Category
        if (question.getCategory() != null && !question.getCategory().isEmpty()) {
            JLabel categoryLabel = new JLabel(question.getCategory());
            categoryLabel.setFont(UIConstants.SMALL_FONT);
            categoryLabel.setForeground(UIConstants.SECONDARY_COLOR);
            infoPanel.add(categoryLabel);
        }
        
        // Posted by
        String postedBy = question.isAnonymous() ? "Anonymous" : question.getUserName();
        JLabel userLabel = new JLabel("Posted by " + postedBy);
        userLabel.setFont(UIConstants.SMALL_FONT);
        userLabel.setForeground(UIConstants.LIGHT_TEXT_COLOR);
        infoPanel.add(userLabel);
        
        // Timestamp (simplified)
        JLabel timestampLabel = new JLabel(formatTimestamp(question.getCreatedAt()));
        timestampLabel.setFont(UIConstants.SMALL_FONT);
        timestampLabel.setForeground(UIConstants.LIGHT_TEXT_COLOR);
        infoPanel.add(timestampLabel);
        
        footerPanel.add(infoPanel, BorderLayout.EAST);
        
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Formats a timestamp string for display
     * 
     * @param timestamp The timestamp string from the database
     * @return A formatted timestamp string (e.g., "2 days ago")
     */
    private String formatTimestamp(String timestamp) {
        if (timestamp == null) {
            return "";
        }
        
        // In a real application, you would parse the actual timestamp
        // and calculate the time difference to render "2 days ago", etc.
        // For simplicity, we'll just return the timestamp as is
        return timestamp;
    }
    
    @Override
    public Dimension getPreferredSize() {
        // Set preferred height for the card
        return new Dimension(super.getPreferredSize().width, 140);
    }
    
    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(getBackground());
        g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, UIConstants.BORDER_RADIUS, UIConstants.BORDER_RADIUS);
        
        g2d.dispose();
    }
}
