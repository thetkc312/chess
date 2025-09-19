package chess;

import java.util.HashSet;

public class QueenRules extends MoveRules {

    public QueenRules() {
        // Tells the parent abstract class whether the piece can move infinitely (recursively) after it starts moving
        super(true);
    }
    protected HashSet<int[]> potentialMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("Not implemented");
    }
}
