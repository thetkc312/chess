package chess;

import java.util.HashSet;

/**
 * The abstract class MoveRules contains the mechanisms that search the board to determine, among
 * all the ways a piece can hypothetically move, which locations it is actually capable of moving to.
 * Its extended classes define all the ways a piece could hypothetically move.
 */
public abstract class MoveRules {

    private final boolean unlimitedDistance; // True when a piece can move infinitely many times in a direction

    private final HashSet<ChessMove> validMoves = new HashSet<>();

    protected abstract HashSet<int[]> potentialMoves(ChessBoard board, ChessPosition myPosition); // A hash set of int arrays of format [row, col] where each [row, col] pair indicates a potential move direction of a piece

    public MoveRules(boolean unlimitedDistance) {
        this.unlimitedDistance = unlimitedDistance;
    }

    public HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition myPosition){
        // TODO: Implement potentialMove as a class of its own
        for (int[] potentialMove : potentialMoves(board, myPosition)) {
            ChessPosition targetPosition = myPosition.getMovedPosition(potentialMove[0], potentialMove[1]);
            if (isValidMove(board, targetPosition)) {
                // TODO: Add mechanism for promotion piece checking
                validMoves.add(new ChessMove(myPosition, targetPosition, null));
                if (unlimitedDistance) {
                    recurseMoves(board, targetPosition, potentialMove);
                }
            }

        }
        return validMoves;
    };

    /**
     *
     * @param board
     * @param myPosition
     * @param lastMove
     */
    // TODO: Integrate implementation of potentialMove class for lastMove
    private void recurseMoves(ChessBoard board, ChessPosition myPosition, int[] lastMove) {
        ChessPosition targetPosition = myPosition.getMovedPosition(lastMove[0], lastMove[1]);
        if (isValidMove(board, targetPosition)) {
            // TODO: Add mechanism for promotion piece checking
            validMoves.add(new ChessMove(myPosition, targetPosition, null));
            recurseMoves(board, targetPosition, lastMove);
        }
    }

    private boolean isValidMove(ChessBoard board, ChessPosition targetPosition) {
        throw new RuntimeException("Not implemented");
    }
}
