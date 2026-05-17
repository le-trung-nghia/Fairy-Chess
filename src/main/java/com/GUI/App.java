package com.GUI;

import com.Logic.BoardPiece;
import com.Logic.GameState;
import com.Logic.Position;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.Objects;

public class App extends Application {

	private static final int SQUARE_SIZE = 70;
	private GameState logic;
	private Pane boardPane;
	private Position selectedPosition = null;
	private static final int MARGIN = 20;
	private static final Color BG_COLOR = Color.web("#808080");  // Gray background
	private static final int SIDE_PANE_WIDTH = 250;
	private VBox sidePane;

	public void renderBoard(Pane boardPane, GameState logic) {
		// Board background - scale to board size
		ImageView background = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/Chess_Board.png"))));
		int boardSize = SQUARE_SIZE * 8;
		background.setFitWidth(boardSize);
		background.setFitHeight(boardSize);
		background.setPreserveRatio(false);
		boardPane.getChildren().add(background);

		// Add invisible squares for click detection
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				int finalRow = row;
				int finalCol = col;
				Rectangle square = new Rectangle(col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
				square.setFill(Color.TRANSPARENT);

//				square.setStroke(Color.web("#0000FF"));
//				square.setStrokeWidth(1);


				square.setOnMouseClicked(event -> handleSquareClick(finalRow, finalCol));
				boardPane.getChildren().add(square);
			}
		}

		// Piece grid
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

	private void addPieceToBoard(Pane boardPane, BoardPiece piece, int row, int col) {
		// Calculate coords
		double x = col * SQUARE_SIZE;
		double y = row * SQUARE_SIZE;

		// Create the Image View
		String path = "/piece_img/" + piece.icon();
		Image img = new Image(getClass().getResourceAsStream(path));
		ImageView pieceView = new ImageView(img);

		// Set position and properties
		pieceView.setX(x);
		pieceView.setY(y);
		pieceView.setFitWidth(SQUARE_SIZE);
		pieceView.setFitHeight(SQUARE_SIZE);
		pieceView.setSmooth(true);

		// Highlight selected piece
		if (selectedPosition != null && selectedPosition.row() == row && selectedPosition.col() == col) {
			pieceView.setOpacity(0.7);
		}

		// Make it transparent so clicks pass to the squares below
		pieceView.setMouseTransparent(true);

		// Add to the Pane
		boardPane.getChildren().add(pieceView);
	}

	private void handleSquareClick(int row, int col) {
		Position clickedPos = new Position(row, col);

		// If a piece is selected, try to move it
		if (selectedPosition != null) {
			movePiece(selectedPosition, clickedPos);
			clearSelection();
		} else if (logic.getSquare(clickedPos) != null) {
			// No piece selected, so select the piece at this square (if there is one)
			selectedPosition = clickedPos;
			redrawBoard();
		}
	}

	private void movePiece(Position fromPos, Position toPos) {
		// Check if destination is occupied
		if (logic.getSquare(toPos) != null) {
			System.out.println("Cannot move piece to occupied square");
			return;
		}

		// Move the piece using GameState's displace method
		BoardPiece piece = logic.getSquare(fromPos);
		if (piece != null) {
			try {
				logic.displace(fromPos, toPos);
				// Re-render to show the new positions
				redrawBoard();
			} catch (IllegalStateException e) {
				System.out.println("Move failed: " + e.getMessage());
			}
		}
	}

	private void clearSelection() {
		selectedPosition = null;
		// Re-render to remove highlighting
		redrawBoard();
	}

	private void redrawBoard() {
		boardPane.getChildren().clear();
		renderBoard(boardPane, logic);
	}

	@Override
	public void start(Stage stage) {
		// App Icon
		stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));

		// Create main container
		Pane mainContainer = new Pane();
		mainContainer.setStyle("-fx-background-color: #505050;");  // Dark gray background

		// Create board pane
		boardPane = new Pane();
		int boardSize = SQUARE_SIZE * 8;

		// Add gray rectangle behind board
		Rectangle bgRect = new Rectangle(MARGIN, MARGIN, boardSize, boardSize);
		bgRect.setFill(Color.web("#808080"));
		mainContainer.getChildren().add(bgRect);

		boardPane.setLayoutX(MARGIN);
		boardPane.setLayoutY(MARGIN);
		mainContainer.getChildren().add(boardPane);

		// Create side pane for the stack
		// TODO SHow the previous moves from the stack
		VBox sidePane = new VBox();
		int sidePaneX = MARGIN + boardSize + MARGIN;
		sidePane.setLayoutX(sidePaneX);
		sidePane.setLayoutY(MARGIN);
		sidePane.setPrefWidth(SIDE_PANE_WIDTH);
		sidePane.setPrefHeight(boardSize);
		sidePane.setStyle("-fx-border-color: #333333; -fx-border-width: 2; -fx-padding: 10;");
//		sidePane.setSpacing(5);  // Space between moves

		// TODO: Each move goes here as a Text/Label in the VBox

		mainContainer.getChildren().add(sidePane);

		// Initialize game
		logic = new GameState();
		renderBoard(boardPane, logic);

		// Calculate total width
		int totalWidth = MARGIN + boardSize + MARGIN + SIDE_PANE_WIDTH + MARGIN;

		Scene scene = new Scene(mainContainer, totalWidth, boardSize + MARGIN * 2);
		stage.setTitle("Fairy Chess 2026");
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();
	}
	public static void main(String[] args) {
		launch();
	}
}