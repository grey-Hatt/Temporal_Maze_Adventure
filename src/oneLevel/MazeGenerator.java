package oneLevel;

import java.util.Random;

/**
 * Simple maze generator / provider.
 * For reliability and to keep it deterministic for demo, we provide a fixed difficult maze,
 * plus a helper to return a copy.
 *
 * Legend: 0 = path, 1 = wall, 2 = exit
 */
public class MazeGenerator {
    public static final int PATH = 0;
    public static final int WALL = 1;
    public static final int EXIT = 2;

    private final int rows;
    private final int cols;
    private final int[][] baseMaze;
    private final Position start;

    public MazeGenerator(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        baseMaze = new int[rows][cols];

        // Hard-coded challenging layout (rows x cols must match)
        // 1 = wall, 0 = path, last cell is exit
        int[][] template = {
            {0,0,0,1,1,1,0,0,0,1,0,0,0,1,0,0,1,0,0},
            {1,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,1,1,0},
            {0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,1,0},
            {0,1,1,1,1,1,0,1,1,1,0,1,1,1,1,1,0,1,0},
            {0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,1,0,0,0},
            {1,1,0,1,0,1,1,1,0,1,1,1,1,1,0,1,1,1,0},
            {0,0,0,1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0},
            {0,1,1,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0},
            {0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {1,1,1,1,0,1,1,1,1,1,1,1,1,1,1,0,1,1,1},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
        };

        // Copy template into baseMaze (if sizes differ, fill with PATH)
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (r < template.length && c < template[0].length) {
                    baseMaze[r][c] = template[r][c];
                } else {
                    baseMaze[r][c] = PATH;
                }
            }
        }

        // Set a start point (top-left-ish) - find first PATH cell
        Position s = new Position(0, 0);
        outer:
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (baseMaze[r][c] == PATH) {
                    s = new Position(r, c);
                    break outer;
                }
            }
        }
        start = s;
    }

    public int[][] getMaze() {
        return getMazeCopy();
    }

    public int[][] getMazeCopy() {
        int[][] copy = new int[rows][cols];
        for (int r = 0; r < rows; r++) System.arraycopy(baseMaze[r], 0, copy[r], 0, cols);
        return copy;
    }

    public Position getStart() { return start; }
}
