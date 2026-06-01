package com.chess.logic.state;

import java.util.Objects;

import com.chess.logic.types.Color;
import com.chess.logic.types.Position;

public class PieceState {
    private Color color;
    private Position position;

    public PieceState(Color color, Position position) {
        this.color = Objects.requireNonNull(color);
        this.position = Objects.requireNonNull(position);
    }

    public Color color() {
        return this.color;
    }

    public Position position() {
        return this.position;
    }

    void setColor(Color color) {
        this.color = color;
    }

    void setPosition(Position position) {
        this.position = position;
    }
}
