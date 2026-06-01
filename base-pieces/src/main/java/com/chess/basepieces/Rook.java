package com.chess.basepieces;

import com.chess.logic.boardregion.Ray;
import com.chess.logic.state.BoardPiece;
import com.chess.logic.state.GameState;
import com.chess.logic.types.Color;
import com.chess.logic.types.Direction;
import com.chess.logic.types.Piece;
import com.chess.logic.types.Position;

import one.util.streamex.StreamEx;

public class Rook extends Piece {
    private static final Direction[] Directions = { Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST };

    @Override
    public String icon(BoardPiece thisState) {
        return thisState.color() == Color.BLACK ? "black_rook.png" : "white_rook.png";
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
        return "rook";
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

        for (Direction d : Directions) {
            for (Position pos : (Iterable<Position>) () -> StreamEx.of(new Ray(thisState.position(), d).iterator())
                    .skip(1)
                    .iterator()) {
                BoardPiece curr = state.getSquare(pos);

                if (curr == null) {
                    moves[pos.row()][pos.col()] = "move.png";
                } else {
                    if (curr.color() != thisState.color()) {
                        moves[pos.row()][pos.col()] = "attack.png";
                    }
                    break;
                }
            }
        }

        return moves;
    }
}
