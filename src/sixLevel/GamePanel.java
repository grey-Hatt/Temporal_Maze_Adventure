package sixLevel;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.ImageIO;

/**
 * UI: draws maze, player sprite, side panel buttons.
 * Uses GameLogic for DSA operations.
 */
public class GamePanel extends JPanel {
    private final JFrame parent;
    private final GameLogic logic = new GameLogic();

    // drawing
    private int tileSize, startX, startY;

    // UI controls
    private final JButton startBtn = new JButton("Start");
    private final JButton undoBtn = new JButton("Undo");
    private final JButton replayBtn = new JButton("Replay");
    private final JButton hintBtn = new JButton("Hint");
    private final JButton resetBtn = new JButton("Reset");
    private final JButton leaderboardBtn = new JButton("Leaderboard");
    private final JButton exitBtn = new JButton("Exit");
    private final JLabel scoreLabel = new JLabel("Score: 0");
    private final JLabel timerLabel = new JLabel("Time: 90");
        private final JButton shortestBtn = new JButton("Shortest Path");
        private java.util.List<GameLogic.Position> shortestPath = null;
        private Timer shortestTimer = null;

    // timers
    private Timer gameTimer;
    private Timer spawnTimer;
    private Timer replayTimer;
    private Timer blockTimer;
    private int timeLeft = 90;
    private boolean running = false;
    private GameLogic.Position preview = null;
    

        private GameLogic.Position hintPos = null;
        private Timer hintTimer = null;

    // sprite
    private BufferedImage playerImg = null;
    private final Difficulty difficulty;

    public GamePanel(JFrame parent, Difficulty difficulty2) {
        this.parent = parent;
        this.difficulty = difficulty2;

        setLayout(null);
        setBackground(Color.DARK_GRAY);

        logic.reset(difficulty2);
        // set initial time based on difficulty
        timeLeft = timeLimitFor(difficulty2);
        loadAssets();
        setupControls();
        setupTimers();
        wireKeys();
        updateLabels();
    }

    private int timeLimitFor(Difficulty d) {
        return switch (d) {
            case EASY -> 120;
            case MEDIUM -> 90;
            case HARD -> 60;
        };
    }

    private void loadAssets() {
        try {
            java.net.URL imgUrl = getClass().getResource("/secondLevel/player.png");
            if (imgUrl != null)
                playerImg = ImageIO.read(imgUrl);
        } catch (Exception ignored) {
        }
    }

