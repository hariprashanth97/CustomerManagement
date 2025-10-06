package com.example.app;

import com.example.view.CustomerPanel;

import javax.swing.*;

public class CustomerManagementApp extends JFrame {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // set system look and feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new CustomerPanel().setVisible(true);
        });
    }
}
