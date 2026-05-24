package com.chess.gui;

import com.chess.basepieces.Bishop;
import com.chess.basepieces.King;
import com.chess.basepieces.Knight;
import com.chess.basepieces.Pawn;
import com.chess.basepieces.Queen;
import com.chess.basepieces.Rook;
import com.chess.logic.state.BoardPiece;
import com.chess.logic.state.GameState;
import com.chess.logic.state.PieceState;
import com.chess.logic.types.Piece;
import com.chess.logic.types.Position;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class App extends Application {

	private static final int SQUARE_SIZE = 70;
	private GameState logic;
	private Pane boardPane;
	private Position selectedPosition = null;
	private String[][] validMoves = null;
	private static final int MARGIN = 20;
	private static final int SIDE_PANE_WIDTH = 250;

	public void renderBoard(Pane boardPane, GameState logic) {
		// 1. Board background
		ImageView background = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Chess_Board.png"))));
		int boardSize = SQUARE_SIZE * 8;
		background.setFitWidth(boardSize);
		background.setFitHeight(boardSize);
		background.setPreserveRatio(false);
		boardPane.getChildren().add(background);

		// 2. Invisible click-detection squares (below highlights and pieces)
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				int finalRow = row;
				int finalCol = col;
				Rectangle square = new Rectangle(col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
				square.setFill(Color.TRANSPARENT);
				square.setOnMouseClicked(event -> handleSquareClick(finalRow, finalCol));
				boardPane.getChildren().add(square);
			}
		}

		// 3. Move highlights (above click rects, below pieces — all mouseTransparent)
		if (selectedPosition != null && validMoves != null) {
			renderMoveHighlights(boardPane, validMoves, logic);
		}

		// 4. Pieces
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				Position pos = new Position(row, col);
				BoardPiece piece = logic.getSquare(pos);
				if (piece != null) {
					addPieceToBoard(boardPane, piece, row, col);
				}
			}
		}
	}

	private void renderMoveHighlights(Pane pane, String[][] moves, GameState state) {
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				if (moves[row][col] == null) continue;

				double x = col * SQUARE_SIZE;
				double y = row * SQUARE_SIZE;
				boolean isCapture = state.getSquare(new Position(row, col)) != null;

				if (isCapture) {
					// Red border ring around capturable enemy pieces
					Rectangle ring = new Rectangle(x, y, SQUARE_SIZE, SQUARE_SIZE);
					ring.setFill(Color.TRANSPARENT);
					ring.setStroke(Color.color(0.85, 0.1, 0.1, 0.8));
					ring.setStrokeWidth(5);
					ring.setMouseTransparent(true);
					pane.getChildren().add(ring);
				} else {
					// Dark dot on empty reachable squares
					Circle dot = new Circle(x + SQUARE_SIZE / 2.0, y + SQUARE_SIZE / 2.0, SQUARE_SIZE / 4.5);
					dot.setFill(Color.color(0.1, 0.1, 0.1, 0.35));
					dot.setMouseTransparent(true);
					pane.getChildren().add(dot);
				}
			}
		}
	}

	private void addPieceToBoard(Pane boardPane, BoardPiece piece, int row, int col) {
		double x = col * SQUARE_SIZE;
		double y = row * SQUARE_SIZE;

		// Load image via the piece's own classloader so plugin JARs find their resources
		Image img = new Image(Objects.requireNonNull(piece.iconStream(), "Missing icon: " + piece.icon()));
		ImageView pieceView = new ImageView(img);

		pieceView.setX(x);
		pieceView.setY(y);
		pieceView.setFitWidth(SQUARE_SIZE);
		pieceView.setFitHeight(SQUARE_SIZE);
		pieceView.setSmooth(true);

		if (selectedPosition != null && selectedPosition.row() == row && selectedPosition.col() == col) {
			pieceView.setOpacity(0.7);
		}

		pieceView.setMouseTransparent(true);
		boardPane.getChildren().add(pieceView);
	}

	private void handleSquareClick(int row, int col) {
		Position clickedPos = new Position(row, col);
		BoardPiece clickedPiece = logic.getSquare(clickedPos);

		if (selectedPosition != null) {
			if (validMoves != null && validMoves[row][col] != null) {
				// Legal destination: dispatch through piece logic (captures, passControl, etc.)
				try {
					logic.commandMove(selectedPosition, clickedPos);
				} catch (Exception e) {
					System.out.println("Move failed: " + e.getMessage());
				}
				selectedPosition = null;
				validMoves = null;
			} else if (clickedPiece != null && clickedPiece.color() == logic.turnPlayer()) {
				// Switch selection to another friendly piece
				selectedPosition = clickedPos;
				validMoves = computeValidMoves(clickedPos, clickedPiece);
			} else {
				// Clicked an illegal square — deselect
				selectedPosition = null;
				validMoves = null;
			}
		} else {
			// Only allow selecting a piece that belongs to the current player
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
		boardPane.getChildren().clear();
		renderBoard(boardPane, logic);
	}

	@Override
	public void start(Stage stage) {
		stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));

		Pane mainContainer = new Pane();
		mainContainer.setStyle("-fx-background-color: #505050;");

		boardPane = new Pane();
		int boardSize = SQUARE_SIZE * 8;

		Rectangle bgRect = new Rectangle(MARGIN, MARGIN, boardSize, boardSize);
		bgRect.setFill(Color.web("#808080"));
		mainContainer.getChildren().add(bgRect);

		boardPane.setLayoutX(MARGIN);
		boardPane.setLayoutY(MARGIN);
		mainContainer.getChildren().add(boardPane);

		// TODO: populate side pane with move history
		VBox sidePane = new VBox();
		int sidePaneX = MARGIN + boardSize + MARGIN;
		sidePane.setLayoutX(sidePaneX);
		sidePane.setLayoutY(MARGIN);
		sidePane.setPrefWidth(SIDE_PANE_WIDTH);
		sidePane.setPrefHeight(boardSize);
		sidePane.setStyle("-fx-border-color: #333333; -fx-border-width: 2; -fx-padding: 10;");
		mainContainer.getChildren().add(sidePane);

		logic = new GameState();
		setupBoard(logic);
		renderBoard(boardPane, logic);

		int totalWidth = MARGIN + boardSize + MARGIN + SIDE_PANE_WIDTH + MARGIN;
		Scene scene = new Scene(mainContainer, totalWidth, boardSize + MARGIN * 2);
		stage.setTitle("Fairy Chess 2026");
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();
	}

	private void setupBoard(GameState state) {
		Piece[] backRank = {new Rook(), new Knight(), new Bishop(), new Queen(),
				new King(), new Bishop(), new Knight(), new Rook()};
		Piece[] backRankWhite = {new Rook(), new Knight(), new Bishop(), new Queen(),
				new King(), new Bishop(), new Knight(), new Rook()};

		for (int col = 0; col < 8; col++) {
			boolean isKing = (col == 4);
			state.place(backRank[col],
					new PieceState(isKing, com.chess.logic.types.Color.BLACK, new Position(0, col)));
			state.place(new Pawn(),
					new PieceState(false, com.chess.logic.types.Color.BLACK, new Position(1, col)));
			state.place(new Pawn(),
					new PieceState(false, com.chess.logic.types.Color.WHITE, new Position(6, col)));
			state.place(backRankWhite[col],
					new PieceState(isKing, com.chess.logic.types.Color.WHITE, new Position(7, col)));
		}
	}

	public static void main(String[] args) {
		launch();
	}
}
