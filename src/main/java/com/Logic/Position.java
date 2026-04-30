package com.Logic;

// A square on the board
public record Position(int row, int col) {
    public Position {
        if (row < 0 || row > 7) {
            throw new IllegalArgumentException(
                    "Position must have row between 0 and 7 inclusive, got %d".formatted(row));
        }
        if (col < 0 || col > 7) {
            throw new IllegalArgumentException(
                    "Position must have column between 0 and 7 inclusive, got %d".formatted(row));
        }
    }

    // add a vector to this position
    public Position add(Vector displacement) {
        return new Position(this.row + displacement.row(), this.col + displacement.col());
    }

    

    // get the vector pointing from this position to another position
    public Vector vectorTowards(Position other) {
        return new Vector(other.row - this.row, other.col - this.col);
    }

    // convert this position to a vector
    public Vector toVector() {
        return new Vector(row, col);
    }

    public int row() {
        return row;
    }

    public int col() {
        return col;
    }
}
