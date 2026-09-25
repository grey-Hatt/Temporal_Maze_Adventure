package MainGame;

import javax.swing.*;

import oneLevel.Level1;
import secondLevel.Level2;
import thirdLevel.Level3;

import java.awt.*;
import java.net.URL;

public class Dashboard extends JFrame {

    private SoundPlayer bgMusic = new SoundPlayer();

    public Dashboard(String username) {

        setTitle("Welcome - " + username);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        getContentPane().setLayout(new BorderLayout());

        // ====== Start background music ======
        bgMusic.playLoop("audio.wav"); // Make sure audio.wav is inside MainGame folder

        // ===== Sidebar (20%) =====
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(30, 30, 30));
        sidebar.setPreferredSize(new Dimension((int) (Toolkit.getDefaultToolkit().getScreenSize().width * 0.2), 0));

        // ===== Logo (Enlarged) =====
        JLabel logo = new JLabel();
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        logo.setPreferredSize(new Dimension(200, 140)); // Bigger size

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("logo.png"));
            Image scaled = icon.getImage().getScaledInstance(180, 120, Image.SCALE_SMOOTH);
            logo.setIcon(new ImageIcon(scaled));
        } catch (Exception e) {
            logo.setText("LOGO");
            logo.setForeground(Color.WHITE);
            logo.setFont(new Font("Arial", Font.BOLD, 22));
        }

        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(200));

        // ===== Sidebar Buttons =====
        String[] options = { "HOME", "SETTING", "SOUND", "KEY", "CONTACT" };
        Color borderColor = new Color(255, 255, 255, 180);
        Font sideFont = new Font("Arial", Font.BOLD, 14);

        for (String opt : options) {
            JButton btn = new JButton(opt);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(300, 45));
            btn.setFont(sideFont);
            btn.setForeground(Color.WHITE);
            btn.setContentAreaFilled(false);
            btn.setOpaque(false);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setMargin(new Insets(10, 30, 10, 30));

            // Top & bottom borders only
            btn.setBorder(BorderFactory.createMatteBorder(2, 50, 2, 50, borderColor));

            // Hover effect
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    btn.setBorder(BorderFactory.createMatteBorder(3, 60, 3, 60, Color.CYAN));
                    btn.setForeground(new Color(180, 255, 255));
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    btn.setBorder(BorderFactory.createMatteBorder(2, 50, 2, 50, borderColor));
                    btn.setForeground(Color.WHITE);
                }
            });

            btn.addActionListener(e ->
                JOptionPane.showMessageDialog(this, opt + " is coming soon!", "Coming Soon", JOptionPane.INFORMATION_MESSAGE));

            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(10));
        }

        // ===== Documentation Button (same style) =====
        sidebar.add(Box.createVerticalGlue());
        JButton docBtn = new JButton("Documentation");
        docBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        docBtn.setMaximumSize(new Dimension(300, 45));
        docBtn.setFont(sideFont);
        docBtn.setForeground(Color.WHITE);
        docBtn.setContentAreaFilled(false);
        docBtn.setOpaque(false);
        docBtn.setFocusPainted(false);
        docBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        docBtn.setMargin(new Insets(10, 30, 10, 30));
        docBtn.setBorder(BorderFactory.createMatteBorder(2, 50, 2, 50, borderColor));

        docBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                docBtn.setBorder(BorderFactory.createMatteBorder(3, 60, 3, 60, Color.CYAN));
                docBtn.setForeground(new Color(180, 255, 255));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                docBtn.setBorder(BorderFactory.createMatteBorder(2, 50, 2, 50, borderColor));
                docBtn.setForeground(Color.WHITE);
            }
        });

        docBtn.addActionListener(e ->
            JOptionPane.showMessageDialog(this, "Documentation is coming soon!", "Coming Soon", JOptionPane.INFORMATION_MESSAGE));

        sidebar.add(docBtn);
        sidebar.add(Box.createVerticalStrut(20));

        // ===== Main Content (80%) with GIF background =====
        JPanel mainPanel = new JPanel(new BorderLayout());
        AnimatedGIFPanel gifPanel = new AnimatedGIFPanel("background.gif");
        gifPanel.setLayout(new GridBagLayout()); // So buttons are centered
        mainPanel.add(gifPanel, BorderLayout.CENTER);

        // ===== Level Buttons (unchanged) =====
        JButton level1Btn = new JButton("Level 1 - Ancient Labyrinth");
        JButton level2Btn = new JButton("Level 2 - The Cyber Future Maze");
        JButton level3Btn = new JButton("Level 3 - The Random Rift Maze...");
        JButton level4Btn = new JButton("Level 4 - The 2D Temporal Rift Maze");
        JButton level5Btn = new JButton("Level 5 - 3D MAZE KINGDOM OF Justice__");
        JButton level6Btn = new JButton("Level 6 - A 360 DSA MAZE KINGDOM ( FINALE..)");

        Font btnFont = new Font("Arial", Font.BOLD, 25);
        Color btnColor = new Color(255, 255, 255, 220);
        Color textColor = Color.BLACK;

        JButton[] levelButtons = { level1Btn, level2Btn, level3Btn, level4Btn, level5Btn, level6Btn };
        int[] widths = { 350, 410, 480, 550, 620, 680 }; // custom widths

        for (int i = 0; i < levelButtons.length; i++) {
            JButton btn = levelButtons[i];
            btn.setFont(btnFont);
            btn.setBackground(btnColor);
            btn.setForeground(textColor);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3, true));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);

            Dimension fixedSize = new Dimension(widths[i], 60);
            btn.setPreferredSize(fixedSize);
            btn.setMaximumSize(fixedSize);
            btn.setMinimumSize(fixedSize);
        }

        // ===== Use BoxLayout + Centering for Level Buttons =====
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(level1Btn);
        buttonPanel.add(Box.createVerticalStrut(30));
        buttonPanel.add(level2Btn);
        buttonPanel.add(Box.createVerticalStrut(30));
        buttonPanel.add(level3Btn);
        buttonPanel.add(Box.createVerticalStrut(30));
        buttonPanel.add(level4Btn);
        buttonPanel.add(Box.createVerticalStrut(30));
        buttonPanel.add(level5Btn);
        buttonPanel.add(Box.createVerticalStrut(30));
        buttonPanel.add(level6Btn);
        buttonPanel.add(Box.createVerticalStrut(30));


        // add title above buttons
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("DSA TEMPORAL MAZE Kingdom");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 48));

        centerPanel.add(title);
        centerPanel.add(Box.createVerticalStrut(30));
        centerPanel.add(buttonPanel);

        gifPanel.add(centerPanel, new GridBagConstraints());

        // ===== Button Actions =====
        level1Btn.addActionListener(e -> {
            // hide dashboard and open level1
            setVisible(false);
            new Level1().setVisible(true);
        });

        level2Btn.addActionListener(e -> {
            // hide dashboard and open level2, pass this dashboard as parent
            setVisible(false);
            new Level2(this).setVisible(true);
        });




        level3Btn.addActionListener(e -> {

            String[] difficultyOptions = { "Easy", "Medium", "Hard" };
            int choice = JOptionPane.showOptionDialog(
                    this,
                    "Select Maze Difficulty",
                    "Difficulty Selection",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    difficultyOptions,
                    difficultyOptions[0]);

            if (choice == -1)
                return; // user closed dialog

            thirdLevel.Difficulty diff = choice == 0 ? thirdLevel.Difficulty.EASY
                    : choice == 1 ? thirdLevel.Difficulty.MEDIUM : thirdLevel.Difficulty.HARD;

            setVisible(false);
            new Level3(this, diff).setVisible(true);
        });



        level4Btn.addActionListener(e -> {
            String[] difficultyOptions = { "Easy", "Medium", "Hard" };
            int choice = JOptionPane.showOptionDialog(
                this,
                "Select Maze Difficulty",
                "Difficulty Selection",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                difficultyOptions,
                difficultyOptions[0]);

            if (choice == -1)
            return; // user closed dialog

            fourthLevel.Difficulty diff = choice == 0 ? fourthLevel.Difficulty.EASY
                : choice == 1 ? fourthLevel.Difficulty.MEDIUM : fourthLevel.Difficulty.HARD;

            setVisible(false);
            new fourthLevel.Level4(this, diff).setVisible(true);
        });



        level5Btn.addActionListener(e -> {
            String[] difficultyOptions = { "Easy", "Medium", "Hard" };
            int choice = JOptionPane.showOptionDialog(
                this,
                "Select Maze Difficulty",
                "Difficulty Selection",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                difficultyOptions,
                difficultyOptions[0]);

            if (choice == -1)
            return; // user closed dialog

            fifthLevel.Difficulty diff = choice == 0 ? fifthLevel.Difficulty.EASY
                : choice == 1 ? fifthLevel.Difficulty.MEDIUM : fifthLevel.Difficulty.HARD;

            setVisible(false);
            new fifthLevel.Level5(this, diff).setVisible(true);
        });



        level6Btn.addActionListener(e -> {
            String[] difficultyOptions = { "Easy", "Medium", "Hard" };
            int choice = JOptionPane.showOptionDialog(
                this,
                "Select Maze Difficulty",
                "Difficulty Selection",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                difficultyOptions,
                difficultyOptions[0]);

            if (choice == -1)
            return; // user closed dialog

            sixLevel.Difficulty diff = choice == 0 ? sixLevel.Difficulty.EASY
                : choice == 1 ? sixLevel.Difficulty.MEDIUM : sixLevel.Difficulty.HARD;

            setVisible(false);
            new sixLevel.Level6(this, diff).setVisible(true);
        });





        // ===== Add to Frame =====
        getContentPane().add(sidebar, BorderLayout.WEST);
        getContentPane().add(mainPanel, BorderLayout.CENTER);

        // Stop music when window closes
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                bgMusic.stop();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Dashboard("Abdullah").setVisible(true));
    }
}

/**
 * Custom panel that stretches animated GIF to full size
 */
class AnimatedGIFPanel extends JPanel {
    private ImageIcon gifIcon;

    public AnimatedGIFPanel(String gifPath) {
        setLayout(null);
        setBackground(Color.BLACK);
        try {
            URL gifURL = getClass().getResource(gifPath);
            if (gifURL != null) {
                gifIcon = new ImageIcon(gifURL);
                new Timer(40, e -> repaint()).start(); // Animate GIF
            }
        } catch (Exception e) {
            gifIcon = null;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (gifIcon != null) {
            g.drawImage(gifIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("GIF not found", getWidth() / 2 - 100, getHeight() / 2);
        }
    }
}
