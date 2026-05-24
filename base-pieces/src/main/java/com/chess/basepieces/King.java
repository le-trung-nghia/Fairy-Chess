package com.chess.basepieces;

import com.chess.logic.boardregion.BoardRegion;
import com.chess.logic.boardregion.Rectangle;
import com.chess.logic.state.BoardPiece;
import com.chess.logic.state.GameState;
import com.chess.logic.types.Color;
import com.chess.logic.types.Piece;
import com.chess.logic.types.Position;
import com.chess.logic.types.Vector;

public class King extends Piece {
    @Override
    public String icon(BoardPiece thisState) {
        // returns the patb for the resource file containing the icon of the King piece
        return thisState.color() == Color.BLACK ? "black_king.png" : "white_king.png"; // returns the color as well
    }

    @Override
    public String identifier() {
        return "king";
    }

    @Override
    public void onMoveCommand(GameState state, BoardPiece thisState, Position to) {
        // the destination has a piece
        if (state.getSquare(to) != null) {
            state.capture(to);
        }
        state.move(thisState.position(), to);
        // pass control of the next turn to the other player
        state.passControl();
    }

    @Override
    public String[][] getMovableSquares(GameState state, BoardPiece thisState) {
        // each element of the 2D array contains the image file that is displayed on
        // each movable square
        String[][] movableSquares = new String[8][8];

        // the King can move to and attack all surrounding squares
        BoardRegion movableRegion = new Rectangle(thisState.position().saturatingSub(new Vector(1, 1)),
                thisState.position().saturatingAdd(new Vector(1, 1))).difference(thisState.position());
        for (Position square : movableRegion) {
            if (state.hasEnemy(thisState.position(), thisState.color())) {
                movableSquares[square.row()][square.col()] = "attack.png";
            } else {
                movableSquares[square.row()][square.col()] = "move.png";
            }
        }
        return movableSquares;
    }

    // this piece does not listen to any events, so event methods do not need to be
    // overridden
}