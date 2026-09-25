package thirdLevel;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * GameLogic holds maze, DSA containers (PathLinkedList, MoveStack,
 * CoinTrapQueue)
 * and exposes methods for movement, undo, spawn etc.
 *
 * Simple static 20x20 maze (moderately difficult but solvable).
 */
public class GameLogic {
    public static final int PATH = 0;
    public static final int WALL = 1;
    public static final int COIN = 2;
    public static final int TRAP = 3;
    public static final int EXIT = 4;

    // private final int rows = 20;
    // private final int cols = 20;
    // private final int[][] baseMaze = new int[rows][cols];
    private int rows, cols;
    private int[][] baseMaze;

    // DSA structures:
    public static class PathLinkedList {
        public static class Node {
            public int r, c;
            public Node next, prev;

            public Node(int r, int c) {
                this.r = r;
                this.c = c;
            }
        }

        private Node head, tail;
        private int size = 0;

        public void clear() {
            head = tail = null;
            size = 0;
        }

        public void add(int r, int c) {
            Node n = new Node(r, c);
            if (head == null)
                head = tail = n;
            else {
                tail.next = n;
                n.prev = tail;
                tail = n;
            }
            size++;
        }

        public Node head() {
            return head;
        }

        public void removeLast() {
            if (tail == null)
                return;
            if (tail.prev == null)
                head = tail = null;
            else {
                tail = tail.prev;
                tail.next = null;
            }
            size = Math.max(0, size - 1);
        }

        public int size() {
            return size;
        }
    }

    public static class MoveStack {
        private final Stack<Position> s = new Stack<>();

        public void push(Position p) {
            s.push(new Position(p.r, p.c));
        }

        public Position pop() {
            return s.isEmpty() ? null : s.pop();
        }

        public Position peek() {
            return s.isEmpty() ? null : s.peek();
        }

        public int size() {
            return s.size();
        }

        public void clear() {
            s.clear();
        }
    }

    public static class CoinTrapQueue {
        public static class Item {
            public final int r, c, type;

            public Item(int r, int c, int t) {
                this.r = r;
                this.c = c;
                this.type = t;
            }
        }

        private final Queue<Item> q = new LinkedList<>();

        public void enqueue(int r, int c, int t) {
            q.add(new Item(r, c, t));
        }

        public Item dequeue() {
            return q.isEmpty() ? null : q.poll();
        }

        public void clear() {
            q.clear();
        }

        public int size() {
            return q.size();
        }
    }

    // Position helper
    public static class Position {
        public int r, c;

        public Position(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }

    // game state
    private final PathLinkedList path = new PathLinkedList();
    private final MoveStack stack = new MoveStack();
    private final CoinTrapQueue spawnQueue = new CoinTrapQueue();
    private int[][] maze; // working copy
    private int playerR, playerC;
    private int score = 0;

    public GameLogic() {
        // Initialize with a sensible default difficulty to avoid calling
        // methods that require rows/cols before they're set.
        reset(Difficulty.MEDIUM);
    }

    private void buildStaticMaze() {
        // fill with path
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                baseMaze[r][c] = PATH;
        // add outer walls
        for (int r = 0; r < rows; r++) {
            baseMaze[r][0] = WALL;
            baseMaze[r][cols - 1] = WALL;
        }
        for (int c = 0; c < cols; c++) {
            baseMaze[0][c] = WALL;
            baseMaze[rows - 1][c] = WALL;
        }

        // A moderately difficult, hand-crafted pattern (corridors & dead ends)
        // We'll add several internal walls to make path twisty but ensure a path to
        // exit.
        int[][] walls = {
                { 1, 2 }, { 2, 2 }, { 3, 2 }, { 4, 2 }, { 5, 2 }, { 6, 2 },
                { 3, 4 }, { 3, 5 }, { 3, 6 }, { 3, 7 },
                { 6, 4 }, { 7, 4 }, { 8, 4 }, { 9, 4 }, { 10, 4 },
                { 5, 7 }, { 6, 7 }, { 7, 7 },
                { 10, 2 }, { 10, 3 }, { 10, 4 }, { 10, 5 },
                { 12, 6 }, { 13, 6 }, { 14, 6 }, { 15, 6 }, { 16, 6 },
                { 12, 8 }, { 12, 9 }, { 12, 10 },
                { 2, 10 }, { 3, 10 }, { 4, 10 }, { 5, 10 },
                { 14, 11 }, { 14, 12 }, { 14, 13 },
                { 7, 13 }, { 8, 13 }, { 9, 13 }, { 10, 13 }, { 11, 13 },
                { 16, 15 }, { 15, 15 }, { 14, 15 },
                { 6, 17 }, { 7, 17 }, { 8, 17 }, { 9, 17 }
        };
        for (int[] w : walls)
            if (w[0] >= 0 && w[0] < rows && w[1] >= 0 && w[1] < cols)
                baseMaze[w[0]][w[1]] = WALL;

        // set exit at bottom-right (reachable)
        baseMaze[rows - 2][cols - 2] = PATH; // ensure near-exit path
        baseMaze[rows - 1][cols - 2] = PATH;
        baseMaze[rows - 2][cols - 1] = PATH;
        baseMaze[rows - 1][cols - 1] = EXIT;
    }

