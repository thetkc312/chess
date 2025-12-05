package chess;

import java.util.HashSet;

public class RookRules extends MoveRules {

    public RookRules(ChessGame.TeamColor pieceColor) {
        // Tells the parent abstract class whether the piece can move infinitely (recursively) after it starts moving
        super(true, pieceColor);
    }

    protected HashSet<int[]> potentialMovements(ChessBoard board, ChessPosition myPosition) {
        HashSet<int[]> rookMoves = new HashSet<>(8);
        rookMoves.add(new int[]{-1, 0});
        rookMoves.add(new int[]{0, -1});
        rookMoves.add(new int[]{0, 1});
        rookMoves.add(new int[]{1, 0});
        return rookMoves;
    }
}
