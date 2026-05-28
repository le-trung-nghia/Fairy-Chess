package com.chess.gui;

import com.chess.logic.state.BoardPiece;
import com.chess.logic.state.GameState;
import com.chess.logic.state.PieceState;
import com.chess.logic.types.Piece;
import com.chess.logic.types.Position;
import com.chess.registry.PiecePath;
import com.chess.registry.PieceRegistry;

import java.io.File;
import java.net.URISyntaxException;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class App extends Application {

    // Window / board constants
    private static final int SQUARE_SIZE = 70;
    private static final int MARGIN = 20;
    private static final int SIDE_PANE_W = 250;
    private static final int BOARD_PX = SQUARE_SIZE * 8; // 560
    private static final int WINDOW_W = MARGIN + BOARD_PX + MARGIN + SIDE_PANE_W + MARGIN; // 870
    private static final int WINDOW_H = BOARD_PX + MARGIN * 2; // 600

    // Game state
    private GameState logic;
    private Pane boardPane;
    private Position selectedPosition = null;
    private String[][] validMoves = null;

    // History: each entry is {fromPos, toPos}; viewIndex 0 = initial board
    private final List<Position[]> moveHistory = new ArrayList<>();
    private int viewIndex = 0;

    // Timer
    private int whiteTimeSeconds;
    private int blackTimeSeconds;
    private Timeline gameTimer;

    // Piece-pack registry (loaded once at startup)
    private final PieceRegistry registry = new PieceRegistry();

    // Live UI handles (set when game scene is built)
    private Label whiteTimerLabel;
    private Label blackTimerLabel;
    private Label turnLabel;
    private VBox historyListBox;
    private Stage primaryStage;

    // JavaFX entry
    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        stage.getIcons().add(new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/icon.png"))));
        stage.setTitle("Fairy Chess 2026");
        stage.setResizable(false);
        loadPacks(); // scan packs/ folder before any scene is shown
        showMenuScene();
        stage.show();
    }

    /**
     * Scans the packs directory for JAR files and registers each one.
     * Location: the value of the JVM property "packs.dir", falling back to
     * a "packs" folder relative to the working directory.
     *
     * Each JAR must contain:
     * • META-INF/MANIFEST.MF with Pack-Name: <identifier>
     * • META-INF/services/com.chess.logic.types.Piece listing provider classes
     * • Piece image PNGs at the root of the JAR
     */
    private void loadPacks() {
        String jarPath = null;
        try {
            jarPath = App.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
                    .getPath();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        File jarFile = new File(jarPath);
        File jarDirectory = jarFile.isDirectory() ? jarFile : jarFile.getParentFile();
        File packsDir = new File(jarDirectory, "packs");
        if (!packsDir.isDirectory()) {
            System.out.println("[packs] directory not found: " + packsDir.getAbsolutePath());
            return;
        }
        File[] jars = packsDir.listFiles(f -> f.getName().endsWith(".jar"));
        if (jars == null || jars.length == 0) {
            System.out.println("[packs] no JARs found in " + packsDir.getAbsolutePath());
            return;
        }
        for (File jar : jars) {
            try {
                registry.registerPiecePack(jar);
                System.out.println("[packs] loaded: " + jar.getName());
            } catch (RuntimeException e) {
                System.err.println("[packs] failed to load " + jar.getName() + ": " + e.getMessage());
            }
        }
    }

    // Scenes: Menu -> Time Select/Settings -> Game
    private void showMenuScene() {
        Text logo = new Text("FAIRY CHESS");
        logo.setFont(Font.font("Arial", FontWeight.BOLD, 62));
        logo.setFill(Color.WHITE);

        Button playBtn = mainButton("PLAY", 220);
        Button settingsBtn = mainButton("SETTINGS", 220);
        playBtn.setOnAction(e -> showTimeSelectScene());
        settingsBtn.setOnAction(e -> showSettingsScene());

        VBox box = new VBox(28, logo, playBtn, settingsBtn);
        box.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(box);
        root.setStyle("-fx-background-color: #2b2b2b;");
        primaryStage.setScene(new Scene(root, WINDOW_W, WINDOW_H));
    }

    private void showTimeSelectScene() {
        Text title = new Text("Select Time Control");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 34));
        title.setFill(Color.WHITE);

        Button b30 = mainButton("30 minutes", 220);
        Button b10 = mainButton("10 minutes", 220);
        Button b3 = mainButton(" 3 minutes", 220);
        Button back = mainButton("← Back", 220);
        back.setStyle(mainButtonCss(220).replace("#4a90d9", "#555555"));

        b30.setOnAction(e -> startGame(30 * 60));
        b10.setOnAction(e -> startGame(10 * 60));
        b3.setOnAction(e -> startGame(3 * 60));
        back.setOnAction(e -> showMenuScene());

        VBox box = new VBox(16, title, b30, b10, b3, back);
        box.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(box);
        root.setStyle("-fx-background-color: #2b2b2b;");
        primaryStage.setScene(new Scene(root, WINDOW_W, WINDOW_H));
    }

    private void showSettingsScene() {
        Text title = new Text("Settings");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 44));
        title.setFill(Color.WHITE);

        Text placeholder = new Text("Coming soon…");
        placeholder.setFont(Font.font("Arial", 22));
        placeholder.setFill(Color.LIGHTGRAY);

        Button back = mainButton("← Back", 200);
        back.setStyle(mainButtonCss(200).replace("#4a90d9", "#555555"));
        back.setOnAction(e -> showMenuScene());

        VBox box = new VBox(28, title, placeholder, back);
        box.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(box);
        root.setStyle("-fx-background-color: #2b2b2b;");
        primaryStage.setScene(new Scene(root, WINDOW_W, WINDOW_H));
    }

    // Game scene

    private void startGame(int timeSeconds) {
        // Reset all game state
        logic = new GameState();
        setupBoard(logic);
        moveHistory.clear();
        viewIndex = 0;
        selectedPosition = null;
        validMoves = null;
        whiteTimeSeconds = timeSeconds;
        blackTimeSeconds = timeSeconds;
        if (gameTimer != null)
            gameTimer.stop();

        // Layout
        Pane mainContainer = new Pane();
        mainContainer.setStyle("-fx-background-color: #505050;");

        // Board area
        boardPane = new Pane();
        Rectangle bgRect = new Rectangle(MARGIN, MARGIN, BOARD_PX, BOARD_PX);
        bgRect.setFill(Color.web("#808080"));
        mainContainer.getChildren().addAll(bgRect, boardPane);
        boardPane.setLayoutX(MARGIN);
        boardPane.setLayoutY(MARGIN);

        // Sidebar
        VBox sidebar = buildSidebar();
        sidebar.setLayoutX(MARGIN + BOARD_PX + MARGIN);
        sidebar.setLayoutY(MARGIN);
        sidebar.setPrefWidth(SIDE_PANE_W);
        sidebar.setPrefHeight(BOARD_PX);
        mainContainer.getChildren().add(sidebar);

        renderBoard(boardPane, logic);
        primaryStage.setScene(new Scene(mainContainer, WINDOW_W, WINDOW_H));
        startGameTimer();
    }

    private VBox buildSidebar() {
        // Turn indicator
        turnLabel = new Label("⬜  White to move");
        turnLabel.setStyle(
                "-fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold;");

        // Timers — black on top (they see the opponent clock first), white below
        blackTimerLabel = new Label("⬛  " + formatTime(blackTimeSeconds));
        whiteTimerLabel = new Label("⬜  " + formatTime(whiteTimeSeconds));
        blackTimerLabel.setMaxWidth(Double.MAX_VALUE);
        whiteTimerLabel.setMaxWidth(Double.MAX_VALUE);
        applyTimerStyle(whiteTimerLabel, true); // white moves first → active
        applyTimerStyle(blackTimerLabel, false);

        // History header
        Label historyHeader = new Label("Move History");
        historyHeader.setStyle(
                "-fx-text-fill: #aaaaaa; -fx-font-size: 12; -fx-font-weight: bold;" +
                        "-fx-padding: 6 0 2 0;");

        // History list inside a scroll pane
        historyListBox = new VBox(2);
        historyListBox.setStyle("-fx-padding: 2 0;");

        ScrollPane scroll = new ScrollPane(historyListBox);
        scroll.setStyle(
                "-fx-background: #2b2b2b; -fx-background-color: #2b2b2b;" +
                        "-fx-border-color: transparent;");
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        // Navigation buttons
        Button backBtn = sideButton("◀ Back");
        Button fwdBtn = sideButton("Forward ▶");
        backBtn.setOnAction(e -> navigateHistory(-1));
        fwdBtn.setOnAction(e -> navigateHistory(+1));
        HBox nav = new HBox(6, backBtn, fwdBtn);
        nav.setAlignment(Pos.CENTER);

        VBox sidebar = new VBox(8,
                turnLabel,
                blackTimerLabel,
                whiteTimerLabel,
                historyHeader,
                scroll,
                nav);
        sidebar.setStyle(
                "-fx-background-color: #3a3a3a;" +
                        "-fx-border-color: #222222; -fx-border-width: 1;" +
                        "-fx-padding: 10;");
        return sidebar;
    }

    // Timer
    private void startGameTimer() {
        gameTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> tickTimer()));
        gameTimer.setCycleCount(Timeline.INDEFINITE);
        gameTimer.play();
    }

    private void tickTimer() {
        boolean whiteTurn = logic.turnPlayer() == com.chess.logic.types.Color.WHITE;
        if (whiteTurn) {
            if (--whiteTimeSeconds <= 0) {
                whiteTimeSeconds = 0;
                gameTimer.stop();
                onTimeout(false);
                return;
            }
        } else {
            if (--blackTimeSeconds <= 0) {
                blackTimeSeconds = 0;
                gameTimer.stop();
                onTimeout(true);
                return;
            }
        }
        refreshTimerUI();
    }

    private void onTimeout(boolean whiteWins) {
        refreshTimerUI();
        turnLabel.setText(whiteWins ? "⬜  White wins on time!" : "⬛  Black wins on time!");
        turnLabel.setStyle("-fx-text-fill: #f0c040; -fx-font-size: 13; -fx-font-weight: bold;");
    }

    private void refreshTimerUI() {
        boolean whiteTurn = logic.turnPlayer() == com.chess.logic.types.Color.WHITE;
        applyTimerStyle(whiteTimerLabel, whiteTurn);
        applyTimerStyle(blackTimerLabel, !whiteTurn);
        whiteTimerLabel.setText("⬜  " + formatTime(whiteTimeSeconds));
        blackTimerLabel.setText("⬛  " + formatTime(blackTimeSeconds));
        if (gameTimer != null && gameTimer.getStatus() == Timeline.Status.RUNNING) {
            turnLabel.setText(whiteTurn ? "⬜  White to move" : "⬛  Black to move");
            turnLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold;");
        }
    }

    private void applyTimerStyle(Label lbl, boolean active) {
        String base = "-fx-font-size: 18; -fx-font-weight: bold;" +
                "-fx-padding: 6 10; -fx-background-radius: 0;";
        if (active) {
            lbl.setStyle("-fx-text-fill: white; -fx-background-color: #505050;" + base);
        } else {
            lbl.setStyle("-fx-text-fill: #777777; -fx-background-color: #2b2b2b;" + base);
        }
    }

    private String formatTime(int secs) {
        return String.format("%02d:%02d", secs / 60, secs % 60);
    }

    // Move history & replay
    private void navigateHistory(int delta) {
        int next = viewIndex + delta;
        if (next < 0 || next > moveHistory.size())
            return;
        viewIndex = next;
        selectedPosition = null;
        validMoves = null;
        updateHistoryList();
        redrawBoard();
    }

    private void updateHistoryList() {
        historyListBox.getChildren().clear();
        for (int i = 0; i < moveHistory.size(); i++) {
            boolean isWhite = (i % 2 == 0);
            String prefix = isWhite ? (i / 2 + 1) + ".  " : "      ";
            String text = prefix + formatMove(moveHistory.get(i));
            boolean current = (i == viewIndex - 1);

            Label lbl = new Label(text);
            lbl.setMaxWidth(Double.MAX_VALUE);
            if (current) {
                lbl.setStyle(
                        "-fx-text-fill: #1a1a1a; -fx-font-size: 13;" +
                                "-fx-background-color: #f0c040;" +
                                "-fx-padding: 3 6; -fx-background-radius: 0;");
            } else {
                lbl.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 13; -fx-padding: 3 6;");
            }

            final int jumpTo = i + 1;
            lbl.setOnMouseClicked(e -> {
                viewIndex = jumpTo;
                selectedPosition = null;
                validMoves = null;
                updateHistoryList();
                redrawBoard();
            });
            historyListBox.getChildren().add(lbl);
        }
    }

    private String formatMove(Position[] move) {
        return toAlgebraic(move[0]) + " → " + toAlgebraic(move[1]);
    }

    private String toAlgebraic(Position p) {
        return "" + (char) ('a' + p.col()) + (8 - p.row());
    }

    // Board rendering
    public void renderBoard(Pane pane, GameState state) {
        // 1. Board background image
        ImageView bg = new ImageView(new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/Chess_Board.png"))));
        bg.setFitWidth(BOARD_PX);
        bg.setFitHeight(BOARD_PX);
        bg.setPreserveRatio(false);
        pane.getChildren().add(bg);

        // 2. Click-detection squares (disabled during replay)
        boolean inReplay = viewIndex < moveHistory.size();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                int r = row, c = col;
                Rectangle sq = new Rectangle(
                        col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
                sq.setFill(Color.TRANSPARENT);
                if (!inReplay)
                    sq.setOnMouseClicked(ev -> handleSquareClick(r, c));
                pane.getChildren().add(sq);
            }
        }

        // 3. Move highlights (live mode only)
        if (!inReplay && selectedPosition != null && validMoves != null) {
            renderMoveHighlights(pane, validMoves, state);
        }

        // 4. Pieces
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                BoardPiece piece = state.getSquare(new Position(row, col));
                if (piece != null)
                    addPieceToBoard(pane, piece, row, col);
            }
        }
    }

    private void renderMoveHighlights(Pane pane, String[][] moves, GameState state) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (moves[row][col] == null)
                    continue;
                double x = col * SQUARE_SIZE;
                double y = row * SQUARE_SIZE;

                if (moves[row][col].equals("attack.png")) {
                    // Red border ring around capturable enemy pieces
                    Rectangle ring = new Rectangle(x, y, SQUARE_SIZE, SQUARE_SIZE);
                    ring.setFill(Color.TRANSPARENT);
                    ring.setStroke(Color.color(0.85, 0.1, 0.1, 0.8));
                    ring.setStrokeWidth(5);
                    ring.setMouseTransparent(true);
                    pane.getChildren().add(ring);
                } else {
                    // Dark dot on empty reachable squares
                    Circle dot = new Circle(
                            x + SQUARE_SIZE / 2.0, y + SQUARE_SIZE / 2.0, SQUARE_SIZE / 4.5);
                    dot.setFill(Color.color(0.1, 0.1, 0.1, 0.35));
                    dot.setMouseTransparent(true);
                    pane.getChildren().add(dot);
                }
            }
        }
    }

    private void addPieceToBoard(Pane pane, BoardPiece piece, int row, int col) {
        Image img = new Image(Objects.requireNonNull(
                piece.iconStream(), "Missing icon: " + piece.icon()));
        ImageView iv = new ImageView(img);
        iv.setX(col * SQUARE_SIZE);
        iv.setY(row * SQUARE_SIZE);
        iv.setFitWidth(SQUARE_SIZE);
        iv.setFitHeight(SQUARE_SIZE);
        iv.setSmooth(true);
        if (selectedPosition != null
                && selectedPosition.row() == row
                && selectedPosition.col() == col) {
            iv.setOpacity(0.7);
        }
        iv.setMouseTransparent(true);
        pane.getChildren().add(iv);
    }

    // Click handling
    private void handleSquareClick(int row, int col) {
        Position clickedPos = new Position(row, col);
        BoardPiece clickedPiece = logic.getSquare(clickedPos);

        if (selectedPosition != null) {
            if (validMoves != null && validMoves[row][col] != null) {
                // Legal destination — execute move and record it
                try {
                    logic.commandMove(selectedPosition, clickedPos);
                    moveHistory.add(new Position[] { selectedPosition, clickedPos });
                    viewIndex = moveHistory.size();
                    updateHistoryList();
                    refreshTimerUI();
                } catch (Exception e) {
                    System.out.println("Move failed: " + e.getMessage());
                }
                selectedPosition = null;
                validMoves = null;
            } else if (clickedPiece != null
                    && clickedPiece.color() == logic.turnPlayer()) {
                // Switch selection to another friendly piece
                selectedPosition = clickedPos;
                validMoves = computeValidMoves(clickedPos, clickedPiece);
            } else {
                // Illegal square — deselect
                selectedPosition = null;
                validMoves = null;
            }
        } else {
            // Select a piece belonging to the current player
            if (clickedPiece != null && clickedPiece.color() == logic.turnPlayer()) {
                selectedPosition = clickedPos;
                validMoves = computeValidMoves(clickedPos, clickedPiece);
            }
        }

        redrawBoard();
    }

    private String[][] computeValidMoves(Position pos, BoardPiece piece) {
        try {
            return piece.getMovableSquares(logic);
        } catch (Exception e) {
            System.out.println("Could not compute moves for " + pos + ": " + e.getMessage());
            return null;
        }
    }

    private void redrawBoard() {
        if (boardPane == null)
            return;
        boardPane.getChildren().clear();
        // Show a snapshot when in replay mode, live state otherwise
        GameState display = (viewIndex < moveHistory.size())
                ? buildStateAtIndex(viewIndex)
                : logic;
        renderBoard(boardPane, display);
    }

    // Replay a fresh GameState up to the given move index
    private GameState buildStateAtIndex(int idx) {
        GameState state = new GameState();
        setupBoard(state);
        for (int i = 0; i < idx; i++) {
            try {
                state.commandMove(moveHistory.get(i)[0], moveHistory.get(i)[1]);
            } catch (Exception ignored) {
                break;
            }
        }
        return state;
    }

    // Board setup
    private void setupBoard(GameState state) {
        // Standard back ranks and pawn rows
        Piece[] back = { registry.instantiatePiece(new PiecePath("base", "rook")),
                registry.instantiatePiece(new PiecePath("base", "knight")),
                registry.instantiatePiece(new PiecePath("base", "bishop")),
                registry.instantiatePiece(new PiecePath("base", "queen")),
                registry.instantiatePiece(new PiecePath("base", "king")),
                registry.instantiatePiece(new PiecePath("base", "bishop")),
                registry.instantiatePiece(new PiecePath("base", "knight")),
                registry.instantiatePiece(new PiecePath("base", "rook")) };
        Piece[] backW = { registry.instantiatePiece(new PiecePath("base", "rook")),
                registry.instantiatePiece(new PiecePath("base", "knight")),
                registry.instantiatePiece(new PiecePath("base", "bishop")),
                registry.instantiatePiece(new PiecePath("base", "queen")),
                registry.instantiatePiece(new PiecePath("base", "king")),
                registry.instantiatePiece(new PiecePath("base", "bishop")),
                registry.instantiatePiece(new PiecePath("base", "knight")),
                registry.instantiatePiece(new PiecePath("base", "rook")) };
        for (int col = 0; col < 8; col++) {
            boolean king = (col == 4);
            state.place(back[col], new PieceState(king, com.chess.logic.types.Color.BLACK, new Position(0, col)));
            state.place(registry.instantiatePiece(new PiecePath("base", "pawn")),
                    new PieceState(false, com.chess.logic.types.Color.BLACK, new Position(1, col)));
            state.place(registry.instantiatePiece(new PiecePath("base", "pawn")),
                    new PieceState(false, com.chess.logic.types.Color.WHITE, new Position(6, col)));
            state.place(backW[col], new PieceState(king, com.chess.logic.types.Color.WHITE, new Position(7, col)));
        }

        // Place a Viking on column D (col 3) in front of each side's pawn row.
        // Black Viking: row 2 (one step ahead of black pawn at row 1)
        // White Viking: row 5 (one step ahead of white pawn at row 6)
        // Silently skipped if the viking pack is not loaded.

        // FIXME This is for demo, should be remove later
        placeFromRegistry(state, "viking", "viking",
                com.chess.logic.types.Color.BLACK, new Position(2, 3));
        placeFromRegistry(state, "viking", "viking",
                com.chess.logic.types.Color.WHITE, new Position(5, 3));
    }

    // Instantiates a piece from a loaded pack and places it on the board.
    // Logs a warning and continues if the pack or piece name is not found.
    private void placeFromRegistry(GameState state, String packName, String pieceName,
            com.chess.logic.types.Color color, Position pos) {
        try {
            Piece piece = registry.instantiatePiece(new PiecePath(packName, pieceName));
            state.place(piece, new PieceState(false, color, pos));
        } catch (Exception e) {
            System.out.println("[packs] could not place " + packName + ":" + pieceName
                    + " — " + e.getMessage());
        }
    }

    // UI helpers
    private String mainButtonCss(double width) {
        return "-fx-background-color: #4a90d9; -fx-text-fill: white;" +
                "-fx-font-size: 15; -fx-font-weight: bold;" +
                "-fx-min-width: " + width + ";" +
                "-fx-padding: 12 24; -fx-background-radius: 0; -fx-cursor: hand;";
    }

    private Button mainButton(String text, double width) {
        Button b = new Button(text);
        b.setStyle(mainButtonCss(width));
        return b;
    }

    private Button sideButton(String text) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color: #4a4a4a; -fx-text-fill: #cccccc;" +
                "-fx-font-size: 12; -fx-padding: 5 10;" +
                "-fx-background-radius: 0; -fx-cursor: hand;");
        return b;
    }

    public static void main(String[] args) {
        launch();
    }
}
