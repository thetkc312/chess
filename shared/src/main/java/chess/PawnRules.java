package chess;

import java.util.HashSet;

public class PawnRules extends MoveRules {

    public PawnRules(ChessGame.TeamColor pieceColor) {
        // Tells the parent abstract class whether the piece can move infinitely (recursively) after it starts moving
        super(true, pieceColor);
    }

    protected HashSet<int[]> potentialMoves(ChessBoard board, ChessPosition myPosition) {
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
        // If the space in front of the pawn is empty, it is a potential move location
        if (board.getPiece(myPosition.getMovedPosition(direction, 0)) == null) {
            pawnMoves.add(new int[]{direction, 0});
            // If the pawn is still in its starting row, it might be able to move twice
            if (myPosition.getRow() == startingRow) {
                // If the space two in front of the pawn is also empty, it is a potential move location
                if (board.getPiece(myPosition.getMovedPosition(2 * direction, 0)) == null) {
                    pawnMoves.add(new int[]{2 * direction, 0});
                }
            }

        }
        // If there is a piece to a diagonal of the pawn, it can move into its space
        // TODO: Add potential moves for capturing pieces, and for en passe capture
        return pawnMoves;
    }
}
