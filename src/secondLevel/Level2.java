package secondLevel;

import javax.swing.*;
import java.awt.*;

public class Level2 extends JFrame {

    private final JFrame previousDashboard;

    public Level2(JFrame previousDashboard) {
        this.previousDashboard = previousDashboard;
        setTitle("Time Traveler's Maze - Level 2");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Create the main game panel
        GamePanel panel = new GamePanel(previousDashboard);
        add(panel);

        // Full screen setup
        setUndecorated(false);  // keep title bar
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Pass screen size to panel
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        panel.setPreferredSize(screenSize);

        pack();
        setLocationRelativeTo(null);

        // Show popup describing Level 2 features
        SwingUtilities.invokeLater(() -> {
            String features = """
                    Level 2 - Futuristic Maze Challenge
                    • Larger Maze (20x20 grid)
                    • Doubly Linked List: track complete path
                    • Stack: undo previous moves
                    • Queue: manage coin & trap spawning
                    • Score Counter for collected coins
                    • Start, Undo, Replay, Reset, Exit buttons
                    • Background sounds (coin, trap, win)
                    • Fullscreen & clean UI for presentation
                    """;
            JOptionPane.showMessageDialog(this, features, "Level 2 Features", JOptionPane.INFORMATION_MESSAGE);
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Level2(null).setVisible(true));
    }
}
