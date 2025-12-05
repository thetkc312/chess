package chess;

import java.util.HashSet;

public class PawnRules extends MoveRules {

    public PawnRules(ChessGame.TeamColor pieceColor) {
        // Tells the parent abstract class whether the piece can move infinitely (recursively) after it starts moving
        super(false, pieceColor);
    }

    /**
     * Analyzes the board state to evaluate the set of hypothetical first-degree moves that could be made,
     * taking into account board states that make new moves possible (i.e. own color, a pawn that
     * can capture, a king in position to perform a castle), but ignoring any obstacles to moves
     * (i.e. would put the piece off the board, would collide with friendly pieces).
     * The Pawn extension of MoveRules is special because of how strongly dependent its moves
     * are on the board state. For example, it can only move forward if there aren't any pieces
     * in front of it at all, so these moves that are possible for other pieces must be manually
     * excluded here, where the set of hypothetically possible moves are generated.
     *
     * @param board      in its current state
     * @param myPosition where this piece is located
     * @return a HashSet of 2-deep integer arrays encoding potential directions this piece could move
     */
    protected HashSet<int[]> potentialMovements(ChessBoard board, ChessPosition myPosition) {
        HashSet<int[]> pawnMoves = new HashSet<>(8);
        int direction;
        int startingRow;

        // If the pawn is white, it moves in the positive direction through rows
        if (myColor == ChessGame.TeamColor.WHITE) {
            direction = 1;
            startingRow = 2;
        }
        // If the pawn is black, it moves in the negative direction through rows
        else {
            direction = -1;
            startingRow = 7;
        }

        // Checking if the pawn can move forward at all
        // If the space in front of the pawn is on the board and empty, it is a potential move location
        ChessPosition candidatePosition = myPosition.getMovedPosition(direction, 0);
        if (candidatePosition.isOnBoard() && board.getPiece(candidatePosition) == null) {
            pawnMoves.add(new int[]{direction, 0});
            // If the pawn is still in its starting row, it might be able to move twice
            if (myPosition.getRow() == startingRow) {
                // If the space two in front of the pawn is also on the board and empty, it is a potential move location
                candidatePosition = myPosition.getMovedPosition(direction + direction, 0);
                if (candidatePosition.isOnBoard() && board.getPiece(candidatePosition) == null) {
                    pawnMoves.add(new int[]{direction + direction, 0});
                }
            }
        }

        // If there is an opponent's piece to a diagonal of the pawn, it can move into its space
        for (int leftRight : new int[]{-1, 1}) {
            candidatePosition = myPosition.getMovedPosition(direction, leftRight);
            if (candidatePosition.isOnBoard()) {
                ChessPiece diagonalPiece = board.getPiece(candidatePosition);
                if (diagonalPiece != null && diagonalPiece.getTeamColor() != myColor) {
                    pawnMoves.add(new int[]{direction, leftRight});
                }
            }
        }

        return pawnMoves;
    }
}
