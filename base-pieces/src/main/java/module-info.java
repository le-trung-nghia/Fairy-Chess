module com.chess.basepieces {
    exports com.chess.basepieces;

    requires transitive com.chess.logic;

    provides com.chess.logic.types.Piece
            with com.chess.basepieces.Bishop, com.chess.basepieces.King, com.chess.basepieces.Knight,
            com.chess.basepieces.Pawn, com.chess.basepieces.Queen, com.chess.basepieces.Rook;
}
