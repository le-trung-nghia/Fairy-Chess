package com.Logic.BasePieces;

import com.Logic.BoardPiece;
import com.Logic.Color;
import com.Logic.Direction;
import com.Logic.GameState;
import com.Logic.Piece;
import com.Logic.Position;
import com.Logic.BoardRegion.Ray;

import one.util.streamex.StreamEx;

public class Rook extends Piece {
    private static final Direction[] Directions = { Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST };

    public Rook(Color color, Position position) {
        super(color, position);
    }

    @Override
    protected String icon() {
        return color == Color.BLACK ? "black_rook.png" : "white_rook.png";
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
                    moves[pos.row()][pos.col()] = "greensquare.png";
                } else {
                    if (curr.color() != color) {
                        moves[pos.row()][pos.col()] = "redsquare.png";
                    }
                    break;
                }
            }
        }

        return moves;
    }
}
