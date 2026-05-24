package com.chess.logic.state;

import java.util.Objects;

import com.chess.logic.types.Color;
import com.chess.logic.types.Position;

public class PieceState {
    private boolean isKing;
    private Color color;
    private Position position;

    public PieceState(boolean isKing, Color color, Position position) {
        this.isKing = isKing;
        this.color = Objects.requireNonNull(color);
        this.position = Objects.requireNonNull(position);
    }

    public boolean isKing() {
        return this.isKing;
    }

    public Color color() {
        return this.color;
    }

    public Position position() {
        return this.position;
    }

    void setKingStatus(boolean isKing) {
        this.isKing = isKing;
    }

    void setColor(Color color) {
        this.color = color;
    }

    void setPosition(Position position) {
        this.position = position;
    }
}
