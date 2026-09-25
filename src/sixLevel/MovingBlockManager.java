package sixLevel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Manages moving blocks in the maze.
 * Moving blocks are placed on path cells that are not dead ends (have at least 2 exits).
 * They move periodically to hinder player movement.
 */
public class MovingBlockManager {
    public static class MovingBlock {
        public int r, c;
        public int dirR, dirC; // direction of movement

        public MovingBlock(int r, int c, int dirR, int dirC) {
            this.r = r;
            this.c = c;
            this.dirR = dirR;
            this.dirC = dirC;
        }

        public void move(int rows, int cols, int[][] maze) {
            int nr = r + dirR;
            int nc = c + dirC;
            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && maze[nr][nc] != GameLogic.WALL) {
                r = nr;
                c = nc;
            } else {
                // reverse direction if blocked
                dirR = -dirR;
                dirC = -dirC;
            }
        }
    }

    private List<MovingBlock> blocks = new ArrayList<>();
    private int rows, cols;

    public void initialize(int rows, int cols, int[][] maze, int numBlocks) {
        this.rows = rows;
        this.cols = cols;
        blocks.clear();

        // Find valid positions: path cells with at least 2 exits (not dead ends)
        List<GameLogic.Position> validPositions = new ArrayList<>();
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};

        for (int r = 1; r < rows - 1; r++) {
            for (int c = 1; c < cols - 1; c++) {
                if (maze[r][c] == GameLogic.PATH) {
                    int exits = 0;
                    for (int d = 0; d < 4; d++) {
                        int nr = r + dr[d], nc = c + dc[d];
                        if (nr >= 0 && nr < rows && nc >= 0 && nc < cols &&
                            (maze[nr][nc] == GameLogic.PATH || maze[nr][nc] == GameLogic.EXIT)) {
                            exits++;
                        }
                    }
                    if (exits >= 2) {
                        validPositions.add(new GameLogic.Position(r, c));
                    }
                }
            }
        }

        // Place blocks randomly on valid positions
        java.util.Collections.shuffle(validPositions);
        for (int i = 0; i < Math.min(numBlocks, validPositions.size()); i++) {
            GameLogic.Position pos = validPositions.get(i);
            // Random initial direction
            int dirIndex = (int) (Math.random() * 4);
            int dirR = dr[dirIndex];
            int dirC = dc[dirIndex];
            blocks.add(new MovingBlock(pos.r, pos.c, dirR, dirC));
        }
    }

    public void moveBlocks(int[][] maze) {
        for (MovingBlock block : blocks) {
            block.move(rows, cols, maze);
        }
    }

    public List<MovingBlock> getBlocks() {
        return blocks;
    }

    public boolean isBlocked(int r, int c) {
        for (MovingBlock block : blocks) {
            if (block.r == r && block.c == c) {
                return true;
            }
        }
        return false;
    }

    public void clear() {
        blocks.clear();
    }
}