    private void generateRandomMaze() {

        baseMaze = new int[rows][cols];

        // fill walls
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                baseMaze[r][c] = WALL;

        boolean[][] visited = new boolean[rows][cols];
        Stack<Position> dfs = new Stack<>();

        dfs.push(new Position(1, 1));
        visited[1][1] = true;
        baseMaze[1][1] = PATH;

        int[] dr = { -2, 2, 0, 0 };
        int[] dc = { 0, 0, -2, 2 };

        while (!dfs.isEmpty()) {
            Position cur = dfs.peek();

            java.util.List<Integer> dirs = new java.util.ArrayList<>();
            for (int i = 0; i < 4; i++)
                dirs.add(i);
            java.util.Collections.shuffle(dirs);

            boolean moved = false;
            for (int i : dirs) {
                int nr = cur.r + dr[i];
                int nc = cur.c + dc[i];

                if (nr > 0 && nc > 0 && nr < rows - 1 && nc < cols - 1 && !visited[nr][nc]) {
                    visited[nr][nc] = true;
                    baseMaze[cur.r + dr[i] / 2][cur.c + dc[i] / 2] = PATH;
                    baseMaze[nr][nc] = PATH;
                    dfs.push(new Position(nr, nc));
                    moved = true;
                    break;
                }
            }
            if (!moved)
                dfs.pop();
        }

        // exit
        baseMaze[rows - 2][cols - 2] = EXIT;
        // Ensure exit is reachable from the start (1,1). If not, carve
        // a short connecting corridor from the nearest reachable cell.
        boolean[][] vis = new boolean[rows][cols];
        java.util.Queue<Position> q = new java.util.LinkedList<>();
        if (baseMaze[1][1] == PATH) {
            vis[1][1] = true;
            q.add(new Position(1, 1));
        }
        int[] ddr = { -1, 1, 0, 0 };
        int[] ddc = { 0, 0, -1, 1 };
        boolean exitReachable = false;
        while (!q.isEmpty()) {
            Position p = q.poll();
            if (p.r == rows - 2 && p.c == cols - 2) {
                exitReachable = true;
                break;
            }
            for (int i = 0; i < 4; i++) {
                int nr = p.r + ddr[i], nc = p.c + ddc[i];
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && !vis[nr][nc]) {
                    // treat EXIT as passable for BFS
                    if (baseMaze[nr][nc] == PATH || baseMaze[nr][nc] == EXIT) {
                        vis[nr][nc] = true;
                        q.add(new Position(nr, nc));
                    }
                }
            }
        }

        if (!exitReachable) {
            // find nearest reachable cell to exit
            Position best = null;
            int bestDist = Integer.MAX_VALUE;
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    if (vis[r][c]) {
                        int d = Math.abs(r - (rows - 2)) + Math.abs(c - (cols - 2));
                        if (d < bestDist) {
                            bestDist = d;
                            best = new Position(r, c);
                        }
                    }
                }
            }
            if (best == null) {
                // no reachable cell found (very unlikely) — connect from (1,1)
                best = new Position(1, 1);
            }

