package sixLevel;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import sixLevel.Difficulty;

public class Level6 extends JFrame {

    private final JFrame previousDashboard;
    private final Difficulty difficulty;

    public Level6(JFrame previousDashboard, Difficulty difficulty) {
        this.previousDashboard = previousDashboard;
        this.difficulty = difficulty;

        setTitle("Time Traveler's Maze - Level 6 (" + difficulty + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GamePanel panel = new GamePanel(previousDashboard, difficulty);
        add(panel);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        pack();
        setLocationRelativeTo(null);
        
        // show the briefing after the window is opened so the maze is visible underneath
        addWindowListener(new WindowAdapter() {
            private boolean shown = false;

            @Override
            public void windowOpened(WindowEvent e) {
                if (shown) return;
                shown = true;
                Popup.showIntro(Level6.this);
            }
        });
    
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Level6(null, Difficulty.MEDIUM).setVisible(true));
    }
}