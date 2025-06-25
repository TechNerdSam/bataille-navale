import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * =================================================================================================
 * BATTLESHIP PREMIUM V3.0 (Fully Documented)
 * =================================================================================================
 *
 * @author Gemini AI Services & Human Coder
 * @version 3.0
 *
 * FR:
 * Cette classe est la fenêtre principale et le contrôleur central du jeu de bataille navale.
 * Elle utilise un CardLayout pour gérer les différents écrans du jeu (menu, placement, jeu, etc.).
 * Elle initialise également tous les composants majeurs du jeu.
 *
 * EN:
 * This class is the main window and central controller for the Battleship game.
 * It uses a CardLayout to manage the different game screens (menu, placement, gameplay, etc.).
 * It also initializes all major game components.
 * =================================================================================================
 */
public class BattleshipPremiumV2 extends JFrame {

    /**
     * FR: Énumération représentant les différents états ou écrans possibles du jeu.
     * EN: Enumeration representing the different possible states or screens of the game.
     */
    public enum GameState {
        MAIN_MENU,        // FR: Écran du menu principal. / EN: Main menu screen.
        SHIP_PLACEMENT,   // FR: Phase de placement des navires. / EN: Ship placement phase.
        PLAYING,          // FR: Phase de jeu active. / EN: Active gameplay phase.
        PAUSED,           // FR: Jeu en pause. / EN: Game is paused.
        GAME_OVER,        // FR: La partie est terminée. / EN: The game is over.
        HIGHSCORE_SCREEN  // FR: Écran des meilleurs scores. / EN: High scores screen.
    }

    // FR: Constante pour le titre de l'application. / EN: Constant for the application title.
    private static final String APP_TITLE = "Battleship Premium V2";

