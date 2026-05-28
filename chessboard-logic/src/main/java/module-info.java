module com.chess.logic {
    exports com.chess.logic.state;
    exports com.chess.logic.types;
    exports com.chess.registry;
    exports com.chess.logic.boardregion;

    requires transitive one.util.streamex;
}
