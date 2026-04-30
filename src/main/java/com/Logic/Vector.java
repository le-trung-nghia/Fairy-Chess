package com.Logic;

public record Vector(int row, int col) {
    final static Vector TOWARDS_BLACK = new Vector(1, 0);
    final static Vector TOWARDS_WHITE = new Vector(-1, 0);

    public Vector {
        if (row < 0 || row > 7) {
            throw new IllegalArgumentException(
                    "Board vector must have row between 0 and 7 inclusive, got %d".formatted(row));
        }
        if (col < 0 || col > 7) {
            throw new IllegalArgumentException(
                    "Board vector must have column between 0 and 7 inclusive, got %d".formatted(row));
        }
    }

    // add another vector to this vector
    public Vector add(Vector other) {
        return new Vector(this.row + other.row, this.col + other.col);
    }

    // subtract another vector from this vector
    public Vector sub(Vector other) {
        return new Vector(this.row - other.row, this.col - other.col);
    }

    // multiply this vector by a scalar
    public Vector mul(int multiplier) {
        return new Vector(row * multiplier, col * multiplier);
    }

    // returns true if this is a zero vector
    public boolean isZero() {
        return row == 0 && col == 0;
    }

    // convert this vector into a board position
    public Position toPosition() {
        return new Position(row, col);
    }
}
