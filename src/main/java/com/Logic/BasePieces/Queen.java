package com.Logic.BasePieces;

import com.Logic.BoardPiece;
import com.Logic.Color;
import com.Logic.Direction;
import com.Logic.GameState;
import com.Logic.Piece;
import com.Logic.Position;
import com.Logic.BoardRegion.Ray;

import one.util.streamex.StreamEx;

public class Queen extends Piece {
    private static final Direction[] Directions = Direction.values();

    public Queen(Color color, Position position) {
        super(color, position);
    }

    @Override
    protected String icon() {
        return color == Color.BLACK ? "black_queen.png" : "white_queen.png";
    }

    @Override
    protected void onMoveCommand(GameState state, Position to) {
        if (state.hasEnemy(to, color)) {
            state.capture(to);
        }
        state.move(position, to);
        state.passControl();
    }

    @Override
    protected String[][] getMovableSquares(GameState state) {
        String[][] moves = new String[8][8];

        for (Direction d : Directions) {
            for (Position pos : (Iterable<Position>) () -> StreamEx.of(new Ray(position, d).iterator()).skip(1)
                    .iterator()) {
                BoardPiece curr = state.getSquare(pos);

                if (curr == null) {
                    moves[pos.row()][pos.col()] = ".png";
                } else {
                    if (curr.color() != color) {
                        moves[pos.row()][pos.col()] = ".png";
                    }
                    break;
                }
            }
        }

        return moves;
    }
}
