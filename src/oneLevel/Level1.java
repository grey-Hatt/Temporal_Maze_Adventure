package oneLevel;

import javax.swing.*;
import java.awt.*;


public class Level1 extends JFrame {

    public Level1() {
        setTitle("Time Traveler's Maze - Level 1");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        GamePanel panel = new GamePanel(this);
        add(panel);

        // Full screen setup
        setUndecorated(false);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // pass screen size to panel
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        panel.setPreferredSize(screenSize);

        pack();
        setLocationRelativeTo(null);

        // show popup describing features
        SwingUtilities.invokeLater(() -> {
            String features = """
                    Level 1 - Ancient Labyrinth (Short Timer)
                    • Short Timer (60 seconds)
                    • Linked List: path memory & replay
                    • Stack: undo last moves
                    • Queue: replay playback
                    • Start, Undo, Replay, Reset, Exit (Exit -> Dashboard)
                    • Fullscreen, scaled maze
                    """;
            JOptionPane.showMessageDialog(this, features, "Level 1 Features", JOptionPane.INFORMATION_MESSAGE);
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Level1::new);
    }
}
