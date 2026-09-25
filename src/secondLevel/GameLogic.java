package secondLevel;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * GameLogic holds maze, DSA containers (PathLinkedList, MoveStack, CoinTrapQueue)
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

    private final int rows = 20;
    private final int cols = 20;
    private final int[][] baseMaze = new int[rows][cols];

    // DSA structures:
    public static class PathLinkedList {
        public static class Node {
            public int r, c;
            public Node next, prev;
            public Node(int r, int c) { this.r=r; this.c=c; }
        }
        private Node head, tail;
        private int size = 0;
        public void clear() { head = tail = null; size = 0; }
        public void add(int r, int c) {
            Node n = new Node(r,c);
            if (head==null) head = tail = n; else { tail.next=n; n.prev=tail; tail=n; }
            size++;
        }
        public Node head() { return head; }
        public void removeLast() { if (tail==null) return; if (tail.prev==null) head=tail=null; else { tail=tail.prev; tail.next=null; } size=Math.max(0,size-1); }
        public int size() { return size; }
    }

    public static class MoveStack {
        private final Stack<Position> s = new Stack<>();
        public void push(Position p) { s.push(new Position(p.r,p.c)); }
        public Position pop() { return s.isEmpty() ? null : s.pop(); }
        public Position peek() { return s.isEmpty() ? null : s.peek(); }
        public int size() { return s.size(); }
        public void clear() { s.clear(); }
    }

    public static class CoinTrapQueue {
        public static class Item { public final int r,c,type; public Item(int r,int c,int t){this.r=r;this.c=c;this.type=t;} }
        private final Queue<Item> q = new LinkedList<>();
        public void enqueue(int r,int c,int t){ q.add(new Item(r,c,t)); }
        public Item dequeue(){ return q.isEmpty() ? null : q.poll(); }
        public void clear(){ q.clear(); }
        public int size(){ return q.size(); }
    }

    // Position helper
    public static class Position { public int r,c; public Position(int r,int c){this.r=r;this.c=c;} }

    // game state
    private final PathLinkedList path = new PathLinkedList();
    private final MoveStack stack = new MoveStack();
    private final CoinTrapQueue spawnQueue = new CoinTrapQueue();
    private int[][] maze; // working copy
    private int playerR, playerC;
    private int score = 0;

    public GameLogic() {
        buildStaticMaze();
        reset();
    }

    private void buildStaticMaze() {
        // fill with path
        for (int r=0;r<rows;r++) for (int c=0;c<cols;c++) baseMaze[r][c]=PATH;
        // add outer walls
        for (int r=0;r<rows;r++){ baseMaze[r][0]=WALL; baseMaze[r][cols-1]=WALL; }
        for (int c=0;c<cols;c++){ baseMaze[0][c]=WALL; baseMaze[rows-1][c]=WALL; }

        // A moderately difficult, hand-crafted pattern (corridors & dead ends)
        // We'll add several internal walls to make path twisty but ensure a path to exit.
        int[][] walls = {
                {1,2},{2,2},{3,2},{4,2},{5,2},{6,2},
                {3,4},{3,5},{3,6},{3,7},
                {6,4},{7,4},{8,4},{9,4},{10,4},
                {5,7},{6,7},{7,7},
                {10,2},{10,3},{10,4},{10,5},
                {12,6},{13,6},{14,6},{15,6},{16,6},
                {12,8},{12,9},{12,10},
                {2,10},{3,10},{4,10},{5,10},
                {14,11},{14,12},{14,13},
                {7,13},{8,13},{9,13},{10,13},{11,13},
                {16,15},{15,15},{14,15},
                {6,17},{7,17},{8,17},{9,17}
        };
        for (int[] w: walls) if (w[0]>=0 && w[0]<rows && w[1]>=0 && w[1]<cols) baseMaze[w[0]][w[1]] = WALL;

        // set exit at bottom-right (reachable)
        baseMaze[rows-2][cols-2] = PATH; // ensure near-exit path
        baseMaze[rows-1][cols-2] = PATH;
        baseMaze[rows-2][cols-1] = PATH;
        baseMaze[rows-1][cols-1] = EXIT;
    }

    public void reset() {
        // copy baseMaze to working maze
        maze = new int[rows][cols];
        for (int r=0;r<rows;r++) for (int c=0;c<cols;c++) maze[r][c]=baseMaze[r][c];

        // initial player start
        playerR = 1; playerC = 1;

        // reset DSAs
        path.clear(); stack.clear(); spawnQueue.clear();
        path.add(playerR, playerC);
        stack.push(new Position(playerR, playerC));
        score = 0;

        // prepare spawn plan (queue) with some coins/traps (FIFO)
        // ensure planned positions are on PATH cells
        int[][] planned = {
                {2,3,COIN},{4,3,COIN},{6,5,TRAP},{8,6,COIN},{9,2,TRAP},
                {11,8,COIN},{13,10,TRAP},{15,12,COIN},{17,14,TRAP}
        };
        for (int[] p: planned) {
            if (isInBounds(p[0],p[1]) && baseMaze[p[0]][p[1]]==PATH) spawnQueue.enqueue(p[0],p[1],p[2]);
        }
    }

    public int[][] getMazeCopy() {
        int[][] copy = new int[rows][cols];
        for (int r=0;r<rows;r++) for (int c=0;c<cols;c++) copy[r][c]=maze[r][c];
        return copy;
    }

    public int getRows(){ return rows; }
    public int getCols(){ return cols; }

    public int getCell(int r,int c){ return maze[r][c]; }

    public Position getPlayerPos(){ return new Position(playerR, playerC); }

    public int getScore(){ return score; }

    // attempt move; returns true if moved
    public boolean movePlayer(int dr,int dc) {
        int nr = playerR + dr, nc = playerC + dc;
        if (!isInBounds(nr,nc)) return false;
        if (maze[nr][nc] == WALL) return false;

        // perform move
        playerR = nr; playerC = nc;
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
            Position p2 = (p1!=null) ? p1 : new Position(1,1);
            if (p2!=null) { playerR = p2.r; playerC = p2.c; }
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
        if (stack.size() <= 1) return false;
        stack.pop(); // remove current
        Position prev = stack.peek();
        if (prev==null) return false;
        playerR = prev.r; playerC = prev.c;
        path.removeLast();
        return true;
    }

    // spawn next queued coin/trap into a random nearby PATH cell (demonstrates FIFO)
    public void spawnNext() {
        CoinTrapQueue.Item it = spawnQueue.dequeue();
        if (it==null) return;
        // try to place at given planned location if free, else random search
        if (isInBounds(it.r,it.c) && maze[it.r][it.c]==PATH && !(it.r==playerR && it.c==playerC)) {
            maze[it.r][it.c] = it.type;
            return;
        }
        // fallback random placement
        for (int attempt=0;attempt<100;attempt++) {
            int rr = 1 + (int)(Math.random()*(rows-2));
            int cc = 1 + (int)(Math.random()*(cols-2));
            if (maze[rr][cc]==PATH && !(rr==playerR && cc==playerC)) { maze[rr][cc]=it.type; return; }
        }
    }

    public PathLinkedList getPathList(){ return path; }
    public MoveStack getStack(){ return stack; }
    public CoinTrapQueue getSpawnQueue(){ return spawnQueue; }

    private boolean isInBounds(int r,int c){ return r>=0 && r<rows && c>=0 && c<cols; }
}
