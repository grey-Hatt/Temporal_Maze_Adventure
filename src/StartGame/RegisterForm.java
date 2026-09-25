package StartGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RegisterForm extends JFrame {
    private JFrame parent;

    public RegisterForm(JFrame parent) {
        this.parent = parent;
        setTitle("Register - Maze Adventure");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel panel = StyledUI.createBackgroundPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = StyledUI.createTitle("Create Account");
        JLabel userLabel = new JLabel("Username:");
        JTextField userField = StyledUI.createTextField(20);
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = StyledUI.createPasswordField(20);

        JButton backBtn = StyledUI.createButton("Back");
        JButton registerBtn = StyledUI.createButton("Register");

        userLabel.setForeground(Color.WHITE);
        passLabel.setForeground(Color.WHITE);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; panel.add(title, gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1; panel.add(userLabel, gbc);
        gbc.gridx = 1; panel.add(userField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; panel.add(passLabel, gbc);
        gbc.gridx = 1; panel.add(passField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; panel.add(backBtn, gbc);
        gbc.gridx = 1; panel.add(registerBtn, gbc);

        add(panel);

        // Back button
        backBtn.addActionListener(e -> {
            setVisible(false);
            parent.setVisible(true);
        });

        // Register button
        registerBtn.addActionListener(e -> {
            String user = userField.getText().trim();
            String pass = new String(passField.getPassword()).trim();

            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter username and password.");
                return;
            }

            if (UserData.userExists(user)) {
                JOptionPane.showMessageDialog(this, "That username is already taken. Please choose another.");
                return;
            }

            // Save to file using UserData
            UserData.saveUser(user, pass);
            JOptionPane.showMessageDialog(this, "User registered successfully!");
            dispose();
            parent.setVisible(true);
        });
    }
}
