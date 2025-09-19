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
        if (myColor == ChessGame.TeamColor.WHITE) {
            direction = 1;
        } else {
            direction = -1;
        }
        // If the space in front of the pawn is empty, it is a potential move location
        if (board.getPiece(myPosition.getMovedPosition(direction, 0)) == null) {
            pawnMoves.add(new int[]{direction, 0});
            // If the space two in front of the pawn is also empty, it is a potential move location
            if (board.getPiece(myPosition.getMovedPosition(2 * direction, 0)) == null) {
                pawnMoves.add(new int[]{2 * direction, 0});
            }
        }
        // TODO: Add potential moves for capturing pieces, and for en passe capture
        return pawnMoves;
    }
}
