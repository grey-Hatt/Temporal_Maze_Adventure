package StartGame;

import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JFrame {
    public SplashScreen() {
        setTitle("Maze Adventure");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(MAXIMIZED_BOTH);

        JPanel bg = StyledUI.createBackgroundPanel();
        bg.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel title = new JLabel("Maze Adventure");
        title.setFont(new Font("Segoe UI", Font.BOLD, 56));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("A DSA Maze Journey");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        subtitle.setForeground(new Color(220, 230, 255));

        JButton startBtn = StyledUI.createButton("Start");
        JButton exitBtn = StyledUI.createButton("Exit");

        JPanel btnRow = new JPanel();
        btnRow.setOpaque(false);
        btnRow.add(startBtn);
        btnRow.add(Box.createHorizontalStrut(24));
        btnRow.add(exitBtn);

        gbc.gridx = 0; gbc.gridy = 0; bg.add(title, gbc);
        gbc.gridy = 1; bg.add(subtitle, gbc);
        gbc.gridy = 2; bg.add(Box.createVerticalStrut(20), gbc);
        gbc.gridy = 3; bg.add(btnRow, gbc);

        add(bg);

        startBtn.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                new MainMenu().setVisible(true);
            });
            dispose();
        });

        exitBtn.addActionListener(e -> System.exit(0));
    }
}