    private void setupControls() {
        add(startBtn);
        add(hintBtn);
        add(shortestBtn);
        add(undoBtn);
        add(replayBtn);
        add(resetBtn);
        add(leaderboardBtn);
        add(exitBtn);
        add(scoreLabel);
        add(timerLabel);

        startBtn.addActionListener(e -> startGameForDifficulty());
        undoBtn.addActionListener(e -> {
            logic.undoLast();
            updateLabels();
            repaint();
        });
        replayBtn.addActionListener(e -> startReplay());
        shortestBtn.addActionListener(e -> showShortestPath());
        hintBtn.addActionListener(e -> showHint());
        resetBtn.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Reset level?") == JOptionPane.YES_OPTION) {
                resetForDifficulty();
            }
        });
        leaderboardBtn.addActionListener(e -> showLeaderboard());
        exitBtn.addActionListener(e -> {
            // close this level window and return to the previous dashboard window
            Window w = SwingUtilities.getWindowAncestor(this);
            if (w != null)
                w.dispose();
            if (parent != null) {
                parent.setVisible(true);
                parent.toFront();
                parent.requestFocus();
            }
        });

        undoBtn.setEnabled(false);
        replayBtn.setEnabled(false);

        // keep focus on panel after clicking
        for (Component c : getComponents())
            if (c instanceof JButton) {
                c.addMouseListener(new MouseAdapter() {
                    public void mouseReleased(MouseEvent e) {
                        requestFocusInWindow();
                    }
                });
            }
    }

    

    private void showHint() {
        int[][] maze = logic.getMazeCopy();
        int er = -1, ec = -1;
        for (int r = 0; r < maze.length; r++) {
            for (int c = 0; c < maze[0].length; c++) {
                if (maze[r][c] == GameLogic.EXIT) { er = r; ec = c; break; }
            }
            if (er != -1) break;
        }
        if (er == -1) return;
        GameLogic.Position cur = logic.getPlayerPos();
        AStarSolver.Result res = AStarSolver.solve(maze, cur.r, cur.c, er, ec);
        if (res == null || res.path == null || res.path.isEmpty()) return;
        if (res.path.size() >= 2) {
            // next step after current
            hintPos = res.path.get(1);
            if (hintTimer != null && hintTimer.isRunning()) hintTimer.stop();
            hintTimer = new Timer(2500, e -> { hintPos = null; ((Timer)e.getSource()).stop(); repaint(); });
            hintTimer.setRepeats(false);
            hintTimer.start();
            repaint();
        }
    }

    private void showShortestPath() {
        int[][] maze = logic.getMazeCopy();
        int er = -1, ec = -1;
        for (int r = 0; r < maze.length; r++) {
            for (int c = 0; c < maze[0].length; c++) {
                if (maze[r][c] == GameLogic.EXIT) { er = r; ec = c; break; }
            }
            if (er != -1) break;
        }
        if (er == -1) return;
        DijkstraSolver.Result res = DijkstraSolver.solve(maze, 1, 1, er, ec);
        if (res == null || res.path == null || res.path.isEmpty()) return;
        shortestPath = res.path;
        if (shortestTimer != null && shortestTimer.isRunning()) shortestTimer.stop();
        shortestTimer = new Timer(3500, e -> { shortestPath = null; ((Timer)e.getSource()).stop(); repaint(); });
        shortestTimer.setRepeats(false);
        shortestTimer.start();
        repaint();
    }

    private void setupTimers() {
        gameTimer = new Timer(1000, e -> {
            if (!running)
                return;
            timeLeft--;
            timerLabel.setText("Time: " + timeLeft);
            if (timeLeft <= 0) {
                running = false;
                gameTimer.stop();
                spawnTimer.stop();
                blockTimer.stop();
                // Add to leaderboard
                int playerSteps = Math.max(0, logic.getPathList().size() - 1);
                int timeUsed = timeLimitFor(difficulty);
                logic.addLeaderboardEntry(logic.getScore(), timeUsed, playerSteps);
                JOptionPane.showMessageDialog(this, "Time's up! Score: " + logic.getScore());
            }
        });
        spawnTimer = new Timer(3000, e -> {
            if (running) {
                logic.spawnNext();
                repaint();
            }
        });
        blockTimer = new Timer(500, e -> {
            if (running) {
                logic.moveBlocks();
                repaint();
            }
        });
    }

    private void wireKeys() {
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!running)
                    return;
                int k = e.getKeyCode();
                int dr = 0, dc = 0;
                switch (k) {
                    case KeyEvent.VK_UP -> dr = -1;
                    case KeyEvent.VK_DOWN -> dr = 1;
                    case KeyEvent.VK_LEFT -> dc = -1;
                    case KeyEvent.VK_RIGHT -> dc = 1;
                    default -> {
                        return;
                    }
                }
                boolean moved = logic.movePlayer(dr, dc);
                if (moved) {
                    updateLabels();
                    repaint();
                    checkExit();
                }
            }
        });
    }

    private void startGame() {
        startGameForDifficulty();
    }

    private void checkExit() {
        GameLogic.Position p = logic.getPlayerPos();
        if (logic.getCell(p.r, p.c) == GameLogic.EXIT) {
            running = false;
            gameTimer.stop();
            spawnTimer.stop();
            blockTimer.stop();
            SoundManager.play("win.wav");
            // debug: indicate checkExit was invoked
            // compute rating using Dijkstra shortest path vs player's taken path
            int[][] maze = logic.getMazeCopy();
            // find exit coordinates
            int er = -1, ec = -1;
            for (int r = 0; r < maze.length; r++) {
                for (int c = 0; c < maze[0].length; c++) {
                    if (maze[r][c] == GameLogic.EXIT) {
                        er = r; ec = c; break;
                    }
                }
                if (er != -1) break;
            }
            int sr = 1, sc = 1; // start is at (1,1)
            DijkstraSolver.Result res = DijkstraSolver.solve(maze, sr, sc, er, ec);
            int shortest = res.distance;
            int playerSteps = Math.max(0, logic.getPathList().size() - 1);
            int stars = Rating.computeStars(shortest, playerSteps);
            // Add to leaderboard
            int timeUsed = timeLimitFor(difficulty) - timeLeft;
            logic.addLeaderboardEntry(logic.getScore(), timeUsed, playerSteps);
            String msg = "You reached the EXIT! Score: " + logic.getScore()
                + "\nShortest steps: " + (shortest >= 0 ? shortest : -1)
                + "\nYour steps: " + playerSteps
                + "\nRating: " + Rating.starsString(stars)
                + "\n\n(Shortest path verified by Dijkstra algorithm.)";
            JOptionPane.showMessageDialog(this, msg);
        }
    }

    private void updateLabels() {
        scoreLabel.setText("Score: " + logic.getScore());
        timerLabel.setText("Time: " + timeLeft);
    }

    private void startReplay() {
        if (replayTimer != null && replayTimer.isRunning())
            return;
        // build queue from path linked list
        java.util.Queue<GameLogic.Position> q = new java.util.LinkedList<>();
        GameLogic.PathLinkedList.Node n = logic.getPathList().head();
        while (n != null) {
            q.add(new GameLogic.Position(n.r, n.c));
            n = n.next;
        }

        boolean prevRunning = running;
        running = false;
        startBtn.setEnabled(false);
        undoBtn.setEnabled(false);
        replayBtn.setEnabled(false);
        resetBtn.setEnabled(false);

        preview = null;
        replayTimer = new Timer(140, e -> {
            GameLogic.Position p = q.poll();
            if (p == null) {
                ((Timer) e.getSource()).stop();
                preview = null;
                running = prevRunning;
                startBtn.setEnabled(true);
                undoBtn.setEnabled(true);
                replayBtn.setEnabled(true);
                resetBtn.setEnabled(true);
                repaint();
                return;
            }
            preview = p;
            repaint();
        });
        replayTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // compute layout and tileSize based on screen size
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int screenW = screen.width;
        int screenH = screen.height;
        int sideW = 260;
        int margin = 28;
        int availW = screenW - sideW - margin * 2;
        int availH = screenH - margin * 2;
        int rows = logic.getRows();
        int cols = logic.getCols();
        tileSize = Math.min(availW / Math.max(1, cols), availH / Math.max(1, rows));
        int mazeW = tileSize * cols;
        int mazeH = tileSize * rows;
        startX = margin;
        startY = margin;

        // background rectangle
        g.setColor(new Color(30, 30, 30));
        g.fillRect(startX - 6, startY - 6, mazeW + 12, mazeH + 12);

        int[][] maze = logic.getMazeCopy();
        // draw cells
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x = startX + c * tileSize;
                int y = startY + r * tileSize;
                int cell = maze[r][c];
                switch (cell) {
                    case GameLogic.WALL -> g.setColor(new Color(20, 20, 20));
                    case GameLogic.PATH -> g.setColor(new Color(200, 200, 200));
                    case GameLogic.COIN -> g.setColor(Color.YELLOW);
                    case GameLogic.TRAP -> g.setColor(Color.RED);
                    case GameLogic.EXIT -> g.setColor(new Color(0, 180, 0));
                    default -> g.setColor(new Color(200, 200, 200));
                }
                g.fillRect(x, y, tileSize, tileSize);
                g.setColor(Color.GRAY);
                g.drawRect(x, y, tileSize, tileSize);
            }
        }

        // draw path (doubly linked list)
        g.setColor(new Color(0, 120, 255, 90));
        GameLogic.PathLinkedList.Node cur = logic.getPathList().head();
        while (cur != null) {
            int x = startX + cur.c * tileSize;
            int y = startY + cur.r * tileSize;
            g.fillRect(x + tileSize / 8, y + tileSize / 8, tileSize - tileSize / 4, tileSize - tileSize / 4);
            cur = cur.next;
        }

        // draw shortest path overlay if present
        if (shortestPath != null) {
            g.setColor(new Color(0, 220, 0, 140));
            for (GameLogic.Position pos : shortestPath) {
                int x = startX + pos.c * tileSize;
                int y = startY + pos.r * tileSize;
                g.fillRect(x + tileSize/6, y + tileSize/6, tileSize - tileSize/3, tileSize - tileSize/3);
            }
        }

        

        // draw preview position (replay)
        if (preview != null) {
            int x = startX + preview.c * tileSize;
            int y = startY + preview.r * tileSize;
            g.setColor(new Color(255, 140, 0, 200));
            g.fillOval(x + tileSize / 6, y + tileSize / 6, tileSize - tileSize / 3, tileSize - tileSize / 3);
        }

        // draw hint (single next move) if present
        if (hintPos != null) {
            int x = startX + hintPos.c * tileSize;
            int y = startY + hintPos.r * tileSize;
            g.setColor(new Color(255, 200, 0, 200));
            g.fillRect(x + tileSize/6, y + tileSize/6, tileSize - tileSize/3, tileSize - tileSize/3);
        }

        // draw player sprite (if exists) or blue circle
        GameLogic.Position p = logic.getPlayerPos();
        int px = startX + p.c * tileSize;
        int py = startY + p.r * tileSize;
        if (playerImg != null) {
            // scale sprite to tileSize (keep aspect)
            Image img = playerImg.getScaledInstance(tileSize - tileSize / 4, tileSize - tileSize / 4,
                    Image.SCALE_SMOOTH);
            g.drawImage(img, px + tileSize / 8, py + tileSize / 8, null);
        } else {
            g.setColor(Color.BLUE);
            g.fillOval(px + tileSize / 8, py + tileSize / 8, tileSize - tileSize / 4, tileSize - tileSize / 4);
        }

        // draw moving blocks
        g.setColor(Color.RED);
        for (MovingBlockManager.MovingBlock block : logic.getBlockManager().getBlocks()) {
            int bx = startX + block.c * tileSize;
            int by = startY + block.r * tileSize;
            g.fillRect(bx + tileSize / 4, by + tileSize / 4, tileSize / 2, tileSize / 2);
        }

        // place side controls
        int sideX = startX + mazeW + 24;
        int y = startY;
        for (Component c : getComponents()) {
            if (c instanceof JButton || c instanceof JLabel) {
                c.setBounds(sideX, y, 200, 40);
                y += 52;
            }
        }

        // footer instructions
        g.setColor(Color.WHITE);
        g.drawString("Arrow keys to move. Undo uses Stack. Spawns use Queue. Path stored in Doubly Linked List. Avoid moving blocks.",
                startX, startY + mazeH + 24);
    }

    // ensure Start/Reset respect selected difficulty
    private void startGameForDifficulty() {
        // Do NOT regenerate maze here — only start the timers and gameplay.
        // Keep the existing maze generated when the level was opened.
        timeLeft = timeLimitFor(difficulty);
        running = true;
        gameTimer.start();
        spawnTimer.start();
        blockTimer.start();
        undoBtn.setEnabled(true);
        replayBtn.setEnabled(true);
        updateLabels();
        requestFocusInWindow();
    }

    private void resetForDifficulty() {
        // Reset player position, path and spawn queue but keep current maze design
        logic.resetPlayerState();
        timeLeft = timeLimitFor(difficulty);
        running = false;
        updateLabels();
        repaint();
    }

    private void showLeaderboard() {
        java.util.List<LeaderboardEntry> topEntries = logic.getTopLeaderboardEntries(10);
        if (topEntries.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No leaderboard entries yet!");
            return;
        }
        StringBuilder sb = new StringBuilder("Top 10 Leaderboard:\n\n");
        int rank = 1;
        for (LeaderboardEntry entry : topEntries) {
            sb.append(rank).append(". ").append(entry.toString()).append("\n");
            rank++;
        }
        JOptionPane.showMessageDialog(this, sb.toString());
    }
}

