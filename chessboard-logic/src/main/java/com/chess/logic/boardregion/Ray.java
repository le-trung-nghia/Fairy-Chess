package com.chess.logic.boardregion;

import java.util.Iterator;
import java.util.Objects;

import com.chess.logic.types.Direction;
import com.chess.logic.types.Position;
import com.chess.logic.types.Vector;

import one.util.streamex.StreamEx;

// A ray on the board, starting from a point on the board and extending infinitely in one direction
public record Ray(Position origin, Direction direction) implements BoardRegion {
    public Ray {
        Objects.requireNonNull(origin);
        Objects.requireNonNull(direction);
    }

    @Override
    public Iterator<Position> iterator() {
        return StreamEx
                .iterate(origin.toVector(), current -> current.isInBounds(),
                        current -> current.add(direction.unitVector()))
                .map(Vector::toPosition)
                .iterator();
    }

    @Override
    public boolean includes(Position pos) {
        return origin.vectorTowards(pos).direction() == direction;
    }
}
