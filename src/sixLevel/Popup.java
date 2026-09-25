package sixLevel;


import javax.swing.*;

/**
 * Simple popup intro describing features (called from Level6).
 */
public class Popup {
    public static void showIntro(JFrame parent) {
        String info = """
                Welcome to Level 6 — Time Traveler's Maze (20x20) — Finale

                • DSA used:
                  - Doubly Linked List: stores full path (replay/draw)
                  - Stack: undo last move (LIFO)
                  - Queue: coin/trap spawn order (FIFO)
                  - Dijkstra's Algorithm & A* Search: pathfinding hints
                  - AVL Tree: balanced leaderboard of top scores
                  - Moving Block Manager: dynamic maze obstacles

                • Controls: Arrow keys to move.
                • Buttons (right): Start, Undo, Replay, Reset, Exit
                • Goal: reach the green EXIT at bottom-right.

                Press Start to begin.
                """;
        JOptionPane.showMessageDialog(parent, info, "Level 6 - Briefing", JOptionPane.INFORMATION_MESSAGE);
    }
}

