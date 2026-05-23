package com.chess.logic.state;

import java.util.ArrayList;
import java.util.Objects;

import com.chess.logic.types.Color;
import com.chess.logic.types.Piece;
import com.chess.logic.types.Position;
import com.chess.logic.types.Vector;

// The entire state of the game at one point in the match
public class GameState {
    // row 0-7, column 0-7
    private BoardPiece[][] board = new BoardPiece[8][8];
    private int turnNumber = 1;
    private Color turnPlayer = Color.WHITE;
    // captured pieces
    private ArrayList<BoardPiece> captured = new ArrayList<>();

    // get the piece (or the lack of one) at a square on the board
    public BoardPiece getSquare(Position pos) {
        return board[pos.row()][pos.col()];
    }

    private void setSquare(Position pos, BoardPiece piece) {
        Objects.requireNonNull(pos);
        board[pos.row()][pos.col()] = piece;
    }

    private void swapSquares(Position src, Position dst) {
        Objects.requireNonNull(src);
        Objects.requireNonNull(dst);
        BoardPiece dstPiece = getSquare(dst);
        setSquare(dst, getSquare(src));
        setSquare(src, dstPiece);
    }

    public void commandMove(Position src, Position dst) {
        getSquare(src).commandMove(this, dst);
        turnNumber++;
    }

    public void place(Piece piece, Position pos, boolean isKing, Color color) {
        if (getSquare(pos) != null) {
            throw new IllegalStateException(
                    "Cannot place a new piece at (%d, %d) because there is already a piece there."
                            .formatted(pos.row(), pos.col()));
        }
        setSquare(pos, null);
    }

    // get the turn number
    public int turnNumber() {
        return turnNumber;
    }

    // displace a piece on the board to another location
    public void displace(Position src, Position dst) {
        Objects.requireNonNull(src);
        Objects.requireNonNull(dst);
        if (getSquare(src) == null) {
            throw new IllegalStateException(
                    "Cannot displace piece at (%d, %d) because there is no piece there."
                            .formatted(src.row(), src.col()));
        }
        if (getSquare(dst) != null) {
            throw new IllegalStateException(
                    "Piece at (%d, %d) cannot be displaced to (%d, %d) because it already contains another piece."
                            .formatted(src.row(), src.col(), dst.row(), dst.col()));
        }
        swapSquares(src, dst);
        getSquare(dst).displace(this, dst);
    }

    public void displace(Position src, Vector displacement) {
        Objects.requireNonNull(src);
        Objects.requireNonNull(displacement);
        displace(src, src.add(displacement));
    }

    // move a piece on the board to another location
    public void move(Position src, Position dst) {
        Objects.requireNonNull(src);
        Objects.requireNonNull(dst);
        if (getSquare(src) == null) {
            throw new IllegalStateException(
                    "Cannot move piece at (%d, %d) because there is no piece there."
                            .formatted(src.row(), src.col()));
        }
        if (getSquare(dst) != null) {
            throw new IllegalStateException(
                    "Piece at (%d, %d) cannot be moved to (%d, %d) because it already contains another piece."
                            .formatted(src.row(), src.col(), dst.row(), dst.col()));
        }
        swapSquares(src, dst);
        getSquare(dst).move(dst);
    }

    public void move(Position src, Vector displacement) {
        Objects.requireNonNull(src);
        Objects.requireNonNull(displacement);
        move(src, src.add(displacement));
    }

    public void capture(Position pos) {
        Objects.requireNonNull(pos);
        BoardPiece piece = getSquare(pos);
        captured.add(piece);
        setSquare(pos, null);
        piece.capture(this);
    }

    public boolean hasEnemy(Position pos, Color color) {
        Objects.requireNonNull(pos);
        Objects.requireNonNull(color);
        BoardPiece piece = getSquare(pos);
        return piece != null && piece.color() != color;
    }

    public void passControl() {
        turnPlayer = turnPlayer.oppositeColor();
    }
}