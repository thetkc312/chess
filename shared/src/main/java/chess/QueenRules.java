package chess;

import java.util.HashSet;

public class QueenRules extends MoveRules {

    public QueenRules() {
        // Tells the parent abstract class whether the piece can move infinitely (recursively) after it starts moving
        super(true);
    }

    /**
     * Analyzes the board state to evaluate the set of hypothetical first-degree moves that could be made,
     * taking into account board states that make new moves possible (i.e. own color, a pawn that
     * can capture, a king in position to perform a castle), but ignoring any obstacles to moves
     * (i.e. would put the piece off the board, would collide with friendly pieces)
     *
     * @param board in its current state
     * @param myPosition where this piece is located
     * // TODO: Implement potentialMove as a class of its own
     * @return a HashSet of 2-deep integer arrays encoding potential directions this piece could move
     */
    protected HashSet<int[]> potentialMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("Not implemented");
    }
}
