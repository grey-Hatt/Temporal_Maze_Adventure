package oneLevel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import MainGame.Dashboard;

public class GamePanel extends JPanel {

    private final JFrame parentFrame;
    private final int rows = 13;
    private final int cols = 19;
    private int tileSize;
    private int startX, startY; // top-left of maze drawing

    // Maze & logic
    private final MazeGenerator generator;
    private int[][] maze; // 0=path,1=wall,2=exit
    private final Player player;
    private final PathLinkedList pathList;
    private final MoveStack moveStack;
    private final ReplayQueue replayQueue;

    // Controls
    private final JButton startBtn = new JButton("Start");
    private final JButton undoBtn = new JButton("Undo");
    private final JButton replayBtn = new JButton("Replay");
    private final JButton resetBtn = new JButton("Reset");
    private final JButton exitBtn = new JButton("Exit");
    private final JLabel scoreLabel = new JLabel("Score: 0");
    private final JLabel timerLabel = new JLabel("Time: 60");

    // State
    private boolean running = false;
    private int score = 0;
    private final Timer gameTimer;      // countdown
    private int timeLeft = 60;          // short timer: 60 seconds
    private Timer replayTimer;          // for animating replay
    private Position previewPos = null; // used during replay

    public GamePanel(JFrame parent) {
        this.parentFrame = parent;
        setLayout(null); // absolute for side controls
        setBackground(Color.DARK_GRAY);

        generator = new MazeGenerator(rows, cols);
        maze = generator.getMaze();

        // initialize DSA objects
        pathList = new PathLinkedList();
        moveStack = new MoveStack();
        replayQueue = new ReplayQueue();

        // initial player at start returned by generator
        Position start = generator.getStart();
        player = new Player(start.row, start.col);

        // UI placement (we will compute tileSize in paintComponent, but add buttons now)
        setupSideControls();

        // Timer (1 second tick)
        gameTimer = new Timer(1000, e -> {
            if (!running) return;
            timeLeft--;
            timerLabel.setText("Time: " + timeLeft);
            if (timeLeft <= 0) {
                gameOver("Time's up!");
            }
        });

        // Key listener for movement
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!running) return;
                int key = e.getKeyCode();
                int dr = 0, dc = 0;
                switch (key) {
                    case KeyEvent.VK_UP -> dr = -1;
                    case KeyEvent.VK_DOWN -> dr = 1;
                    case KeyEvent.VK_LEFT -> dc = -1;
                    case KeyEvent.VK_RIGHT -> dc = 1;
                    default -> { return; }
                }
                attemptMove(dr, dc);
            }
        });

        // ensure initial path/stack store starting position
        resetGameState();

        // make sure buttons get focus when clicked
        addFocusListenersToButtons();
    }

    private void setupSideControls() {
        // compute a side panel width (will be placed relative to screen width in paintComponent)
        int w = 180;
        int margin = 20;

        startBtn.setBounds(0, 0, w, 40);
        undoBtn.setBounds(0, 50, w, 40);
        replayBtn.setBounds(0, 100, w, 40);
        resetBtn.setBounds(0, 150, w, 40);
        exitBtn.setBounds(0, 200, w, 40);
        scoreLabel.setBounds(0, 260, w, 30);
        timerLabel.setBounds(0, 300, w, 30);

        // button actions
        startBtn.addActionListener(e -> startGame());
        undoBtn.addActionListener(e -> undoMove());
        replayBtn.addActionListener(e -> startReplay());
        resetBtn.addActionListener(e -> resetGame());
        exitBtn.addActionListener(e -> exitToDashboard());

        // initial enabling
        undoBtn.setEnabled(false);
        replayBtn.setEnabled(false);
        resetBtn.setEnabled(true);

        // add to panel (we will translate them in paintComponent based on startX)
        add(startBtn);
        add(undoBtn);
        add(replayBtn);
        add(resetBtn);
        add(exitBtn);
        add(scoreLabel);
        add(timerLabel);
    }

    private void addFocusListenersToButtons() {
        for (Component c : getComponents()) {
            if (c instanceof JButton) {
                c.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        requestFocusInWindow();
                    }
                });
            }
        }
    }

    private void resetGameState() {
        maze = generator.getMazeCopy(); // fresh
        Position s = generator.getStart();
        player.setPosition(s.row, s.col);
        pathList.clear();
        moveStack.clear();
        replayQueue.clear();
        pathList.addMove(player.getRow(), player.getCol());
        moveStack.push(new Position(player.getRow(), player.getCol()));
        score = 0;
        scoreLabel.setText("Score: " + score);
        timeLeft = 60; // short timer
        timerLabel.setText("Time: " + timeLeft);
        running = false;
        previewPos = null;
        if (gameTimer.isRunning()) gameTimer.stop();
        if (replayTimer != null && replayTimer.isRunning()) replayTimer.stop();
    }

    private void startGame() {
        resetGameState(); // start fresh
        running = true;
        gameTimer.start();
        undoBtn.setEnabled(true);
        replayBtn.setEnabled(true);
        requestFocusInWindow();
        repaint();
    }

    private void undoMove() {
        if (!running) return;
        if (moveStack.size() <= 1) return; // can't undo start
        moveStack.pop(); // remove current
        Position prev = moveStack.peek();
        if (prev != null) {
            pathList.removeLast();
            player.setPosition(prev.row, prev.col);
        }
        score = Math.max(0, score - 2); // small penalty
        scoreLabel.setText("Score: " + score);
        repaint();
    }

    private void attemptMove(int dr, int dc) {
        int nr = player.getRow() + dr;
        int nc = player.getCol() + dc;
        if (!isValid(nr, nc)) return;
        if (maze[nr][nc] == MazeGenerator.WALL) return;

        // move the player
        player.setPosition(nr, nc);
        pathList.addMove(nr, nc);
        moveStack.push(new Position(nr, nc));
        replayQueue.enqueue(new Position(nr, nc)); // enqueue for possible replay
        previewPos = null;

        // score: +1 per move, extra +10 if reach exit
        score += 1;
        if (maze[nr][nc] == MazeGenerator.EXIT) {
            score += 10;
            scoreLabel.setText("Score: " + score);
            gameTimer.stop();
            running = false;
            JOptionPane.showMessageDialog(this, "You reached the portal! Score: " + score);
        } else {
            scoreLabel.setText("Score: " + score);
        }
        repaint();
    }

    private void startReplay() {
        if (replayTimer != null && replayTimer.isRunning()) return;
        // Build a fresh queue from the pathList so replay shows full path from start
        replayQueue.clear();
        PathLinkedList.Node cur = pathList.getHead();
        while (cur != null) {
            replayQueue.enqueue(new Position(cur.row, cur.col));
            cur = cur.next;
        }

        // disable controls during replay
        boolean prevRunning = running;
        running = false;
        startBtn.setEnabled(false);
        undoBtn.setEnabled(false);
        replayBtn.setEnabled(false);
        resetBtn.setEnabled(false);

        previewPos = null;
        final int delay = 150; // milliseconds per step
        replayTimer = new Timer(delay, null);
        replayTimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Position p = replayQueue.dequeue();
                if (p == null) {
                    replayTimer.stop();
                    previewPos = null;
                    running = prevRunning;
                    startBtn.setEnabled(true);
                    undoBtn.setEnabled(true);
                    replayBtn.setEnabled(true);
                    resetBtn.setEnabled(true);
                    repaint();
                    return;
                }
                previewPos = p;
                repaint();
            }
        });
        replayTimer.start();
    }

    private void resetGame() {
        int answer = JOptionPane.showConfirmDialog(this, "Reset level? Progress will be lost.", "Reset", JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            resetGameState();
            repaint();
        }
    }

    private void exitToDashboard() {
        // close current window and open dashboard
        SwingUtilities.getWindowAncestor(this).dispose();
        SwingUtilities.invokeLater(() -> new Dashboard("Abdullah").setVisible(true));
    }

    private void gameOver(String message) {
        running = false;
        gameTimer.stop();
        JOptionPane.showMessageDialog(this, message + " Score: " + score);
    }

    private boolean isValid(int r, int c) {
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // compute tile size to fill a large portion of the screen but leave space for side controls
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        int sideWidth = 220; // space for controls
        int availableWidth = screenWidth - sideWidth - 60; // margins
        int availableHeight = screenHeight - 60;

        tileSize = Math.min(availableWidth / cols, availableHeight / rows);
        int mazeWidth = tileSize * cols;
        int mazeHeight = tileSize * rows;

        startX = 30;
        startY = 30;

        // draw background rectangle for maze
        g.setColor(new Color(200, 200, 200));
        g.fillRect(startX - 2, startY - 2, mazeWidth + 4, mazeHeight + 4);

        // draw each cell
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x = startX + c * tileSize;
                int y = startY + r * tileSize;
                int cell = maze[r][c];
                switch (cell) {
                    case MazeGenerator.WALL -> g.setColor(Color.BLACK);
                    case MazeGenerator.PATH -> g.setColor(new Color(220, 220, 220));
                    case MazeGenerator.EXIT -> g.setColor(Color.GREEN.darker());
                    default -> g.setColor(new Color(220, 220, 220));
                }
                g.fillRect(x, y, tileSize, tileSize);
                g.setColor(Color.GRAY);
                g.drawRect(x, y, tileSize, tileSize);
            }
        }

        // draw path (light blue)
        g.setColor(new Color(0, 100, 255, 90));
        PathLinkedList.Node cur = pathList.getHead();
        while (cur != null) {
            int x = startX + cur.col * tileSize;
            int y = startY + cur.row * tileSize;
            g.fillRect(x + tileSize/8, y + tileSize/8, tileSize - tileSize/4, tileSize - tileSize/4);
            cur = cur.next;
        }

        // draw preview during replay (orange)
        if (previewPos != null) {
            int x = startX + previewPos.col * tileSize;
            int y = startY + previewPos.row * tileSize;
            g.setColor(new Color(255, 140, 0, 200));
            g.fillOval(x + tileSize/6, y + tileSize/6, tileSize - tileSize/3, tileSize - tileSize/3);
        }

        // draw player (blue)
        int px = startX + player.getCol() * tileSize;
        int py = startY + player.getRow() * tileSize;
        g.setColor(Color.BLUE);
        g.fillOval(px + tileSize/8, py + tileSize/8, tileSize - tileSize/4, tileSize - tileSize/4);

        // position side controls (aligned to the right side)
        int sideX = startX + mazeWidth + 20;
        int curY = 30;
        for (Component c : getComponents()) {
            if (c instanceof JButton || c instanceof JLabel) {
                c.setBounds(sideX, curY, 180, 36);
                curY += 46;
            }
        }

        // show instructions bottom-left
        g.setColor(Color.WHITE);
        g.drawString("Use arrow keys to move. Undo uses Stack. Replay uses Queue.", startX, startY + mazeHeight + 30);
    }
}
