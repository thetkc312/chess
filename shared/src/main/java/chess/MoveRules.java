package chess;

import java.util.HashSet;

/**
 * The abstract class MoveRules contains the mechanisms that search the board to determine, among
 * all the ways a piece can hypothetically move, which locations it is actually capable of moving to.
 * Its extended classes define all the ways a piece could hypothetically move.
 */
public abstract class MoveRules {

    private final boolean unlimitedDistance; // True when a piece can move infinitely many times in a direction

    public MoveRules(boolean unlimitedDistance) {
        this.unlimitedDistance = unlimitedDistance;
    }

    private final HashSet<ChessMove> validMoveSet = new HashSet<>();

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
    protected abstract HashSet<int[]> potentialMoves(ChessBoard board, ChessPosition myPosition); // A hash set of int arrays of format [row, col] where each [row, col] pair indicates a potential move direction of a piece

    public HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition myPosition){
        // TODO: Implement potentialMove as a class of its own
        ChessGame.TeamColor myColor = board.getPiece(myPosition).getTeamColor();
        for (int[] potentialMove : potentialMoves(board, myPosition)) {
            ChessPosition targetPosition = myPosition.getMovedPosition(potentialMove[0], potentialMove[1]);
            // Check if the location the piece wants to move to can be moved into
            if (isValidMove(board, targetPosition, myColor)) {
                // TODO: Add mechanism for promotion piece checking
                validMoveSet.add(new ChessMove(myPosition, targetPosition, null));
                // If the piece can continue moving in a direction, try to recurse
                if (unlimitedDistance) {
                    // If the targetPosition is an empty position, recurse. Otherwise, it must be taking an enemy piece and forced to stop, and should not recurse.
                    if (board.getPiece(targetPosition) == null) {
                        recurseMoves(board, targetPosition, potentialMove, myColor);
                    }
                }
            }

        }
        return validMoveSet;
    };

    /**
     * This recursive function is called to allow a piece with unlimitedDistance
     * to continue moving in a direction after it has started travelling
     *
     * @param board in its current state
     * @param latestPosition of the piece of interest
     * @param lastMove made by the piece of interest
     * @param myColor for the piece of interest
     */
    // TODO: Integrate implementation of potentialMove class for lastMove
    private void recurseMoves(ChessBoard board, ChessPosition latestPosition, int[] lastMove, ChessGame.TeamColor myColor) {
        ChessPosition targetPosition = latestPosition.getMovedPosition(lastMove[0], lastMove[1]);
        if (isValidMove(board, targetPosition, myColor)) {
            // TODO: Add mechanism for promotion piece checking
            validMoveSet.add(new ChessMove(latestPosition, targetPosition, null));
            // If the targetPosition was an empty position, recurse. Otherwise, it must be taking an enemy piece and should stop recursion.
            if (board.getPiece(targetPosition) == null) {
                recurseMoves(board, targetPosition, lastMove, myColor);
            }
        }
    }

    /**
     * For a piece in myPosition trying to move to targetPosition, determine whether such a move would be valid
     *
     * @param board in its current state
     * @param myColor for the piece that is moving
     * @param targetPosition where this piece wants to move
     * @return a boolean indicating whether this is a valid move option
     */
    private boolean isValidMove(ChessBoard board, ChessPosition targetPosition, ChessGame.TeamColor myColor) {
        // If the piece wants to move off the board, return false
        if (!targetPosition.isOnBoard()) {
            return false;
        }
        // If the piece wants to move into an empty space, return true
        if (board.getPiece(targetPosition) == null) {
            return true;
        }
        ChessGame.TeamColor targetColor = board.getPiece(targetPosition).getTeamColor();
        // If the piece wants to move into the space of a piece of a different color, return true
        return myColor != targetColor;
    }
}
