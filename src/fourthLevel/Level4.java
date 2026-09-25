package fourthLevel;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import fourthLevel.Difficulty;

public class Level4 extends JFrame {

    private final JFrame previousDashboard;
    private final Difficulty difficulty;

    public Level4(JFrame previousDashboard, Difficulty difficulty) {
        this.previousDashboard = previousDashboard;
        this.difficulty = difficulty;

        setTitle("Time Traveler's Maze - Level 4 (" + difficulty + ")");
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
                Popup.showIntro(Level4.this);
            }
        });
    
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Level4(null, Difficulty.MEDIUM).setVisible(true));
    }
}