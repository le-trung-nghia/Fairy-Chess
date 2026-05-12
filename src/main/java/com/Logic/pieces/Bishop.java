package com.Logic.pieces;

import com.Logic.BoardPiece;
import com.Logic.Color;
import com.Logic.Direction;
import com.Logic.GameState;
import com.Logic.Piece;
import com.Logic.Position;
import com.Logic.Vector;
 
public class Bishop extends Piece {
    private static final Direction[] DIRECTIONS = {
        Direction.NORTHEAST, Direction.NORTHWEST,
        Direction.SOUTHEAST, Direction.SOUTHWEST
    };
    
    public Bishop(Color color, Position position) {
        super(color, position);
    }

    @Override
    protected String icon() {
        return color == Color.BLACK ? "black_bishop.png" : "white_bishop.png";
    }

    @Override
    protected void onMoveCommand(GameState state, Position to) {
        String[][] movable = getMovableSquares(state);
        if (movable[to.row()][to.col()] != null) {
            if (state.hasEnemy(to, color)) {
                state.capture(to);
            }
            state.move(position, to);
            state.passControl();
        }
    }

    @Override
    protected String[][] getMovableSquares(GameState state) {
        String[][] moves = new String[8][8];
 
        for (Direction d : DIRECTIONS) {
            Vector step = d.unitVector();
            Vector newPos = position.toVector().add(step);
 
            while (newPos.isInBounds()) {
                Position pos = cursor.toPosition();
                BoardPiece curr = state.getSquare(pos);
 
                if (curr == null) {
                    moves[pos.row()][pos.col()] = ".png";
                } else {
                    if (curr.color() != color) {
                        moves[pos.row()][pos.col()] = ".png";
                    }
                    break;
                }
 
                newPos = newPos.add(step);
            }
        }
 
        return moves;
    }
}
