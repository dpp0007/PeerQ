package com.peerq;

import com.peerq.desktopui.DesktopAppWindow;

public class DesktopMain {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            DesktopAppWindow app = new DesktopAppWindow();
            app.setVisible(true);
        });
    }
} 