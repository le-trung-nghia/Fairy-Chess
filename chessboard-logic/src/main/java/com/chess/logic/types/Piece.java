package com.chess.logic.types;

import com.chess.logic.state.BoardPiece;
import com.chess.logic.state.GameState;
import com.chess.registry.PiecePath;

// Implementation of a piece
public abstract class Piece {
    public Piece() {
    }

    // called when this piece is first placed on the board
    public void onInit(BoardPiece thisState) {
    }

    // return whether this piece is a king piece
    public abstract boolean isKing();

    // return the path to the icon for the piece
    public abstract String icon(BoardPiece thisState);

    // return this piece's identifier (unique among all pieces in a piece pack)
    public abstract String identifier();

    // user commands this piece to "move" to a square to the board
    public abstract void onMoveCommand(GameState gameState, BoardPiece thisState, Position to);

    // get the squares this piece can move to
    // returns a 2D array of marker strings overlaid on movable squares
    // array can contain nulls to indicate that the corresponding square cannot be
    // moved to
    public abstract String[][] getMovableSquares(GameState state, BoardPiece thisState);

    // return the pieces this piece can promote to after a move, or null if no
    // promotion
    // each PiecePath specifies the pack and piece name to promote to
    // called by the GUI after every move — return null for pieces that never
    // promote
    public abstract PiecePath[] promotionOptions(GameState state, BoardPiece thisState);

    // called when this piece is displaced by another piece
    // thisState contains this piece's position after the displacement
    public void onDisplace(GameState state, BoardPiece thisState) {
    }

    // called when this piece's color is changed
    // thisState contains this piece's changed color
    // the new color may be the same as the old color
    public void onColorChange(GameState state, BoardPiece thisState) {
    }

    // called when this piece's king status is changed
    // thisState contains this piece's changed king status
    // the new king status may be the same as the old king status
    public void onKingStatusChange(GameState state, BoardPiece thisState) {
    }

    // called when this piece is captured
    public void onCapture(GameState state, BoardPiece thisState) {
    }
}