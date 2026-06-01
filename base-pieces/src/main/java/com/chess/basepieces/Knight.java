package com.chess.basepieces;

import com.chess.logic.state.BoardPiece;
import com.chess.logic.state.GameState;
import com.chess.logic.types.Color;
import com.chess.logic.types.Piece;
import com.chess.logic.types.Position;
import com.chess.logic.types.Vector;

public class Knight extends Piece {
    private static final Vector[] jumps = {
            new Vector(-2, -1), new Vector(-2, 1),
            new Vector(-1, -2), new Vector(-1, 2),
            new Vector(1, -2), new Vector(1, 2),
            new Vector(2, -1), new Vector(2, 1)
    };

    @Override
    public String icon(BoardPiece thisState) {
        return thisState.color() == Color.BLACK ? "black_knight.png" : "white_knight.png";
    }

    @Override
    public com.chess.registry.PiecePath[] promotionOptions(GameState state, BoardPiece thisState) {
        return null;
    }

    @Override
    public boolean isKing() {
        return false;
    }

    @Override
    public String identifier() {
        return "knight";
    }

    @Override
    public void onMoveCommand(GameState state, BoardPiece thisState, Position to) {
        if (state.hasEnemy(to, thisState.color())) {
            state.capture(to);
        }
        state.move(thisState.position(), to);
        state.passControl();
    }

    @Override
    public String[][] getMovableSquares(GameState state, BoardPiece thisState) {
        String[][] moves = new String[8][8];

        for (Vector j : jumps) {
            Vector newPos = thisState.position().toVector().add(j);

            if (newPos.isInBounds()) {
                Position pos = newPos.toPosition();
                BoardPiece curr = state.getSquare(pos);
                if (curr == null) {
                    moves[pos.row()][pos.col()] = "move.png";
                } else if (curr.color() != thisState.color()) {
                    moves[pos.row()][pos.col()] = "attack.png";
                }
            }
        }

        return moves;
    }
}
