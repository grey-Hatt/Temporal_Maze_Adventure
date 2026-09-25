package fourthLevel;


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
    private final JButton shortestBtn = new JButton("Shortest Path");
    private final JButton resetBtn = new JButton("Reset");
    private final JButton exitBtn = new JButton("Exit");
    private final JLabel scoreLabel = new JLabel("Score: 0");
    private final JLabel timerLabel = new JLabel("Time: 90");

    // timers
    private Timer gameTimer;
    private Timer spawnTimer;
    private Timer replayTimer;
        private java.util.List<GameLogic.Position> shortestPath = null;
        private Timer shortestTimer = null;
    private int timeLeft = 90;
    private boolean running = false;
    private GameLogic.Position preview = null;
    

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
            add(shortestBtn);
        add(undoBtn);
        add(replayBtn);
        add(resetBtn);
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
        
        resetBtn.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Reset level?") == JOptionPane.YES_OPTION) {
                resetForDifficulty();
            }
        });
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
                JOptionPane.showMessageDialog(this, "Time's up! Score: " + logic.getScore());
            }
        });
        spawnTimer = new Timer(3000, e -> {
            if (running) {
                logic.spawnNext();
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
            String msg = "You reached the EXIT! Score: " + logic.getScore()
                    + "\nShortest steps: " + (shortest >= 0 ? shortest : -1)
                    + "\nYour steps: " + playerSteps
                    + "\nRating: " + Rating.starsString(stars);
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
        g.drawString("Arrow keys to move. Undo uses Stack. Spawns use Queue. Path stored in Doubly Linked List.",
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
}

