package StartGame;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends JFrame {

    public MainMenu() {
        setTitle("Maze Adventure - Main Menu");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(MAXIMIZED_BOTH);
        // Styled background and center content
        JPanel bg = StyledUI.createBackgroundPanel();
        bg.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(16, 16, 16, 16);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel title = StyledUI.createTitle("Maze Adventure");
        title.setFont(new Font("Segoe UI", Font.BOLD, 40));

        JLabel subtitle = new JLabel("A DSA Maze Journey");
        subtitle.setForeground(new Color(220, 230, 255));
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        JButton loginBtn = StyledUI.createButton("Login");
        JButton registerBtn = StyledUI.createButton("Register");

        // arrange
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; bg.add(title, gbc);
        gbc.gridy = 1; bg.add(subtitle, gbc);
        gbc.gridy = 2; bg.add(Box.createVerticalStrut(18), gbc);
        gbc.gridwidth = 1;
        gbc.gridy = 3; gbc.gridx = 0; bg.add(loginBtn, gbc);
        gbc.gridx = 1; bg.add(registerBtn, gbc);

        // button actions
        loginBtn.addActionListener(e -> switchTo(new LoginForm(this)));
        registerBtn.addActionListener(e -> switchTo(new RegisterForm(this)));

        add(bg);
    }

    private void switchTo(JFrame frame) {
        frame.setVisible(true);
        this.setVisible(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenu().setVisible(true));
    }
}
