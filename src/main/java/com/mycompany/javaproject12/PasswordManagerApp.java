package com.mycompany.javaproject12;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class PasswordManagerApp {
    public static HashMap<String, String> passwordDatabase = new HashMap<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginScreen loginScreen = new LoginScreen();
            loginScreen.setVisible(true);
        });
    }

    // Encription function
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}

class LoginScreen extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginScreen() {
        setTitle("Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2));

        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        panel.add(new JLabel(""));
        loginButton = new JButton("Login");
        panel.add(loginButton);

        add(panel);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (username.equals("admin") && password.equals("admin")) {
                JOptionPane.showMessageDialog(this, "Login successful");
                dispose();
                new MainScreen().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid login credentials");
            }
        });
    }
}

class MainScreen extends JFrame {
    private JTextField accountField;
    private JPasswordField passwordField;
    private JButton saveButton, generateButton, showAllButton, editButton;

    public MainScreen() {
        setTitle("Password Manager");
        setSize(450, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        accountField = new JTextField();
        passwordField = new JPasswordField();
        saveButton = new JButton("Save");
        generateButton = new JButton("Generate Password");
        showAllButton = new JButton("Show All");
        editButton = new JButton("Edit Password");

        inputPanel.add(new JLabel("Account:"));
        inputPanel.add(accountField);
        inputPanel.add(new JLabel("Password:"));
        inputPanel.add(passwordField);
        inputPanel.add(saveButton);
        inputPanel.add(generateButton);
        inputPanel.add(showAllButton);
        inputPanel.add(editButton);

        add(inputPanel);

        // Save account with encripted password
        saveButton.addActionListener(e -> {
            String account = accountField.getText();
            String password = new String(passwordField.getPassword());

            if (!account.isEmpty() && !password.isEmpty()) {
                String hashed = PasswordManagerApp.hashPassword(password);
                PasswordManagerApp.passwordDatabase.put(account, hashed);
                JOptionPane.showMessageDialog(this, "Password saved (encrypted).");
            } else {
                JOptionPane.showMessageDialog(this, "Please enter both account and password.");
            }
        });

        // generate random password
        generateButton.addActionListener(e -> {
            String generated = generateRandomPassword();
            passwordField.setText(generated);
        });

        // Show all accounts 
        showAllButton.addActionListener(e -> {
            StringBuilder result = new StringBuilder("Stored Accounts:\n");
            for (String account : PasswordManagerApp.passwordDatabase.keySet()) {
                result.append(account).append(" → ").append(PasswordManagerApp.passwordDatabase.get(account)).append("\n");
            }
            JOptionPane.showMessageDialog(this, result.toString());
        });

        // Change password  
        editButton.addActionListener(e -> {
            String accountToEdit = JOptionPane.showInputDialog(this, "Enter the account to edit:");
            if (accountToEdit != null && PasswordManagerApp.passwordDatabase.containsKey(accountToEdit)) {
                String newPassword = JOptionPane.showInputDialog(this, "Enter new password:");
                if (newPassword != null && !newPassword.isEmpty()) {
                    String hashed = PasswordManagerApp.hashPassword(newPassword);
                    PasswordManagerApp.passwordDatabase.put(accountToEdit, hashed);
                    JOptionPane.showMessageDialog(this, "Password updated successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "New password cannot be empty.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Account not found.");
            }
        });
    }

    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            int index = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }
}
