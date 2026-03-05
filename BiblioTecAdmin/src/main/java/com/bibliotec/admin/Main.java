package com.bibliotec.admin;

import com.bibliotec.admin.ui.LoginFrame;
import com.bibliotec.admin.ui.MainFrame;
import com.bibliotec.admin.util.SessionManager;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class Main {

    public static MainFrame mainFrame;

    public static void main(String[] args) {
        // FlatLaf light theme
        try {
            FlatLightLaf.setup();
            UIManager.put("defaultFont", new javax.swing.plaf.FontUIResource("Segoe UI", java.awt.Font.PLAIN, 13));
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });
    }

    public static void showMain() {
        SwingUtilities.invokeLater(() -> {
            mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }

    public static void showLogin() {
        SwingUtilities.invokeLater(() -> {
            if (mainFrame != null) mainFrame.dispose();
            SessionManager.getInstance().clearSession();
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
        });
    }
}
