package com.chess.registry;

public record PiecePath(String packName, String pieceName) {
    public static PiecePath fromString(String path) {
        String[] components = path.split(":", 2);
        return new PiecePath(components[0], components[1]);
    }

    public String toString() {
        return "%s:%s".formatted(packName, pieceName);
    }
}