            // carve a simple Manhattan path from best to exit
            int r = best.r;
            int c = best.c;
            int tr = rows - 2, tc = cols - 2;
            while (r != tr || c != tc) {
                if (r < tr) r++; else if (r > tr) r--; else if (c < tc) c++; else if (c > tc) c--;
                if (baseMaze[r][c] == WALL) baseMaze[r][c] = PATH;
            }
            // finally set exit cell
            baseMaze[tr][tc] = EXIT;
        }
    }

    private void prepareSpawnQueue() {
        for (int i = 0; i < rows; i += 3) {
            for (int j = 0; j < cols; j += 4) {
                if (baseMaze[i][j] == PATH) {
                    spawnQueue.enqueue(i, j, Math.random() > 0.6 ? TRAP : COIN);
                }
            }
        }
    }

    public void reset(Difficulty difficulty) {

        switch (difficulty) {
            case EASY -> {
                rows = 15;
                cols = 15;
            }
            case MEDIUM -> {
                rows = 20;
                cols = 20;
            }
            case HARD -> {
                rows = 25;
                cols = 25;
            }
        }

        generateRandomMaze();

        maze = new int[rows][cols];
        for (int r = 0; r < rows; r++)
            System.arraycopy(baseMaze[r], 0, maze[r], 0, cols);

        playerR = 1;
        playerC = 1;

        path.clear();
        stack.clear();
        spawnQueue.clear();

        path.add(playerR, playerC);
        stack.push(new Position(playerR, playerC));
        score = 0;

        prepareSpawnQueue();
    }

    /**
     * Reset only player position, path, score and spawn queue WITHOUT regenerating the
     * base maze. Useful for Start/Reset buttons that should not change maze design.
     */
    public void resetPlayerState() {
        // restore working copy from baseMaze
        if (baseMaze == null) return;
        maze = new int[rows][cols];
        for (int r = 0; r < rows; r++)
            System.arraycopy(baseMaze[r], 0, maze[r], 0, cols);

        playerR = 1;
        playerC = 1;

        path.clear();
        stack.clear();
        spawnQueue.clear();

        path.add(playerR, playerC);
        stack.push(new Position(playerR, playerC));
        score = 0;

        prepareSpawnQueue();
    }

    public int[][] getMazeCopy() {
        int[][] copy = new int[rows][cols];
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                copy[r][c] = maze[r][c];
        return copy;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getCell(int r, int c) {
        return maze[r][c];
    }

    public Position getPlayerPos() {
        return new Position(playerR, playerC);
    }

    public int getScore() {
        return score;
    }

    // attempt move; returns true if moved
    public boolean movePlayer(int dr, int dc) {
        int nr = playerR + dr, nc = playerC + dc;
        if (!isInBounds(nr, nc))
            return false;
        if (maze[nr][nc] == WALL)
            return false;

        // perform move
        playerR = nr;
        playerC = nc;
        path.add(playerR, playerC);
        stack.push(new Position(playerR, playerC));

        // interactions
        if (maze[playerR][playerC] == COIN) {
            score += 10;
            maze[playerR][playerC] = PATH;
            SoundManager.play("coin.wav");
        } else if (maze[playerR][playerC] == TRAP) {
            score = Math.max(0, score - 15);
            maze[playerR][playerC] = PATH; // clear trap after triggered
            // send player back 2 steps (instead of full reset) if possible
            stack.pop(); // remove current
            Position p1 = stack.pop(); // step back 1
            Position p2 = (p1 != null) ? p1 : new Position(1, 1);
            if (p2 != null) {
                playerR = p2.r;
                playerC = p2.c;
            }
            SoundManager.play("trap.wav");
            // push current position to stack so undo still works
            stack.push(new Position(playerR, playerC));
            path.add(playerR, playerC);
        } else if (maze[playerR][playerC] == EXIT) {
            SoundManager.play("win.wav");
        }
        return true;
    }

    // Undo: pop last move and set player to previous position
    public boolean undoLast() {
        if (stack.size() <= 1)
            return false;
        stack.pop(); // remove current
        Position prev = stack.peek();
        if (prev == null)
            return false;
        playerR = prev.r;
        playerC = prev.c;
        path.removeLast();
        return true;
    }

    // spawn next queued coin/trap into a random nearby PATH cell (demonstrates
    // FIFO)
    public void spawnNext() {
        CoinTrapQueue.Item it = spawnQueue.dequeue();
        if (it == null)
            return;
        // try to place at given planned location if free, else random search
        if (isInBounds(it.r, it.c) && maze[it.r][it.c] == PATH && !(it.r == playerR && it.c == playerC)) {
            maze[it.r][it.c] = it.type;
            return;
        }
        // fallback random placement
        for (int attempt = 0; attempt < 100; attempt++) {
            int rr = 1 + (int) (Math.random() * (rows - 2));
            int cc = 1 + (int) (Math.random() * (cols - 2));
            if (maze[rr][cc] == PATH && !(rr == playerR && cc == playerC)) {
                maze[rr][cc] = it.type;
                return;
            }
        }
    }

    public PathLinkedList getPathList() {
        return path;
    }

    public MoveStack getStack() {
        return stack;
    }

    public CoinTrapQueue getSpawnQueue() {
        return spawnQueue;
    }

    private boolean isInBounds(int r, int c) {
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }
}
