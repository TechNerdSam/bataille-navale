import javax.swing.*;import java.awt.*;import java.awt.event.*;import java.io.*;import java.util.List;import java.util.ArrayList;import java.util.Collections;import java.util.Random;

public class BattleshipGame extends JFrame implements KeyListener {
    public enum GameState {MAIN_MENU, PLAYING, PAUSED, GAME_OVER, HIGHSCORE_SCREEN, HOW_TO_PLAY}
    private CardLayout cardLayout;private JPanel mainPanel;private GameEngine gameEngine;

    public BattleshipGame() {
        setTitle("Bataille Navale Géométrique");setSize(800, 600);setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);setLocationRelativeTo(null);
        cardLayout = new CardLayout();mainPanel = new JPanel(cardLayout);gameEngine = new GameEngine(this);
        mainPanel.add(new MainMenuPanel(), GameState.MAIN_MENU.name());
        mainPanel.add(new GamePanel(), GameState.PLAYING.name());
        mainPanel.add(new PauseMenuPanel(), GameState.PAUSED.name());
        mainPanel.add(new HighScoreDisplayPanel(), GameState.HIGHSCORE_SCREEN.name());
        add(mainPanel);addKeyListener(this);setFocusable(true);setFocusTraversalKeysEnabled(false);
        addWindowListener(new WindowAdapter() { // Handle window closing
            @Override public void windowClosing(WindowEvent e) {
                // *** CRITICAL FIX: Only trigger score saving if in an active game state ***
                if (gameEngine.getCurrentState() == GameState.PLAYING ||
                    gameEngine.getCurrentState() == GameState.PAUSED ||
                    gameEngine.getCurrentState() == GameState.GAME_OVER) {
                    gameEngine.handleGameOverOrQuit(); // This will prompt for score if applicable
                }
                System.exit(0); // Always exit when closing window
            }
        });
        showPanel(GameState.MAIN_MENU);
    }
    public void showPanel(GameState state) {
        cardLayout.show(mainPanel, state.name());
        if (state == GameState.PLAYING) { ((GamePanel) mainPanel.getComponent(1)).requestFocusInWindow(); gameEngine.resumeGame(); }
        else if (state == GameState.PAUSED) { gameEngine.pauseGame(); }
        else if (state == GameState.HIGHSCORE_SCREEN) {
            gameEngine.setCurrentState(GameState.HIGHSCORE_SCREEN); // Explicitly set state to high score
            ((HighScoreDisplayPanel) mainPanel.getComponent(3)).refreshScores();
        } else if (state == GameState.MAIN_MENU) {
            gameEngine.setCurrentState(GameState.MAIN_MENU); // Explicitly set state to main menu
        }
    }
    public GameEngine getGameEngine() { return gameEngine; }
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyPressed(KeyEvent e) {
        if (gameEngine.getCurrentState() == GameState.PLAYING) {
            int keyCode = e.getKeyCode();
            if (keyCode == KeyEvent.VK_ESCAPE || keyCode == KeyEvent.VK_P) { showPanel(GameState.PAUSED); }
        }
    }
    @Override public void keyReleased(KeyEvent e) {}
    public void startGame() { gameEngine.initGame(); showPanel(GameState.PLAYING); ((GamePanel) mainPanel.getComponent(1)).startDrawingLoop(); }

    // Method to quit and potentially save score (called from active game states)
    public void quitGameAndSave() {
        gameEngine.handleGameOverOrQuit(); // This calls the score saving logic
        System.exit(0);
    }

    // Method to quit directly without saving (called from non-game states like Main Menu, High Scores)
    public void quitGameDirectly() {
        System.exit(0); // Just exits the application
    }

    public static void main(String[] args) { SwingUtilities.invokeLater(() -> { new BattleshipGame().setVisible(true); }); }

    // --- Inner Classes ---
    public class MainMenuPanel extends JPanel {
        public MainMenuPanel() {
            setLayout(new GridBagLayout());setBackground(new Color(30, 30, 60));GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);gbc.gridx = 0;gbc.gridy = 0;
            add(new JLabel("Bataille Navale Géométrique") {{ setFont(new Font("Arial", Font.BOLD, 36)); setForeground(Color.CYAN); }}, gbc);
            gbc.gridy++;add(createMenuButton("Jouer", e -> startGame()), gbc);
            gbc.gridy++;add(createMenuButton("Comment Jouer", e -> JOptionPane.showMessageDialog(this, "Placez vos navires et coulez ceux de l'ordinateur !\n\nNavires:\nCarrés (Bleu) - Dimensions: Taille x Taille\nCercles (Vert) - Dimensions: Rayon = Taille/2\nTriangles (Orange) - Dimensions: Bas = Taille, Hauteur = Taille")), gbc);
            gbc.gridy++;add(createMenuButton("Meilleurs Scores", e -> showPanel(GameState.HIGHSCORE_SCREEN)), gbc);
            gbc.gridy++;add(createMenuButton("Options", e -> JOptionPane.showMessageDialog(this, "Pas d'options pour le moment.")), gbc);
            gbc.gridy++;add(createMenuButton("Quitter", e -> quitGameDirectly()), gbc); // Direct quit from main menu
        }
        private JButton createMenuButton(String text, ActionListener action) {
            JButton button = new JButton(text);button.setFont(new Font("Arial", Font.BOLD, 20));button.setBackground(new Color(70, 70, 100));
            button.setForeground(Color.WHITE);button.setPreferredSize(new Dimension(200, 50));button.setFocusPainted(false);
            button.setBorder(BorderFactory.createLineBorder(Color.CYAN, 2));button.addActionListener(action);return button;
        }
    }

    public class PauseMenuPanel extends JPanel {
        public PauseMenuPanel() {
            setLayout(new GridBagLayout());setBackground(new Color(0, 0, 0, 180));GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);gbc.gridx = 0;gbc.gridy = 0;
            add(new JLabel("Jeu en Pause") {{ setFont(new Font("Arial", Font.BOLD, 40)); setForeground(Color.WHITE); }}, gbc);
            gbc.gridy++;add(createMenuButton("Reprendre", e -> showPanel(GameState.PLAYING)), gbc);
            gbc.gridy++;add(createMenuButton("Menu Principal", e -> { gameEngine.handleGameOverOrQuit(); showPanel(GameState.MAIN_MENU); }), gbc); // Save score when going to main menu from pause
            gbc.gridy++;add(createMenuButton("Quitter le Jeu", e -> quitGameAndSave()), gbc); // Quit and save score
        }
        private JButton createMenuButton(String text, ActionListener action) {
            JButton button = new JButton(text);button.setFont(new Font("Arial", Font.BOLD, 20));button.setBackground(new Color(100, 100, 150));
            button.setForeground(Color.WHITE);button.setPreferredSize(new Dimension(200, 50));button.setFocusPainted(false);
            button.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));button.addActionListener(action);return button;
        }
    }

    public class GamePanel extends JPanel {
        private Timer gameLoopTimer;
        public GamePanel() {
            setBackground(Color.BLACK);addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    if (gameEngine.getCurrentState() == GameState.PLAYING) {
                        int cellSize = Math.min(getWidth(), getHeight()) / (Board.BOARD_SIZE * 2);
                        if (e.getY() < getHeight() / 2) { gameEngine.playerMakeMove(e.getX() / cellSize, e.getY() / cellSize); repaint(); }
                    }
                }
            });
        }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);Graphics2D g2d = (Graphics2D) g;g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            gameEngine.drawGame(g2d, getWidth(), getHeight());
            g2d.setColor(Color.WHITE);g2d.setFont(new Font("Arial", Font.BOLD, 18));
            g2d.drawString("Score: " + gameEngine.getPlayerScore(), 10, 20);g2d.drawString("Niveau: " + gameEngine.getCurrentLevel(), 10, 40);
            if (gameEngine.getCurrentState() == GameState.GAME_OVER) {
                g2d.setColor(new Color(255, 0, 0, 150));g2d.fillRect(0, getHeight() / 2 - 50, getWidth(), 100);
                g2d.setColor(Color.WHITE);g2d.setFont(new Font("Arial", Font.BOLD, 50));
                String gameOverText = "GAME OVER!";int textWidth = g2d.getFontMetrics().stringWidth(gameOverText);
                g2d.drawString(gameOverText, (getWidth() - textWidth) / 2, getHeight() / 2 + 15);
            }
        }
        public void startDrawingLoop() { if (gameLoopTimer != null && gameLoopTimer.isRunning()) { gameLoopTimer.stop(); }
            gameLoopTimer = new Timer(1000 / 60, e -> { if (gameEngine.getCurrentState() == GameState.PLAYING) { repaint(); }});
            gameLoopTimer.start();
        }
        public void stopDrawingLoop() { if (gameLoopTimer != null) { gameLoopTimer.stop(); }}
    }

    public class HighScoreDisplayPanel extends JPanel {
        private JTextArea scoreArea;
        public HighScoreDisplayPanel() {
            setLayout(new BorderLayout());setBackground(new Color(40, 40, 80));
            add(new JLabel("Meilleurs Scores", SwingConstants.CENTER) {{ setFont(new Font("Arial", Font.BOLD, 36)); setForeground(Color.YELLOW); }}, BorderLayout.NORTH);
            scoreArea = new JTextArea();scoreArea.setFont(new Font("Monospaced", Font.PLAIN, 18));scoreArea.setBackground(new Color(50, 50, 100));
            scoreArea.setForeground(Color.WHITE);scoreArea.setEditable(false);add(new JScrollPane(scoreArea), BorderLayout.CENTER);
            JButton backButton = new JButton("Retour au Menu Principal");backButton.setFont(new Font("Arial", Font.BOLD, 20));backButton.setBackground(new Color(70, 70, 100));
            backButton.setForeground(Color.WHITE);backButton.setFocusPainted(false);backButton.setBorder(BorderFactory.createLineBorder(Color.CYAN, 2));
            backButton.addActionListener(e -> showPanel(GameState.MAIN_MENU));
            JPanel buttonPanel = new JPanel();buttonPanel.setBackground(new Color(40, 40, 80));buttonPanel.add(backButton);add(buttonPanel, BorderLayout.SOUTH);
        }
        public void refreshScores() {
            scoreArea.setText("");StringBuilder sb = new StringBuilder();List<HighScoreEntry> scores = gameEngine.getHighScoreManager().getHighScores();
            if (scores.isEmpty()) { sb.append("Aucun score enregistré pour le moment."); }
            else { for (int i = 0; i < scores.size(); i++) { sb.append(String.format("%d. %-15s %10d\n", i + 1, scores.get(i).getPlayerName(), scores.get(i).getScore())); }}
            scoreArea.setText(sb.toString());
        }
    }

    public class GameEngine {
        private BattleshipGame gameFrame;private GameState currentState;
        private Board playerBoard;private Board computerBoard;private HighScoreManager highScores;
        private int currentLevel;private long playerScore;private String currentPlayerName = "Joueur";

        public static final int MAX_LEVEL = 20;

        public GameEngine(BattleshipGame gameFrame) { this.gameFrame = gameFrame;this.highScores = new HighScoreManager();this.currentState = GameState.MAIN_MENU;initGame(); }
        public void initGame() {
            playerBoard = new Board();computerBoard = new Board();currentLevel = 1;playerScore = 0;placeShipsForLevel(currentLevel);
            currentState = GameState.PLAYING;
        }
        private void placeShipsForLevel(int level) {
            playerBoard.clearShips();computerBoard.clearShips();Random rand = new Random();
            int numShips = Math.min(level / 2 + 3, 10);int minShipSize = 2;int maxShipSize = Math.min(Board.BOARD_SIZE / 2, level / 3 + 2);
            if (maxShipSize < minShipSize) maxShipSize = minShipSize;
            for (int i = 0; i < numShips; i++) {
                int shipSize = rand.nextInt(maxShipSize - minShipSize + 1) + minShipSize;
                Ship playerShip = createRandomGeometricShip(shipSize);while (!playerBoard.placeShipRandomly(playerShip)) { playerShip = createRandomGeometricShip(shipSize); }
                Ship computerShip = createRandomGeometricShip(shipSize);while (!computerBoard.placeShipRandomly(computerShip)) { computerShip = createRandomGeometricShip(shipSize); }
            }
        }
        private Ship createRandomGeometricShip(int size) {
            Random rand = new Random();
            switch (rand.nextInt(3)) {
                case 0: return new SquareShip(size);
                case 1: return new CircleShip(size);
                case 2: TriangleShip triShip = new TriangleShip(size);triShip.setOrientation(TriangleShip.Orientation.values()[rand.nextInt(TriangleShip.Orientation.values().length)]);return triShip;
                default: return new SquareShip(size);
            }
        }
        public void playerMakeMove(int x, int y) {
            if (currentState != GameState.PLAYING) return;
            if (x < 0 || x >= Board.BOARD_SIZE || y < 0 || y >= Board.BOARD_SIZE) { JOptionPane.showMessageDialog(gameFrame, "Hors des limites du plateau !"); return; }
            if (computerBoard.isShot(x, y)) { JOptionPane.showMessageDialog(gameFrame, "Vous avez déjà tiré ici !"); return; }
            boolean hit = computerBoard.shoot(x, y);
            if (hit) { playerScore += 100 * currentLevel; JOptionPane.showMessageDialog(gameFrame, "Touché !"); }
            else { JOptionPane.showMessageDialog(gameFrame, "Manqué !"); }
            checkGameStatus();
            if (currentState == GameState.PLAYING) { computerMakeMove(); checkGameStatus(); }
        }
        private void computerMakeMove() {
            if (currentState != GameState.PLAYING) return;Random rand = new Random();int x, y;
            do { x = rand.nextInt(Board.BOARD_SIZE); y = rand.nextInt(Board.BOARD_SIZE); } while (playerBoard.isShot(x, y));
            boolean hit = playerBoard.shoot(x, y);
            if (hit) { JOptionPane.showMessageDialog(gameFrame, "L'ordinateur a touché un de vos navires !"); }
            else { JOptionPane.showMessageDialog(gameFrame, "L'ordinateur a manqué !"); }
        }
        private void checkGameStatus() {
            if (computerBoard.areAllShipsSunk()) { playerScore += 500 * currentLevel; JOptionPane.showMessageDialog(gameFrame, "Tous les navires ennemis coulés !"); levelUp(); }
            else if (playerBoard.areAllShipsSunk()) { JOptionPane.showMessageDialog(gameFrame, "Tous vos navires sont coulés !"); setGameOver(); }
        }
        private void levelUp() {
            currentLevel++;
            if (currentLevel > MAX_LEVEL) { JOptionPane.showMessageDialog(gameFrame, "Vous avez terminé tous les niveaux ! Le jeu recommence au niveau 1 avec votre score actuel."); currentLevel = 1; playerScore += 10000; }
            else { JOptionPane.showMessageDialog(gameFrame, "Bravo ! Vous passez au niveau " + currentLevel + " !"); }
            placeShipsForLevel(currentLevel); gameFrame.showPanel(GameState.PLAYING);
        }
        private void setGameOver() {
            currentState = GameState.GAME_OVER;handleGameOverOrQuit();JOptionPane.showMessageDialog(gameFrame, "Game Over! Votre score final : " + playerScore);
            gameFrame.showPanel(GameState.MAIN_MENU);
        }
        public void handleGameOverOrQuit() {
            // This method is now specifically called when a game session truly ends.
            // It will trigger the score saving prompt if the score is positive or it's a new high score.
            if (playerScore > 0 || highScores.isNewHighScore(playerScore)) {
                String name = JOptionPane.showInputDialog(gameFrame, "Entrez votre pseudo pour le highscore:", currentPlayerName);
                if (name == null || name.trim().isEmpty()) { name = "Anonyme"; }
                currentPlayerName = name;highScores.addScore(currentPlayerName, playerScore);
            }
            highScores.saveHighScores();
        }
        public void drawGame(Graphics2D g2d, int panelWidth, int panelHeight) {
            int cellSize = Math.min(panelWidth, panelHeight) / (Board.BOARD_SIZE * 2);
            g2d.setColor(Color.DARK_GRAY);g2d.drawString("Terrain de l'Ordinateur", 10, panelHeight / 2 - 10);
            computerBoard.draw(g2d, 0, 0, cellSize, true);
            int playerBoardY = panelHeight / 2;
            g2d.setColor(Color.DARK_GRAY);g2d.drawString("Votre Terrain", 10, playerBoardY + 20);
            playerBoard.draw(g2d, 0, playerBoardY + 30, cellSize, false);
        }
        public GameState getCurrentState() { return currentState; }
        public void setCurrentState(GameState state) { this.currentState = state; } // New setter for state
        public void pauseGame() { if (currentState == GameState.PLAYING) { currentState = GameState.PAUSED; }}
        public void resumeGame() { if (currentState == GameState.PAUSED) { currentState = GameState.PLAYING; }}
        public long getPlayerScore() { return playerScore; }
        public int getCurrentLevel() { return currentLevel; }
        public HighScoreManager getHighScoreManager() { return highScores; }
    }

    public class Board {
        public static final int BOARD_SIZE = 10;private char[][] grid;private List<Ship> ships;
        public Board() { grid = new char[BOARD_SIZE][BOARD_SIZE];ships = new ArrayList<>();initializeGrid(); }
        private void initializeGrid() { for (int i = 0; i < BOARD_SIZE; i++) { for (int j = 0; j < BOARD_SIZE; j++) { grid[i][j] = 'W'; }}}
        public boolean placeShipRandomly(Ship ship) {
            Random rand = new Random();int attempts = 0;
            while (attempts < 500) {
                int startX = rand.nextInt(BOARD_SIZE);int startY = rand.nextInt(BOARD_SIZE);boolean isHorizontal = rand.nextBoolean();
                if (ship instanceof TriangleShip) { ((TriangleShip) ship).setOrientation(TriangleShip.Orientation.values()[rand.nextInt(TriangleShip.Orientation.values().length)]); }
                ship.setPlacement(startX, startY, isHorizontal);List<Point> proposedCells = ship.getOccupiedCells();
                boolean canPlace = true;
                for (Point p : proposedCells) {
                    if (p.x < 0 || p.x >= BOARD_SIZE || p.y < 0 || p.y >= BOARD_SIZE || grid[p.x][p.y] == 'S') { canPlace = false; break; }
                }
                if (canPlace) { for (Point p : proposedCells) { grid[p.x][p.y] = 'S'; } ships.add(ship); return true; }
                attempts++;
            }
            return false;
        }
        public boolean shoot(int x, int y) {
            if (x < 0 || x >= BOARD_SIZE || y < 0 || y >= BOARD_SIZE || grid[x][y] == 'H' || grid[x][y] == 'M') { return false; }
            if (grid[x][y] == 'S') {
                grid[x][y] = 'H';boolean shipSunk = false;
                for (Ship ship : ships) { if (ship.isHit(x, y)) { if (ship.isSunk()) { shipSunk = true; } break; }} return true;
            } else { grid[x][y] = 'M'; return false; }
        }
        public boolean isShot(int x, int y) { return x < 0 || x >= BOARD_SIZE || y < 0 || y >= BOARD_SIZE || grid[x][y] == 'H' || grid[x][y] == 'M'; }
        public boolean areAllShipsSunk() { for (Ship ship : ships) { if (!ship.isSunk()) { return false; }} return true; }
        public void clearShips() { ships.clear();initializeGrid(); }
        public void draw(Graphics2D g2d, int offsetX, int offsetY, int cellSize, boolean hideShips) {
            g2d.setColor(Color.GRAY);for (int i = 0; i <= BOARD_SIZE; i++) { g2d.drawLine(offsetX + i * cellSize, offsetY, offsetX + i * cellSize, offsetY + BOARD_SIZE * cellSize);g2d.drawLine(offsetX, offsetY + i * cellSize, offsetX + BOARD_SIZE * cellSize, offsetY + i * cellSize); }
            for (int i = 0; i < BOARD_SIZE; i++) { for (int j = 0; j < BOARD_SIZE; j++) {
                int x = offsetX + i * cellSize;int y = offsetY + j * cellSize;
                switch (grid[i][j]) {
                    case 'H': g2d.setColor(Color.RED);g2d.fillRect(x, y, cellSize, cellSize);g2d.setColor(Color.WHITE);g2d.drawLine(x, y, x + cellSize, y + cellSize);g2d.drawLine(x + cellSize, y, x, y + cellSize); break;
                    case 'M': g2d.setColor(new Color(50, 50, 50));g2d.fillRect(x, y, cellSize, cellSize);g2d.setColor(Color.WHITE);g2d.fillOval(x + cellSize / 4, y + cellSize / 4, cellSize / 2, cellSize / 2); break;
                }
            }}
            if (!hideShips) { for (Ship ship : ships) { ship.draw(g2d, offsetX, offsetY, cellSize); }}
        }
    }

    public abstract class Ship {
        protected int size;protected int hitCount;protected int startX, startY;protected boolean isHorizontal;protected List<Point> occupiedCells;
        public Ship(int size) { this.size = size;this.hitCount = 0;this.occupiedCells = new ArrayList<>(); }
        public int getSize() { return size; }
        public boolean isSunk() { return hitCount >= occupiedCells.size(); }
        public void setPlacement(int startX, int startY, boolean isHorizontal) { this.startX = startX;this.startY = startY;this.isHorizontal = isHorizontal;this.occupiedCells = calculateOccupiedCells(startX, startY); }
        public boolean isHit(int x, int y) { for (Point p : occupiedCells) { if (p.x == x && p.y == y) { hitCount++; return true; }} return false; }
        public List<Point> getOccupiedCells() { return occupiedCells; }
        public abstract List<Point> calculateOccupiedCells(int startX, int startY);
        public abstract void draw(Graphics2D g2d, int offsetX, int offsetY, int cellSize);
    }

    public class SquareShip extends Ship {
        public SquareShip(int size) { super(size); if (size < 1) this.size = 1; }
        @Override public List<Point> calculateOccupiedCells(int startX, int startY) {
            List<Point> cells = new ArrayList<>();for (int i = 0; i < size; i++) { for (int j = 0; j < size; j++) { cells.add(new Point(startX + i, startY + j)); }} return cells;
        }
        @Override public void draw(Graphics2D g2d, int offsetX, int offsetY, int cellSize) {
            g2d.setColor(new Color(60, 60, 200));int drawX = offsetX + startX * cellSize;int drawY = offsetY + startY * cellSize;int drawSize = size * cellSize;
            g2d.fillRect(drawX, drawY, drawSize, drawSize);g2d.setColor(new Color(100, 100, 255));g2d.drawRect(drawX, drawY, drawSize, drawSize);
        }
    }

    public class CircleShip extends Ship {
        public CircleShip(int size) { super(size); if (size < 1) this.size = 1; }
        @Override public List<Point> calculateOccupiedCells(int startX, int startY) {
            List<Point> cells = new ArrayList<>();int radius = size / 2;int centerX = startX + radius;int centerY = startY + radius;
            for (int i = 0; i < size; i++) { for (int j = 0; j < size; j++) {
                double distance = Math.sqrt(Math.pow((startX + i + 0.5) - (centerX + 0.5), 2) + Math.pow((startY + j + 0.5) - (centerY + 0.5), 2));
                if (distance <= radius + 0.4) { cells.add(new Point(startX + i, startY + j)); }
            }} return cells;
        }
        @Override public void draw(Graphics2D g2d, int offsetX, int offsetY, int cellSize) {
            g2d.setColor(new Color(0, 160, 0));int diameterPixels = size * cellSize;int drawX = offsetX + startX * cellSize;int drawY = offsetY + startY * cellSize;
            g2d.fillOval(drawX, drawY, diameterPixels, diameterPixels);g2d.setColor(new Color(0, 200, 0));g2d.drawOval(drawX, drawY, diameterPixels, diameterPixels);
        }
    }

    public class TriangleShip extends Ship {
        public enum Orientation { UP, DOWN, LEFT, RIGHT } private Orientation orientation;
        public TriangleShip(int size) { super(size); if (size < 1) this.size = 1; this.orientation = Orientation.UP; }
        public void setOrientation(Orientation orientation) { this.orientation = orientation; }
        @Override public void setPlacement(int startX, int startY, boolean isHorizontal) { super.setPlacement(startX, startY, isHorizontal); }
        @Override public List<Point> calculateOccupiedCells(int startX, int startY) {
            List<Point> cells = new ArrayList<>();
            for (int i = 0; i < size; i++) { for (int j = 0; j < size; j++) {
                if (orientation == Orientation.UP && (i + j < size)) { cells.add(new Point(startX + i, startY + j)); }
                else if (orientation == Orientation.DOWN && (i + j >= size -1)) { cells.add(new Point(startX + i, startY + j)); }
                else if (orientation == Orientation.LEFT && (i < size - j)) { cells.add(new Point(startX + i, startY + j)); }
                else if (orientation == Orientation.RIGHT && (i >= j)) { cells.add(new Point(startX + i, startY + j)); }
            }}
            List<Point> uniqueCells = new ArrayList<>();for (Point p : cells) { if (p.x >= 0 && p.x < Board.BOARD_SIZE && p.y >= 0 && p.y < Board.BOARD_SIZE && !uniqueCells.contains(p)) { uniqueCells.add(p); }} return uniqueCells;
        }
        @Override public void draw(Graphics2D g2d, int offsetX, int offsetY, int cellSize) {
            g2d.setColor(new Color(200, 100, 0));int x1, y1, x2, y2, x3, y3;int basePixels = size * cellSize;int heightPixels = size * cellSize;
            int drawStartX = offsetX + startX * cellSize;int drawStartY = offsetY + startY * cellSize;
            switch (orientation) {
                case UP: x1 = drawStartX;y1 = drawStartY + heightPixels;x2 = drawStartX + basePixels;y2 = drawStartY + heightPixels;x3 = drawStartX + basePixels / 2;y3 = drawStartY;break;
                case DOWN: x1 = drawStartX;y1 = drawStartY;x2 = drawStartX + basePixels;y2 = drawStartY;x3 = drawStartX + basePixels / 2;y3 = drawStartY + heightPixels;break;
                case LEFT: x1 = drawStartX + basePixels;y1 = drawStartY;x2 = drawStartX + basePixels;y2 = drawStartY + heightPixels;x3 = drawStartX;y3 = drawStartY + heightPixels / 2;break;
                case RIGHT: x1 = drawStartX;y1 = drawStartY;x2 = drawStartX;y2 = drawStartY + heightPixels;x3 = drawStartX + basePixels;y3 = drawStartY + heightPixels / 2;break;
                default: x1 = x2 = x3 = y1 = y2 = y3 = 0;
            }
            g2d.fillPolygon(new int[]{x1, x2, x3}, new int[]{y1, y2, y3}, 3);g2d.setColor(new Color(255, 150, 0));g2d.drawPolygon(new int[]{x1, x2, x3}, new int[]{y1, y2, y3}, 3);
        }
    }

    public static class HighScoreEntry implements Serializable, Comparable<HighScoreEntry> {
        private static final long serialVersionUID = 1L;private String playerName;private long score;
        public HighScoreEntry(String playerName, long score) { this.playerName = playerName; this.score = score; }
        public String getPlayerName() { return playerName; } public long getScore() { return score; }
        @Override public String toString() { return playerName + ": " + score; }
        @Override public int compareTo(HighScoreEntry other) { return Long.compare(other.score, this.score); }
    }

    public class HighScoreManager {
        private static final String HIGHSCORE_FILE = "highscores.dat"; private static final int MAX_HIGHSCORES = 10; private List<HighScoreEntry> highScores;
        public HighScoreManager() { highScores = new ArrayList<>(); loadHighScores(); }
        public void addScore(String playerName, long score) {
            highScores.add(new HighScoreEntry(playerName, score));Collections.sort(highScores);
            if (highScores.size() > MAX_HIGHSCORES) { highScores = highScores.subList(0, MAX_HIGHSCORES); }
        }
        public List<HighScoreEntry> getHighScores() { return highScores; }
        public boolean isNewHighScore(long score) { return highScores.size() < MAX_HIGHSCORES || score > highScores.get(highScores.size() - 1).getScore(); }
        @SuppressWarnings("unchecked") public void loadHighScores() {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(HIGHSCORE_FILE))) { highScores = (List<HighScoreEntry>) ois.readObject(); Collections.sort(highScores); }
            catch (FileNotFoundException e) { System.out.println("Highscore file not found, starting fresh."); }
            catch (IOException | ClassNotFoundException e) { System.err.println("Error loading high scores: " + e.getMessage()); }
        }
        public void saveHighScores() {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(HIGHSCORE_FILE))) { oos.writeObject(highScores); }
            catch (IOException e) { System.err.println("Error saving high scores: " + e.getMessage()); }
        }
    }

    public class Player {} public class ComputerPlayer {}
}