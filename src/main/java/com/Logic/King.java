package com.Logic;

public class King extends Piece {
    public King(Color color, Position position) {
        super(color, position);
    }

    @Override
    protected String icon() {
        // returns the patb for the resource file containing the icon of the King piece
        return "king.png";
    }

    @Override
    protected void onMoveCommand(GameState state, Position to) {
        // the destination has a piece
        if (state.getSquare(to) != null) {
            state.capture(to);
        }
        state.move(this.position, to);
        // pass control of the next turn to the other player
        state.passControl();
    }

    @Override
    protected String[][] getMovableSquares(GameState state) {
        // each element of the 2D array contains the image file that is displayed on each movable square
        String[][] movableSquares = new String[8][8];
        int row = position.row();
        int col = position.col();
        // the King can move to and attack all surrounding squares
        for (int i = Math.max(row - 1, 0); i < Math.min(row + 1, 8); i++) {
            for (int j = Math.max(col - 1, 0); j < Math.min(col + 1, 8); j++) {
                if (i != row && j != col) {
                    if (state.hasEnemy(position, color)) {
                        movableSquares[i][j] = "attack.png";
                    } else {
                        movableSquares[i][j] = "move.png";
                    }
                }
            }
        }
        return movableSquares;
    }

    // this piece does not listen to any events, so event methods do not need to be overridden
}