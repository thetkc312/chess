package chess;

import java.util.HashSet;

/**
 * The abstract class MoveRules contains the mechanisms that search the board to determine, among
 * all the ways a piece can hypothetically move, which locations it is actually capable of moving to.
 * Its extended classes define all the ways a piece could hypothetically move.
 */
public abstract class MoveRules {

    private final boolean unlimitedDistance; // True when a piece can move infinitely many times in a direction

    protected final ChessGame.TeamColor myColor;

    public MoveRules(boolean unlimitedDistance, ChessGame.TeamColor pieceColor) {
        this.unlimitedDistance = unlimitedDistance;
        this.myColor = pieceColor;
    }

    private final HashSet<ChessMove> validMoveSet = new HashSet<>();

    /**
     * Analyzes the board state to evaluate the set of hypothetical first-degree moves that could be made,
     * taking into account board states that make new moves possible (i.e. own color, a pawn that
     * can capture, a king in position to perform a castle), but ignoring any obstacles to moves
     * (i.e. would put the piece off the board, would collide with friendly pieces)
     *
     * @param board      in its current state
     * @param myPosition where this piece is located
     *                   // TODO: Implement potentialMove as a class of its own
     * @return a HashSet of 2-deep integer arrays encoding potential directions this piece could move
     */
    protected abstract HashSet<int[]> potentialMoves(ChessBoard board, ChessPosition myPosition); // A hash set of int arrays of format [row, col] where each [row, col] pair indicates a potential move direction of a piece

    public HashSet<ChessMove> getMoves(ChessBoard board, ChessPosition myPosition) {
        // TODO: Implement potentialMove as a class of its own
        for (int[] potentialMove : potentialMoves(board, myPosition)) {
            ChessPosition targetPosition = myPosition.getMovedPosition(potentialMove[0], potentialMove[1]);
            if (!targetPosition.isOnBoard()) {
                continue;
            }
            // Check if the location the piece wants to move to can be moved into
            while (isValidMove(board, targetPosition)) {
                // If the piece is a pawn and moving into the uppermost or lowermost row, it must be promoted
                if (board.getPiece(myPosition).getPieceType() == ChessPiece.PieceType.PAWN && (targetPosition.getRow() == 1 || targetPosition.getRow() == 8)) {
                    for (ChessPiece.PieceType promotionCandidate : new ChessPiece.PieceType[]{ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.ROOK}) {
                        validMoveSet.add(new ChessMove(myPosition, targetPosition, promotionCandidate));
                    }
                } else {
                    validMoveSet.add(new ChessMove(myPosition, targetPosition, null));
                }
                // If the piece can continue moving in a direction, try to recurse
                if (!unlimitedDistance) {
                    break;
                }
                // If the targetPosition is an empty position, continue adding moves. Otherwise, it must be taking an enemy piece and forced to stop, and should not recurse.
                if (board.getPiece(targetPosition) == null) {
                    targetPosition = targetPosition.getMovedPosition(potentialMove[0], potentialMove[1]);
                } else {
                    break;
                }
            }

        }
        return validMoveSet;
    }

    /**
     * For a piece in myPosition trying to move to targetPosition, determine whether such a move would be valid
     *
     * @param board          in its current state
     * @param targetPosition where this piece wants to move
     * @return a boolean indicating whether this is a valid move option
     */
    private boolean isValidMove(ChessBoard board, ChessPosition targetPosition) {
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