    // FR: Gestionnaires de l'interface utilisateur et de la logique du jeu.
    // EN: Managers for the user interface and game logic.
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);
    private final GameEngine gameEngine = new GameEngine(this);
    private final GamePanel gamePanel = new GamePanel();
    private final ShipPlacementPanel shipPlacementPanel = new ShipPlacementPanel();
    private final HighScoreDisplayPanel highScoreDisplayPanel = new HighScoreDisplayPanel();

    /**
     * FR: Constructeur principal de l'application. Initialise la fenêtre et les panneaux.
     * EN: Main constructor for the application. Initializes the window and panels.
     */
    public BattleshipPremiumV2() {
        setTitle(APP_TITLE);
        setSize(Theme.APP_WIDTH, Theme.APP_HEIGHT);
        // FR: Empêche la fermeture par défaut pour gérer la sauvegarde.
        // EN: Prevents default closing to handle the save process.
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // FR: Ajout des différents panneaux au CardLayout avec un nom correspondant à l'état du jeu.
        // EN: Adding the different panels to the CardLayout with a name corresponding to the game state.
        mainPanel.add(new MainMenuPanel(), GameState.MAIN_MENU.name());
        mainPanel.add(shipPlacementPanel, GameState.SHIP_PLACEMENT.name());
        mainPanel.add(gamePanel, GameState.PLAYING.name());
        mainPanel.add(new PauseMenuPanel(), GameState.PAUSED.name());
        mainPanel.add(highScoreDisplayPanel, GameState.HIGHSCORE_SCREEN.name());

        add(mainPanel);

        // FR: Ajout d'un écouteur pour gérer l'événement de fermeture de la fenêtre.
        // EN: Adding a listener to handle the window closing event.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // FR: Si le jeu est en cours ou en pause, on déclenche la logique de fin de partie.
                // EN: If the game is in progress or paused, trigger the game over logic.
                if (gameEngine.getCurrentState() == GameState.PLAYING || gameEngine.getCurrentState() == GameState.PAUSED) {
                    gameEngine.handleGameOverOrQuit();
                }
                System.exit(0); // FR: Ferme l'application. / EN: Closes the application.
            }
        });

        // FR: Affiche le menu principal au démarrage.
        // EN: Show the main menu on startup.
        showPanel(GameState.MAIN_MENU);
    }

    /**
     * FR: Change le panneau visible dans le CardLayout.
     * EN: Changes the visible panel in the CardLayout.
     * @param state FR: L'état du jeu à afficher. / EN: The game state to display.
     */
    public void showPanel(GameState state) {
        cardLayout.show(mainPanel, state.name());
        gameEngine.setCurrentState(state);

        // FR: Actions spécifiques à exécuter lors du changement de panneau.
        // EN: Specific actions to execute when changing panels.
        switch (state) {
            case PLAYING -> gamePanel.requestFocusInWindow();
            case HIGHSCORE_SCREEN -> highScoreDisplayPanel.refreshScores();
            case SHIP_PLACEMENT -> shipPlacementPanel.startPlacementPhase();
            default -> { /* FR: Aucune action requise. / EN: No action needed. */ }
        }
    }

    // --- Getters ---
    public GameEngine getGameEngine() { return gameEngine; }
    public GamePanel getGamePanel() { return gamePanel; }

    /**
     * FR: Démarre une toute nouvelle partie depuis le début.
     * EN: Starts a brand new game from the beginning.
     */
    public void startNewGame() {
        gameEngine.initGame();
        showPanel(GameState.SHIP_PLACEMENT);
    }

    /**
     * FR: Lance la phase de jeu après le placement des navires.
     * EN: Starts the gameplay phase after ship placement.
     */
    public void startGameplay() {
        showPanel(GameState.PLAYING);
        gamePanel.startDrawingLoop();
    }

    /**
     * FR: Point d'entrée principal de l'application.
     * EN: Main entry point of the application.
     * @param args FR: Arguments de la ligne de commande (non utilisés). / EN: Command-line arguments (not used).
     */
    public static void main(String[] args) {
        // FR: Tente de définir un look and feel multiplateforme pour une apparence cohérente.
        // EN: Tries to set a cross-platform look and feel for a consistent appearance.
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // FR: Lance l'interface graphique sur le thread de dispatch des événements (EDT).
        // EN: Launches the graphical user interface on the Event Dispatch Thread (EDT).
        SwingUtilities.invokeLater(() -> new BattleshipPremiumV2().setVisible(true));
    }

    /**
     * FR: Classe interne contenant toutes les constantes de thème (couleurs, polices, tailles).
     * EN: Inner class containing all theme constants (colors, fonts, sizes).
     */
    private static final class Theme {
        // Dimensions
        public static final int APP_WIDTH = 800;
        public static final int APP_HEIGHT = 800;
        public static final int BOARD_SIZE = 10; // 10x10 grid
        public static final int CELL_SIZE = 40;  // 40x40 pixels per cell
        public static final int MAX_LEVEL = 5;

        // Colors
        public static final Color COLOR_BACKGROUND_START = new Color(10, 20, 40);
        public static final Color COLOR_BACKGROUND_END = new Color(25, 45, 80);
        public static final Color COLOR_TEXT = new Color(230, 230, 255);
        public static final Color COLOR_ACCENT = new Color(0, 150, 255);
        public static final Color COLOR_BUTTON = new Color(30, 50, 90);
        public static final Color COLOR_BUTTON_HOVER = new Color(50, 80, 130);
        public static final Color COLOR_GRID_BG = new Color(40, 60, 110, 150);
        public static final Color COLOR_GRID_LINE = new Color(60, 90, 150);
        public static final Color COLOR_HIT = new Color(255, 80, 50);
        public static final Color COLOR_MISS = new Color(150, 180, 220, 200);
        public static final Color COLOR_SHIP = new Color(180, 190, 210);
        public static final Color COLOR_GHOST_OK = new Color(0, 255, 0, 100);
        public static final Color COLOR_GHOST_BAD = new Color(255, 0, 0, 100);
        public static final Color COLOR_TRANSPARENT_BG = new Color(0, 0, 0, 180);

        // Fonts
        public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 38);
        public static final Font FONT_SUBTITLE = new Font("SansSerif", Font.BOLD, 24);
        public static final Font FONT_BUTTON = new Font("SansSerif", Font.BOLD, 18);
        public static final Font FONT_TEXT = new Font("SansSerif", Font.PLAIN, 16);
        public static final Font FONT_MONO = new Font("Monospaced", Font.PLAIN, 18);
    }

    /**
     * FR: Un JPanel personnalisé qui dessine un fond en dégradé.
     * EN: A custom JPanel that draws a gradient background.
     */
    private static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            var g2d = (Graphics2D) g;
            var gp = new GradientPaint(0, 0, Theme.COLOR_BACKGROUND_START, 0, getHeight(), Theme.COLOR_BACKGROUND_END);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    /**
     * FR: Un JButton personnalisé avec un style moderne.
     * EN: A custom JButton with a modern style.
     */
    private static class ModernButton extends JButton {
        public ModernButton(String text) {
            super(text);
            setFont(Theme.FONT_BUTTON);
            setBackground(Theme.COLOR_BUTTON);
            setForeground(Theme.COLOR_TEXT);
            setFocusPainted(false);
            setPreferredSize(new Dimension(250, 55));
            setBorder(BorderFactory.createLineBorder(Theme.COLOR_ACCENT, 1));

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent evt) { setBackground(Theme.COLOR_BUTTON_HOVER); }
                public void mouseExited(MouseEvent evt) { setBackground(Theme.COLOR_BUTTON); }
            });
        }
    }

    /**
     * FR: Le panneau du menu principal, affichant les options de jeu.
     * EN: The main menu panel, displaying game options.
     */
    private class MainMenuPanel extends GradientPanel {
        public MainMenuPanel() {
            setLayout(new GridBagLayout());
            var gbc = new GridBagConstraints();
            gbc.insets = new Insets(15, 10, 15, 10);
            gbc.gridx = 0;

            var title = new JLabel(APP_TITLE);
            title.setFont(Theme.FONT_TITLE);
            title.setForeground(Theme.COLOR_TEXT);
            gbc.gridy = 0;
            add(title, gbc);

            gbc.gridy = 1;
            add(new ModernButton("Nouvelle Partie") {{ addActionListener(_ -> startNewGame()); }}, gbc);
            gbc.gridy = 2;
            add(new ModernButton("Meilleurs Scores") {{ addActionListener(_ -> showPanel(GameState.HIGHSCORE_SCREEN)); }}, gbc);
            gbc.gridy = 3;
            add(new ModernButton("Quitter") {{ addActionListener(_ -> System.exit(0)); }}, gbc);
        }
    }

    /**
     * FR: Le panneau du menu de pause, superposé à l'écran de jeu.
     * EN: The pause menu panel, overlaid on the game screen.
     */
    private class PauseMenuPanel extends JPanel {
        public PauseMenuPanel() {
            setOpaque(false);
            setBackground(Theme.COLOR_TRANSPARENT_BG);
            setLayout(new GridBagLayout());
            var gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.gridx = 0;

            var pauseLabel = new JLabel("PAUSE");
            pauseLabel.setFont(Theme.FONT_TITLE);
            pauseLabel.setForeground(Theme.COLOR_TEXT);
            gbc.gridy = 0; add(pauseLabel, gbc);

            gbc.gridy = 1; add(new ModernButton("Reprendre") {{ addActionListener(_ -> showPanel(GameState.PLAYING)); }}, gbc);
            gbc.gridy = 2; add(new ModernButton("Menu Principal") {{ addActionListener(_ -> {
                gameEngine.handleGameOverOrQuit();
                showPanel(GameState.MAIN_MENU);
            }); }}, gbc);
            gbc.gridy = 3; add(new ModernButton("Quitter le Jeu") {{ addActionListener(_ -> {
                gameEngine.handleGameOverOrQuit();
                System.exit(0);
            }); }}, gbc);
        }

        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
            super.paintComponent(g);
        }
    }

    /**
     * FR: Panneau pour afficher la liste des meilleurs scores.
     * EN: Panel for displaying the list of high scores.
     */
    private class HighScoreDisplayPanel extends GradientPanel {
        private final JTextArea scoreArea = new JTextArea();

        public HighScoreDisplayPanel() {
            setLayout(new BorderLayout(20, 20));
            setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            var title = new JLabel("Meilleurs Scores", SwingConstants.CENTER);
            title.setFont(Theme.FONT_TITLE);
            title.setForeground(Theme.COLOR_TEXT);
            add(title, BorderLayout.NORTH);

            scoreArea.setFont(Theme.FONT_MONO);
            scoreArea.setBackground(Theme.COLOR_BACKGROUND_START);
            scoreArea.setForeground(Theme.COLOR_TEXT);
            scoreArea.setEditable(false);
            
            var scrollPane = new JScrollPane(scoreArea);
            scrollPane.setBorder(BorderFactory.createLineBorder(Theme.COLOR_ACCENT));
            add(scrollPane, BorderLayout.CENTER);

            var buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.setOpaque(false);
            buttonPanel.add(new ModernButton("Retour") {{ addActionListener(_ -> showPanel(GameState.MAIN_MENU)); }});
            add(buttonPanel, BorderLayout.SOUTH);
        }
        
        /**
         * FR: Met à jour l'affichage des scores en les rechargeant depuis le disque.
         * EN: Updates the score display by reloading them from the disk.
         */
        public void refreshScores() {
            scoreArea.setText("");
            var sb = new StringBuilder();
            gameEngine.getHighScoreManager().loadHighScores(); 
            var scores = gameEngine.getHighScoreManager().getHighScores();
            if (scores.isEmpty()) {
                sb.append("\n   Aucun score enregistré pour le moment.");
            } else {
                sb.append(String.format("\n   %-4s %-15s %10s\n", "Rang", "Joueur", "Score"));
                sb.append("   ----------------------------------\n");
                for (int i = 0; i < scores.size(); i++) {
                    var entry = scores.get(i);
                    sb.append(String.format("   #%-3d %-15s %10d\n", i + 1, entry.getPlayerName(), entry.getScore()));
                }
            }
            scoreArea.setText(sb.toString());
        }
    }

    /**
     * FR: Panneau interactif où le joueur place ses navires sur la grille.
     * EN: Interactive panel where the player places their ships on the grid.
     */
    private class ShipPlacementPanel extends GradientPanel implements MouseListener, MouseMotionListener, KeyListener {
        private int currentShipIndex;
        private Ship currentPlacingShip;
        private boolean isHorizontal = true;
        private Point mouseGridPos = new Point(-1, -1);
        private List<Ship> shipsToPlace;

        public ShipPlacementPanel() {
            this.addMouseListener(this);
            this.addMouseMotionListener(this);
            this.addKeyListener(this);
            this.setFocusable(true);
        }

        /**
         * FR: Initialise la phase de placement.
         * EN: Initializes the placement phase.
         */
        public void startPlacementPhase() {
            shipsToPlace = gameEngine.getShipsForCurrentLevel();
            currentShipIndex = 0;
            nextShipToPlace();
            this.requestFocusInWindow();
            repaint();
        }

        /**
         * FR: Prépare le prochain navire à être placé par le joueur.
         * EN: Prepares the next ship to be placed by the player.
         */
        private void nextShipToPlace() {
            if (currentShipIndex < shipsToPlace.size()) {
                currentPlacingShip = shipsToPlace.get(currentShipIndex);
                currentPlacingShip.setOrientation(isHorizontal);
            } else {
                // FR: Tous les navires sont placés, on lance le jeu.
                // EN: All ships are placed, start the game.
                currentPlacingShip = null;
                gameEngine.placeComputerShips();
                startGameplay();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            var g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // FR: Calcule le décalage pour centrer la grille.
            // EN: Calculates the offset to center the grid.
            var boardPixelSize = Theme.BOARD_SIZE * Theme.CELL_SIZE;
            int offsetX = (getWidth() - boardPixelSize) / 2;
            int offsetY = (getHeight() - boardPixelSize) / 2;

            // FR: Dessine les textes d'instruction.
            // EN: Draws the instruction texts.
            g2d.setFont(Theme.FONT_SUBTITLE);
            g2d.setColor(Theme.COLOR_TEXT);
            g2d.drawString("Placez vos Navires", getWidth() / 2 - 120, 50);

            var instruction = (currentPlacingShip != null)
                ? "Cliquez pour placer. 'R' pour pivoter. Navire : " + currentPlacingShip.getType() + " (Taille " + currentPlacingShip.getSize() + ")"
                : "Placement terminé. Lancement du jeu...";
            g2d.setFont(Theme.FONT_TEXT);
            g2d.drawString(instruction, getWidth() / 2 - 200, 80);

            // FR: Dessine la grille du joueur.
            // EN: Draws the player's grid.
            gameEngine.getPlayerBoard().draw(g2d, offsetX, offsetY, false);

            // FR: Dessine un "fantôme" du navire en cours de placement.
            // EN: Draws a "ghost" of the ship currently being placed.
            if (currentPlacingShip != null && mouseGridPos.x != -1) {
                currentPlacingShip.setPlacement(mouseGridPos.x, mouseGridPos.y, isHorizontal);
                boolean canPlace = gameEngine.getPlayerBoard().canPlaceShip(currentPlacingShip);
                currentPlacingShip.drawGhost(g2d, offsetX, offsetY, canPlace);
            }
        }

        /**
         * FR: Convertit les coordonnées en pixels de la souris en coordonnées de la grille.
         * EN: Converts mouse pixel coordinates to grid coordinates.
         */
        private Point getGridCoordinates(MouseEvent e) {
            var boardPixelSize = Theme.BOARD_SIZE * Theme.CELL_SIZE;
            int offsetX = (getWidth() - boardPixelSize) / 2;
            int offsetY = (getHeight() - boardPixelSize) / 2;
            if (e.getX() >= offsetX && e.getY() >= offsetY) {
                int gridX = (e.getX() - offsetX) / Theme.CELL_SIZE;
                int gridY = (e.getY() - offsetY) / Theme.CELL_SIZE;
                if (gridX < Theme.BOARD_SIZE && gridY < Theme.BOARD_SIZE) {
                    return new Point(gridX, gridY);
                }
            }
            return new Point(-1, -1); // FR: Hors de la grille. / EN: Outside the grid.
        }

        // --- Écouteurs d'événements / Event Listeners ---
        
        @Override public void mouseClicked(MouseEvent e) {
             if (e.getButton() == MouseEvent.BUTTON1 && currentPlacingShip != null && mouseGridPos.x != -1) {
                currentPlacingShip.setPlacement(mouseGridPos.x, mouseGridPos.y, isHorizontal);
                if (gameEngine.getPlayerBoard().placeShip(currentPlacingShip)) {
                    currentShipIndex++;
                    nextShipToPlace();
                }
            }
        }
        
        @Override public void mouseMoved(MouseEvent e) {
            Point newGridPos = getGridCoordinates(e);
            if (!newGridPos.equals(mouseGridPos)) {
                mouseGridPos = newGridPos;
                repaint();
            }
        }
        
        @Override public void keyPressed(KeyEvent e) {
            // FR: La touche 'R' pivote le navire. / EN: The 'R' key rotates the ship.
            if (e.getKeyCode() == KeyEvent.VK_R && currentPlacingShip != null) {
                isHorizontal = !isHorizontal;
                currentPlacingShip.setOrientation(isHorizontal);
                repaint();
            }
        }
        
        // FR: Méthodes d'interface non utilisées. / EN: Unused interface methods.
        @Override public void keyTyped(KeyEvent e) {}
        @Override public void keyReleased(KeyEvent e) {}
        @Override public void mousePressed(MouseEvent e) {}
        @Override public void mouseReleased(MouseEvent e) {}
        @Override public void mouseEntered(MouseEvent e) {}
        @Override public void mouseExited(MouseEvent e) {}
        @Override public void mouseDragged(MouseEvent e) {}
    }

    /**
     * FR: Le panneau principal du jeu, où se déroule l'action.
     * EN: The main game panel, where the action takes place.
     */
    private class GamePanel extends JPanel {
        private Timer gameLoopTimer; // FR: Pour redessiner l'écran à 60 FPS. / EN: For redrawing the screen at 60 FPS.
        private String animatedMessage = null; // FR: Message animé (Touché, Manqué). / EN: Animated message (Hit, Miss).
        private float messageAlpha = 0.0f; // FR: Transparence du message. / EN: Transparency of the message.
        private Timer messageTimer; // FR: Timer pour l'animation du message. / EN: Timer for the message animation.

        public GamePanel() {
            setBackground(Theme.COLOR_BACKGROUND_START);
            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    // FR: Gère le clic du joueur sur la grille ennemie.
                    // EN: Handles the player's click on the enemy grid.
                    if (gameEngine.getCurrentState() == GameState.PLAYING && !gameEngine.isComputerTurn()) {
                        int boardSizePx = Theme.BOARD_SIZE * Theme.CELL_SIZE;
                        int offsetX = (getWidth() - boardSizePx) / 2;
                        
                        // FR: Vérifie si le clic est sur la grille ennemie (la grille du haut).
                        // EN: Checks if the click is on the enemy grid (the top grid).
                        if (e.getY() >= 100 && e.getY() < 100 + boardSizePx) {
                             if(e.getX() >= offsetX && e.getX() < offsetX + boardSizePx) {
                                int x = (e.getX() - offsetX) / Theme.CELL_SIZE;
                                int y = (e.getY() - 100) / Theme.CELL_SIZE;
                                gameEngine.playerMakeMove(x, y);
                             }
                        }
                    }
                }
            });
        }
        
        /**
         * FR: Affiche un message animé au centre de l'écran.
         * EN: Displays an animated message in the center of the screen.
         * @param message Le texte à afficher. / The text to display.
         */
        public void showAnimatedMessage(String message) {
            this.animatedMessage = message;
            this.messageAlpha = 1.0f;
            if (messageTimer != null && messageTimer.isRunning()) messageTimer.stop();
            
            // FR: Fait disparaître le message progressivement.
            // EN: Fades the message out progressively.
            messageTimer = new Timer(20, ae -> {
                messageAlpha -= 0.01f;
                if (messageAlpha <= 0) {
                    messageAlpha = 0;
                    animatedMessage = null;
                    ((Timer)ae.getSource()).stop();
                }
                repaint();
            });
            messageTimer.setInitialDelay(1000); // FR: Délai avant le début du fondu. / EN: Delay before the fade starts.
            messageTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            var g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // FR: Dessine le fond en dégradé. / EN: Draws the gradient background.
            var gp = new GradientPaint(0, 0, Theme.COLOR_BACKGROUND_START, 0, getHeight(), Theme.COLOR_BACKGROUND_END);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // FR: Laisse le moteur de jeu dessiner les grilles, etc.
            // EN: Lets the game engine draw the grids, etc.
            gameEngine.drawGame(g2d, getWidth(), getHeight());
            
            // FR: Dessine le message animé s'il y en a un.
            // EN: Draws the animated message if there is one.
            if (animatedMessage != null) {
                g2d.setFont(Theme.FONT_TITLE);
                g2d.setColor(new Color(255, 255, 255, (int)(messageAlpha * 255)));
                int textWidth = g2d.getFontMetrics().stringWidth(animatedMessage);
                g2d.drawString(animatedMessage, (getWidth() - textWidth) / 2, getHeight() / 2);
            }
        }

        /**
         * FR: Démarre la boucle de rendu principale du jeu.
         * EN: Starts the main rendering loop of the game.
         */
        public void startDrawingLoop() {
            if (gameLoopTimer != null && gameLoopTimer.isRunning()) gameLoopTimer.stop();
            gameLoopTimer = new Timer(1000 / 60, _ -> repaint()); // ~60 FPS
            gameLoopTimer.start();
        }
    }

    /**
     * FR: Le cœur logique du jeu. Gère l'état, les tours, les scores, et la logique de l'IA.
     * EN: The logical core of the game. Manages state, turns, scores, and AI logic.
     */
    private static class GameEngine {
        private final BattleshipPremiumV2 gameFrame;
        private GameState currentState;
        private Board playerBoard;
        private Board computerBoard;
        private final HighScoreManager highScores;
        private int currentLevel;
        private long playerScore;
        private String currentPlayerName = "Joueur";
        private boolean isComputerTurn = false;
        
        // FR: Liste des cibles prioritaires pour l'IA (après avoir touché un navire).
        // EN: List of priority targets for the AI (after hitting a ship).
        private final List<Point> huntTargets = new ArrayList<>();
        private final Random random = new Random();

        public GameEngine(BattleshipPremiumV2 gameFrame) {
            this.gameFrame = gameFrame;
            this.highScores = new HighScoreManager();
            this.currentState = GameState.MAIN_MENU;
        }

        /**
         * FR: Initialise une session de jeu complète (score, niveau 1).
         * EN: Initializes a full game session (score, level 1).
         */
        public void initGame() {
            playerScore = 0;
            currentLevel = 1;
            setupNextLevel();
        }

        /**
         * FR: Prépare le niveau suivant (ou le premier) sans réinitialiser le score.
         * EN: Prepares the next (or first) level without resetting the score.
         */
        public void setupNextLevel() {
            playerBoard = new Board();
            computerBoard = new Board();
            huntTargets.clear();
            isComputerTurn = false;
        }

        /**
         * FR: Retourne la liste des navires à placer pour le niveau actuel.
         * EN: Returns the list of ships to be placed for the current level.
         */
        public List<Ship> getShipsForCurrentLevel() {
            var ships = new ArrayList<Ship>();
            // FR: Le nombre de navires augmente avec les niveaux.
            // EN: The number of ships increases with levels.
            int numShips = Math.min(currentLevel / 2 + 2, 5);
            ships.add(new Ship(5, "Porte-avions"));
            ships.add(new Ship(4, "Croiseur"));
            ships.add(new Ship(3, "Destroyer"));
            ships.add(new Ship(3, "Sous-marin"));
            ships.add(new Ship(2, "Torpilleur"));
            return ships.subList(0, Math.min(numShips, ships.size()));
        }

        /**
         * FR: Place aléatoirement les navires de l'ordinateur sur sa grille.
         * EN: Randomly places the computer's ships on its grid.
         */
        public void placeComputerShips() {
            var shipsToPlace = getShipsForCurrentLevel();
            for (var ship : shipsToPlace) {
                while (!computerBoard.placeShipRandomly(ship, random));
            }
        }

        /**
         * FR: Gère une tentative de tir du joueur.
         * EN: Handles a shot attempt from the player.
         * @param x Coordonnée X. / X coordinate.
         * @param y Coordonnée Y. / Y coordinate.
         */
        public void playerMakeMove(int x, int y) {
            if (currentState != GameState.PLAYING || isComputerTurn) return;

            if (computerBoard.isShot(x, y)) {
                gameFrame.getGamePanel().showAnimatedMessage("Déjà tiré ici!");
                return;
            }

            boolean hit = computerBoard.shoot(x, y);
            if (hit) {
                playerScore += 100 * currentLevel;
                gameFrame.getGamePanel().showAnimatedMessage("Touché !");
            } else {
                gameFrame.getGamePanel().showAnimatedMessage("Manqué !");
            }

            // FR: Si la partie n'est pas terminée, c'est au tour de l'ordinateur.
            // EN: If the game is not over, it's the computer's turn.
            if (checkGameStatus()) {
                 initiateComputerTurn();
            }
        }

        /**
         * FR: Déclenche et exécute le tour de l'ordinateur (IA).
         * EN: Triggers and executes the computer's turn (AI).
         */
        private void initiateComputerTurn() {
            isComputerTurn = true;

            // FR: Un timer pour simuler une pause de réflexion de l'IA.
            // EN: A timer to simulate a thinking pause for the AI.
            var computerMoveTimer = new Timer(1500, _ -> {
                Point target;
                // FR: Mode "chasse" : si une cible prioritaire existe, on la vise.
                // EN: "Hunt" mode: if a priority target exists, aim for it.
                if (!huntTargets.isEmpty()) {
                    target = huntTargets.remove(0);
                } else {
                    // FR: Mode "recherche" : tir aléatoire sur une case non touchée.
                    // EN: "Search" mode: random shot on an untouched cell.
                    do {
                        target = new Point(random.nextInt(Theme.BOARD_SIZE), random.nextInt(Theme.BOARD_SIZE));
                    } while (playerBoard.isShot(target.x, target.y));
                }

                boolean hit = playerBoard.shoot(target.x, target.y);
                if (hit) {
                    addHuntTargets(target.x, target.y); // FR: Ajoute les cases adjacentes aux cibles. / EN: Adds adjacent cells to targets.
                    var sunkShip = playerBoard.getShipAt(target.x, target.y);
                    if (sunkShip != null && sunkShip.isSunk()) {
                        huntTargets.clear(); // FR: Le navire est coulé, on arrête la chasse. / EN: The ship is sunk, stop hunting.
                        gameFrame.getGamePanel().showAnimatedMessage("L'ennemi a coulé un navire!");
                    } else {
                        gameFrame.getGamePanel().showAnimatedMessage("L'ennemi a touché!");
                    }
                }

                checkGameStatus();
                isComputerTurn = false;
            });
            computerMoveTimer.setRepeats(false);
            computerMoveTimer.start();
        }

        /**
         * FR: Ajoute les cases adjacentes à une touche réussie à la liste des cibles de l'IA.
         * EN: Adds adjacent cells of a successful hit to the AI's target list.
         */
        private void addHuntTargets(int x, int y) {
            int[] dx = {0, 0, 1, -1};
            int[] dy = {1, -1, 0, 0};
            for (int i = 0; i < 4; i++) {
                int nx = x + dx[i];
                int ny = y + dy[i];
                if (playerBoard.isValidCoordinate(nx, ny) && !playerBoard.isShot(nx, ny)) {
                    huntTargets.add(new Point(nx, ny));
                }
            }
            Collections.shuffle(huntTargets); // FR: Mélange pour éviter un comportement prévisible. / EN: Shuffle to avoid predictable behavior.
        }

        /**
         * FR: Vérifie si la partie est terminée (victoire du joueur ou de l'ordinateur).
         * EN: Checks if the game is over (player or computer victory).
         * @return FR: `false` si la partie est terminée, `true` sinon. / EN: `false` if the game is over, `true` otherwise.
         */
        private boolean checkGameStatus() {
            if (computerBoard.areAllShipsSunk()) {
                playerScore += 500 * currentLevel;
                levelUp();
                return false;
            } else if (playerBoard.areAllShipsSunk()) {
                setGameOver();
                return false;
            }
            return true;
        }

        /**
         * FR: Fait passer le joueur au niveau supérieur ou termine la partie s'il a gagné.
         * EN: Moves the player to the next level or ends the game if they have won.
         */
        private void levelUp() {
            currentLevel++;
            if (currentLevel > Theme.MAX_LEVEL) {
                setGameWon();
            } else {
                gameFrame.getGamePanel().showAnimatedMessage("Niveau " + currentLevel);
                setupNextLevel();
                gameFrame.showPanel(GameState.SHIP_PLACEMENT);
            }
        }

        /**
         * FR: Gère la condition de défaite du joueur.
         * EN: Handles the player's defeat condition.
         */
        private void setGameOver() {
            currentState = GameState.GAME_OVER;
            gameFrame.getGamePanel().showAnimatedMessage("GAME OVER");
            handleGameOverOrQuit();
            var t = new Timer(3000, _ -> gameFrame.showPanel(GameState.MAIN_MENU));
            t.setRepeats(false);
            t.start();
        }

        /**
         * FR: Gère la condition de victoire du joueur.
         * EN: Handles the player's victory condition.
         */
        private void setGameWon() {
            currentState = GameState.GAME_OVER;
            playerScore += 1000 * Theme.MAX_LEVEL;
            gameFrame.getGamePanel().showAnimatedMessage("VICTOIRE !");
            handleGameOverOrQuit();
            var t = new Timer(4000, _ -> gameFrame.showPanel(GameState.MAIN_MENU));
            t.setRepeats(false);
            t.start();
        }

        /**
         * FR: Gère la sauvegarde du score à la fin d'une partie.
         * EN: Handles saving the score at the end of a game.
         */
        public void handleGameOverOrQuit() {
            if (highScores.isNewHighScore(playerScore)) {
                String name = JOptionPane.showInputDialog(gameFrame, "Nouveau Highscore ! Entrez votre pseudo:", currentPlayerName);
                if (name == null || name.trim().isEmpty()) { name = "Anonyme"; }
                currentPlayerName = name;
                highScores.addScore(currentPlayerName, playerScore);
                highScores.saveHighScores();
            }
        }

        /**
         * FR: Dessine tous les éléments du jeu (grilles, score, etc.).
         * EN: Draws all game elements (grids, score, etc.).
         */
        public void drawGame(Graphics2D g2d, int panelWidth, int panelHeight) {
            int boardSizePx = Theme.BOARD_SIZE * Theme.CELL_SIZE;
            int offsetX = (panelWidth - boardSizePx) / 2;
            int playerOffsetY = 100 + boardSizePx + 50;
            
            g2d.setColor(Theme.COLOR_TEXT);
            g2d.setFont(Theme.FONT_SUBTITLE);
            g2d.drawString("Grille Ennemie", offsetX, 90);
            computerBoard.draw(g2d, offsetX, 100, true); // hideShips = true
            
            g2d.drawString("Votre Grille", offsetX, playerOffsetY - 10);
            playerBoard.draw(g2d, offsetX, playerOffsetY, false); // hideShips = false

            g2d.setFont(Theme.FONT_BUTTON);
            g2d.drawString("Score: " + getPlayerScore(), 20, 40);
            g2d.drawString("Niveau: " + getCurrentLevel(), 20, 70);
        }
        
        // --- Getters & Setters ---
        public GameState getCurrentState() { return currentState; }
        public void setCurrentState(GameState state) { this.currentState = state; }
        public Board getPlayerBoard() { return playerBoard; }
        public long getPlayerScore() { return playerScore; }
        public int getCurrentLevel() { return currentLevel; }
        public HighScoreManager getHighScoreManager() { return highScores; }
        public boolean isComputerTurn() { return isComputerTurn; }
    }

    /**
     * FR: Représente une grille de jeu (du joueur ou de l'ordinateur).
     * EN: Represents a game grid (for the player or the computer).
     */
    private static class Board {
        // FR: '~': Eau, 'S': Navire, 'H': Touché, 'M': Manqué
        // EN: '~': Water, 'S': Ship, 'H': Hit, 'M': Miss
        private final char[][] grid = new char[Theme.BOARD_SIZE][Theme.BOARD_SIZE];
        private final List<Ship> ships = new ArrayList<>();

        public Board() {
            clearShips();
        }

        public void clearShips() {
            ships.clear();
            for (int i = 0; i < Theme.BOARD_SIZE; i++) {
                for (int j = 0; j < Theme.BOARD_SIZE; j++) {
                    grid[i][j] = '~';
                }
            }
        }
        
        public boolean isValidCoordinate(int x, int y) {
             return x >= 0 && x < Theme.BOARD_SIZE && y >= 0 && y < Theme.BOARD_SIZE;
        }

        /**
         * FR: Vérifie si un navire peut être placé aux coordonnées données.
         * EN: Checks if a ship can be placed at the given coordinates.
         */
        public boolean canPlaceShip(Ship ship) {
             for(var p : ship.getOccupiedCells()) {
                 if (!isValidCoordinate(p.x, p.y) || grid[p.x][p.y] == 'S') {
                     return false; // FR: Hors de la grille ou sur un autre navire. / EN: Outside the grid or on another ship.
                 }
             }
             return true;
        }

        /**
         * FR: Place un navire sur la grille.
         * EN: Places a ship on the grid.
         */
        public boolean placeShip(Ship ship) {
            if(canPlaceShip(ship)) {
                for(var p : ship.getOccupiedCells()) grid[p.x][p.y] = 'S';
                ships.add(ship);
                return true;
            }
            return false;
        }

        /**
         * FR: Tente de placer un navire à une position aléatoire.
         * EN: Tries to place a ship at a random position.
         */
        public boolean placeShipRandomly(Ship ship, Random rand) {
            int attempts = 0;
            while (attempts < 100) {
                ship.setPlacement(rand.nextInt(Theme.BOARD_SIZE), rand.nextInt(Theme.BOARD_SIZE), rand.nextBoolean());
                if (placeShip(ship)) return true;
                attempts++;
            }
            return false; // FR: Échec après 100 tentatives. / EN: Failed after 100 attempts.
        }

        /**
         * FR: Enregistre un tir à une coordonnée.
         * EN: Registers a shot at a coordinate.
         * @return `true` si un navire est touché, `false` sinon.
         */
        public boolean shoot(int x, int y) {
            if (!isValidCoordinate(x, y) || isShot(x,y)) return false;
            
            if (grid[x][y] == 'S') {
                grid[x][y] = 'H';
                for (var ship : ships) if (ship.isHit(x, y)) break;
                return true;
            } else {
                grid[x][y] = 'M';
                return false;
            }
        }
        
        public Ship getShipAt(int x, int y) {
             for (var ship : ships) {
                for(var p : ship.getOccupiedCells()) {
                    if (p.x == x && p.y == y) return ship;
                }
            }
            return null;
        }
        
        public boolean isShot(int x, int y) { return grid[x][y] == 'H' || grid[x][y] == 'M'; }
        public boolean areAllShipsSunk() { return ships.stream().allMatch(Ship::isSunk); }

        /**
         * FR: Dessine la grille et son contenu.
         * EN: Draws the grid and its contents.
         * @param hideShips FR: Si vrai, ne dessine pas les navires intacts (pour la grille ennemie).
         * EN: If true, does not draw intact ships (for the enemy grid).
         */
        public void draw(Graphics2D g2d, int offsetX, int offsetY, boolean hideShips) {
            for (int i = 0; i < Theme.BOARD_SIZE; i++) {
                for (int j = 0; j < Theme.BOARD_SIZE; j++) {
                    int x = offsetX + i * Theme.CELL_SIZE;
                    int y = offsetY + j * Theme.CELL_SIZE;
                    
                    g2d.setColor(Theme.COLOR_GRID_BG);
                    g2d.fillRect(x, y, Theme.CELL_SIZE, Theme.CELL_SIZE);
                    g2d.setColor(Theme.COLOR_GRID_LINE);
                    g2d.drawRect(x, y, Theme.CELL_SIZE, Theme.CELL_SIZE);

                    switch (grid[i][j]) {
                        case 'H' -> {
                            g2d.setColor(Theme.COLOR_HIT);
                            g2d.fillRect(x+2, y+2, Theme.CELL_SIZE-4, Theme.CELL_SIZE-4);
                        }
                        case 'M' -> {
                            g2d.setColor(Theme.COLOR_MISS);
                            g2d.fillOval(x + Theme.CELL_SIZE / 2 - 4, y + Theme.CELL_SIZE / 2 - 4, 8, 8);
                        }
                    }
                }
            }
            if (!hideShips) {
                for (var ship : ships) ship.draw(g2d, offsetX, offsetY);
            }
        }
    }

    /**
     * FR: Représente un navire avec sa taille, son type et son état.
     * EN: Represents a ship with its size, type, and state.
     */
    private static class Ship {
        private final int size;
        private final String type;
        private int hitCount;
        private int startX, startY;
        private boolean isHorizontal;
        private List<Point> occupiedCells = new ArrayList<>();

        public Ship(int size, String type) {
            this.size = size;
            this.type = type;
        }

        public void setPlacement(int startX, int startY, boolean isHorizontal) {
            this.startX = startX;
            this.startY = startY;
            this.isHorizontal = isHorizontal;
            this.occupiedCells = calculateOccupiedCells(startX, startY);
        }

        /**
         * FR: Enregistre une touche sur ce navire.
         * EN: Registers a hit on this ship.
         * @return `true` si le tir a touché ce navire, `false` sinon.
         */
        public boolean isHit(int x, int y) {
            if (occupiedCells.contains(new Point(x,y))) {
                hitCount++;
                return true;
            }
            return false;
        }

        /**
         * FR: Calcule toutes les cellules occupées par le navire.
         * EN: Calculates all the cells occupied by the ship.
         */
        private List<Point> calculateOccupiedCells(int startX, int startY) {
            var cells = new ArrayList<Point>();
            for (int i = 0; i < size; i++) {
                cells.add(new Point(isHorizontal ? startX + i : startX, isHorizontal ? startY : startY + i));
            }
            return cells;
        }

        /**
         * FR: Dessine le navire sur la grille du joueur.
         * EN: Draws the ship on the player's grid.
         */
        public void draw(Graphics2D g2d, int offsetX, int offsetY) {
            g2d.setColor(Theme.COLOR_SHIP);
            var drawX = offsetX + startX * Theme.CELL_SIZE;
            var drawY = offsetY + startY * Theme.CELL_SIZE;
            var width = isHorizontal ? size * Theme.CELL_SIZE : Theme.CELL_SIZE;
            var height = isHorizontal ? Theme.CELL_SIZE : size * Theme.CELL_SIZE;
            g2d.fillRect(drawX, drawY, width, height);
            g2d.setColor(Color.WHITE);
            g2d.drawRect(drawX, drawY, width, height);
        }
        
        /**
         * FR: Dessine une prévisualisation du navire lors du placement.
         * EN: Draws a preview of the ship during placement.
         */
        public void drawGhost(Graphics2D g2d, int offsetX, int offsetY, boolean canPlace) {
             g2d.setColor(canPlace ? Theme.COLOR_GHOST_OK : Theme.COLOR_GHOST_BAD);
            var drawX = offsetX + startX * Theme.CELL_SIZE;
            var drawY = offsetY + startY * Theme.CELL_SIZE;
            var width = isHorizontal ? size * Theme.CELL_SIZE : Theme.CELL_SIZE;
            var height = isHorizontal ? Theme.CELL_SIZE : size * Theme.CELL_SIZE;
            g2d.fillRect(drawX, drawY, width, height);
        }
        
        // --- Getters & Setters ---
        public int getSize() { return size; }
        public String getType() { return type; }
        public boolean isSunk() { return hitCount >= size; }
        public void setOrientation(boolean isHorizontal) { this.isHorizontal = isHorizontal; }
        public List<Point> getOccupiedCells() { return occupiedCells; }
    }

    /**
     * FR: Représente une entrée dans le tableau des meilleurs scores.
     * EN: Represents an entry in the high score table.
     */
    private static class HighScoreEntry implements Comparable<HighScoreEntry> {
        private final String playerName;
        private final long score;

        public HighScoreEntry(String playerName, long score) {
            this.playerName = playerName;
            this.score = score;
        }
        public String getPlayerName() { return playerName; }
        public long getScore() { return score; }

        // FR: Compare les scores pour le tri (ordre décroissant).
        // EN: Compares scores for sorting (descending order).
        @Override public int compareTo(HighScoreEntry other) { return Long.compare(other.score, this.score); }
        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            var that = (HighScoreEntry) o;
            return score == that.score && playerName.equals(that.playerName);
        }
        @Override public int hashCode() { return Objects.hash(playerName, score); }
    }
    
    /**
     * FR: Gère la lecture, l'écriture et la maintenance des meilleurs scores.
     * EN: Manages the reading, writing, and maintenance of high scores.
     */
    private static class HighScoreManager {
        private static final String HIGHSCORE_FILE = "highscores_premium.json";
        private static final int MAX_HIGHSCORES = 10;
        private List<HighScoreEntry> highScores;

        public HighScoreManager() {
            this.highScores = new ArrayList<>();
            loadHighScores();
        }

        /**
         * FR: Ajoute un nouveau score et maintient la liste triée et limitée en taille.
         * EN: Adds a new score and keeps the list sorted and limited in size.
         */
        public void addScore(String playerName, long score) {
            highScores.add(new HighScoreEntry(playerName, score));
            Collections.sort(highScores);
            if (highScores.size() > MAX_HIGHSCORES) {
                highScores = highScores.subList(0, MAX_HIGHSCORES);
            }
        }

        /**
         * FR: Charge les scores depuis le fichier JSON. Gère les erreurs de formatage.
         * EN: Loads scores from the JSON file. Handles formatting errors.
         */
        public void loadHighScores() {
            this.highScores.clear();
            var file = Paths.get(HIGHSCORE_FILE);
            if (!Files.exists(file)) {
                return; // FR: Normal au premier lancement. / EN: Normal on first launch.
            }

            try {
                String content = Files.readString(file, StandardCharsets.UTF_8);

                Pattern entryPattern = Pattern.compile("\\{.*?\\}", Pattern.DOTALL);
                Matcher entryMatcher = entryPattern.matcher(content);

                while (entryMatcher.find()) {
                    String entryBlock = entryMatcher.group();
                    Pattern namePattern = Pattern.compile("\"playerName\"\\s*:\\s*\"(.*?)\"");
                    Matcher nameMatcher = namePattern.matcher(entryBlock);
                    Pattern scorePattern = Pattern.compile("\"score\"\\s*:\\s*(\\d+)");
                    Matcher scoreMatcher = scorePattern.matcher(entryBlock);

                    if (nameMatcher.find() && scoreMatcher.find()) {
                        String name = nameMatcher.group(1);
                        long score = Long.parseLong(scoreMatcher.group(1));
                        this.highScores.add(new HighScoreEntry(name, score));
                    }
                }
                Collections.sort(this.highScores);

            } catch (IOException | NumberFormatException e) {
                System.err.println("Erreur lors du chargement des scores : " + e.getMessage());
                JOptionPane.showMessageDialog(null,
                        "Impossible de lire le fichier des scores.\nIl sera réinitialisé à la prochaine sauvegarde.",
                        "Erreur de Lecture", JOptionPane.WARNING_MESSAGE);
                this.highScores.clear();
            }
        }

        /**
         * FR: Sauvegarde la liste des scores dans le fichier JSON.
         * EN: Saves the list of scores to the JSON file.
         */
        public void saveHighScores() {
            var sb = new StringBuilder("[\n");
            for (int i = 0; i < highScores.size(); i++) {
                var entry = highScores.get(i);
                // FR: Échappe les caractères spéciaux pour un JSON valide.
                // EN: Escapes special characters for valid JSON.
                String safePlayerName = entry.getPlayerName()
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"");

                sb.append("  {\n");
                sb.append("    \"playerName\": \"").append(safePlayerName).append("\",\n");
                sb.append("    \"score\": ").append(entry.getScore()).append("\n");
                sb.append("  }");
                if (i < highScores.size() - 1) {
                    sb.append(",\n");
                } else {
                    sb.append("\n");
                }
            }
            sb.append("]");

            try (var writer = Files.newBufferedWriter(Paths.get(HIGHSCORE_FILE), StandardCharsets.UTF_8)) {
                writer.write(sb.toString());
            } catch (IOException e) {
                 System.err.println("Erreur critique lors de la sauvegarde des scores : " + e.getMessage());
                 JOptionPane.showMessageDialog(null,
                         "Impossible d'écrire dans le fichier des scores.\n" +
                         "Vérifiez les permissions du dossier où le jeu est lancé.",
                         "Erreur de Sauvegarde", JOptionPane.ERROR_MESSAGE);
            }
        }

        public List<HighScoreEntry> getHighScores() { return highScores; }

        /**
         * FR: Vérifie si un score est assez élevé pour entrer dans le top 10.
         * EN: Checks if a score is high enough to enter the top 10.
         */
        public boolean isNewHighScore(long score) {
            if (score <= 0) {
                return false;
            }
            if (highScores.size() < MAX_HIGHSCORES) {
                return true;
            }
            return score > highScores.get(highScores.size() - 1).getScore();
        }
    }
}