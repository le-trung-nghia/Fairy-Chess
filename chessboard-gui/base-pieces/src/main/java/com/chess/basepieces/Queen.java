package com.chess.basepieces;

import com.chess.logic.boardregion.Ray;
import com.chess.logic.state.BoardPiece;
import com.chess.logic.state.GameState;
import com.chess.logic.types.Color;
import com.chess.logic.types.Direction;
import com.chess.logic.types.Piece;
import com.chess.logic.types.Position;

import one.util.streamex.StreamEx;

public class Queen extends Piece {
    private static final Direction[] Directions = Direction.values();

    @Override
    public String icon(BoardPiece thisState) {
        return thisState.color() == Color.BLACK ? "black_queen.png" : "white_queen.png";
    }

    @Override
    public String identifier() {
        return "queen";
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
                    moves[pos.row()][pos.col()] = ".png";
                } else {
                    if (curr.color() != thisState.color()) {
                        moves[pos.row()][pos.col()] = ".png";
                    }
                    break;
                }
            }
        }

        return moves;
    }
}
