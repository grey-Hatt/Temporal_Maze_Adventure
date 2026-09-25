package thirdLevel;


import javax.swing.*;

/**
 * Simple popup intro describing features (called from Level3).
 */
public class Popup {
    public static void showIntro(JFrame parent) {
        String info = """
                Welcome to Level 3 — Time Traveler's Maze (20x20)

                • DSA used:
                  - Doubly Linked List: stores full path (replay/draw)
                  - Stack: undo last move (LIFO)
                  - Queue: coin/trap spawn order (FIFO)
                  - Selectable Difficulty: Easy / Medium / Hard

                • Controls: Arrow keys to move.
                • Buttons (right): Start, Undo, Replay, Reset, Exit
                • Goal: reach the green EXIT at bottom-right.

                Press Start to begin.
                """;
        JOptionPane.showMessageDialog(parent, info, "Level 3 - Briefing", JOptionPane.INFORMATION_MESSAGE);
    }
}

