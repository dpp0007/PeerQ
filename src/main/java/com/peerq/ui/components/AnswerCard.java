package com.peerq.ui.components;

import com.peerq.dao.AnswerDAO;
import com.peerq.dao.VoteDAO;
import com.peerq.model.Answer;
import com.peerq.ui.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Component that displays an answer as a card in the ViewQuestion screen.
 */
public class AnswerCard extends JPanel {
    private final Answer answer;
    private final int currentUserId;
    private final boolean isQuestionAuthor;
    private JLabel upvoteCountLabel;
    private JButton upvoteButton;
    private JButton acceptButton;
    private final AnswerDAO answerDAO;
    private final VoteDAO voteDAO;
    
    /**
     * Constructor for creating an answer card
     * 
     * @param answer The Answer object to display
     * @param currentUserId The ID of the currently logged in user
     * @param isQuestionAuthor Whether the current user is the author of the question
     */
    public AnswerCard(Answer answer, int currentUserId, boolean isQuestionAuthor) {
        this.answer = answer;
        this.currentUserId = currentUserId;
        this.isQuestionAuthor = isQuestionAuthor;
        this.answerDAO = new AnswerDAO();
        this.voteDAO = new VoteDAO();
        
        initializeUI();
    }
    
    /**
     * Sets up the UI components of the answer card
     */
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIConstants.CARD_BG_COLOR);
        setBorder(UIConstants.CARD_BORDER);
        
        // Left panel (voting)
        JPanel votingPanel = new JPanel();
        votingPanel.setLayout(new BoxLayout(votingPanel, BoxLayout.Y_AXIS));
        votingPanel.setOpaque(false);
        
        // Upvote button
        upvoteButton = new JButton("↑");
        upvoteButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        upvoteButton.setForeground(voteDAO.hasUserVoted(answer.getId(), currentUserId) ?
                UIConstants.ACCENT_COLOR : UIConstants.TEXT_COLOR);
        upvoteButton.setBorderPainted(false);
        upvoteButton.setContentAreaFilled(false);
        upvoteButton.setFocusPainted(false);
        upvoteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        upvoteButton.setToolTipText("Upvote this answer");
        upvoteButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Don't allow upvoting own answers
        if (answer.getUserId() == currentUserId) {
            upvoteButton.setEnabled(false);
            upvoteButton.setToolTipText("You cannot upvote your own answer");
        }
        
        upvoteButton.addActionListener((ActionEvent e) -> {
            if (!voteDAO.hasUserVoted(answer.getId(), currentUserId)) {
                boolean success = answerDAO.upvoteAnswer(answer.getId(), currentUserId);
                if (success) {
                    // Update UI
                    answer.setUpvotes(answer.getUpvotes() + 1);
                    upvoteCountLabel.setText(String.valueOf(answer.getUpvotes()));
                    upvoteButton.setForeground(UIConstants.ACCENT_COLOR);
                    JOptionPane.showMessageDialog(this, "Upvote recorded!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "You have already voted on this answer.",
                            "Cannot Vote", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "You have already voted on this answer.",
                        "Cannot Vote", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        votingPanel.add(upvoteButton);
        votingPanel.add(Box.createVerticalStrut(5));
        
        // Upvote count
        upvoteCountLabel = new JLabel(String.valueOf(answer.getUpvotes()), SwingConstants.CENTER);
        upvoteCountLabel.setFont(UIConstants.REGULAR_FONT);
        upvoteCountLabel.setForeground(UIConstants.TEXT_COLOR);
        upvoteCountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        votingPanel.add(upvoteCountLabel);
        votingPanel.add(Box.createVerticalStrut(10));
        
        // Accept answer button (only shown to question author and if the answer is not already accepted)
        if (isQuestionAuthor && !answer.isAccepted()) {
            acceptButton = new JButton("✓");
            acceptButton.setFont(new Font("SansSerif", Font.BOLD, 18));
            acceptButton.setForeground(UIConstants.TEXT_COLOR);
            acceptButton.setBorderPainted(false);
            acceptButton.setContentAreaFilled(false);
            acceptButton.setFocusPainted(false);
            acceptButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            acceptButton.setToolTipText("Accept this answer");
            acceptButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            acceptButton.addActionListener((ActionEvent e) -> {
                boolean success = answerDAO.markAnswerAsAccepted(answer.getId());
                if (success) {
                    // Update UI
                    answer.setAccepted(true);
                    acceptButton.setForeground(UIConstants.SUCCESS_COLOR);
                    acceptButton.setEnabled(false);
                    acceptButton.setToolTipText("This answer has been accepted");
                    
                    // Add accepted marker to the title
                    add(createAcceptedMarker(), BorderLayout.NORTH);
                    
                    JOptionPane.showMessageDialog(this, "Answer accepted!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Refresh the parent panel
                    Container parent = getParent();
                    if (parent != null) {
                        parent.revalidate();
                        parent.repaint();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Could not accept the answer. Please try again.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            votingPanel.add(acceptButton);
        } else if (answer.isAccepted()) {
            // Add accepted marker without the button
            add(createAcceptedMarker(), BorderLayout.NORTH);
        }
        
        add(votingPanel, BorderLayout.WEST);
        
        // Center panel (answer content)
        JPanel contentPanel = new JPanel(new BorderLayout(5, 10));
        contentPanel.setOpaque(false);
        
        JTextArea answerText = new JTextArea(answer.getContent());
        answerText.setFont(UIConstants.REGULAR_FONT);
        answerText.setForeground(UIConstants.TEXT_COLOR);
        answerText.setLineWrap(true);
        answerText.setWrapStyleWord(true);
        answerText.setEditable(false);
        answerText.setBackground(UIConstants.CARD_BG_COLOR);
        answerText.setBorder(BorderFactory.createEmptyBorder());
        
        contentPanel.add(answerText, BorderLayout.CENTER);
        
        // Footer panel (author, timestamp)
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setOpaque(false);
        
        // Posted by
        String postedBy = answer.isAnonymous() ? "Anonymous" : answer.getUserName();
        JLabel authorLabel = new JLabel("Posted by " + postedBy);
        authorLabel.setFont(UIConstants.SMALL_FONT);
        authorLabel.setForeground(UIConstants.LIGHT_TEXT_COLOR);
        footerPanel.add(authorLabel);
        
        // Timestamp
        JLabel timestampLabel = new JLabel(formatTimestamp(answer.getCreatedAt()));
        timestampLabel.setFont(UIConstants.SMALL_FONT);
        timestampLabel.setForeground(UIConstants.LIGHT_TEXT_COLOR);
        footerPanel.add(timestampLabel);
        
        contentPanel.add(footerPanel, BorderLayout.SOUTH);
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    /**
     * Creates a marker to show that an answer has been accepted
     * 
     * @return A JPanel containing the accepted marker
     */
    private JPanel createAcceptedMarker() {
        JPanel markerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        markerPanel.setOpaque(false);
        
        JLabel acceptedLabel = new JLabel("Accepted Answer ✓");
        acceptedLabel.setFont(UIConstants.SMALL_FONT);
        acceptedLabel.setForeground(UIConstants.SUCCESS_COLOR);
        acceptedLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.SUCCESS_COLOR, 1, true),
            BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));
        
        markerPanel.add(acceptedLabel);
        return markerPanel;
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
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(getBackground());
        g2d.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, UIConstants.BORDER_RADIUS, UIConstants.BORDER_RADIUS);
        
        g2d.dispose();
    }
}
