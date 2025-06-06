package com.peerq.desktopui;

import javax.swing.*;
import java.awt.*;

public class DesktopAppWindow extends JFrame {
    public DesktopAppWindow() {
        setTitle("PeerQ - Next Gen Desktop");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
        setUndecorated(false); // For now, keep window controls
        setLayout(new BorderLayout());

        // Sidebar (left)
        SidebarPanel sidebar = new SidebarPanel();
        add(sidebar, BorderLayout.WEST);

        // Workspace (center)
        WorkspacePanel workspace = new WorkspacePanel();
        add(workspace, BorderLayout.CENTER);

        // Right panel
        RightPanel rightPanel = new RightPanel();
        add(rightPanel, BorderLayout.EAST);
    }
} 