package com.chess.logic.types;

public enum Color {
    BLACK,
    WHITE;

    // get the opposite color
    public Color oppositeColor() {
        return (this == BLACK) ? WHITE : BLACK;
    }

    // get the forward direction (i.e. for a pawn) for the color
    public Direction forwardDirection() {
        return (this == BLACK) ? Direction.SOUTH : Direction.NORTH;
    }
}
