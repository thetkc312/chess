package chess;

import java.util.HashSet;

public class BishopRules extends MoveRules {

    public BishopRules(ChessGame.TeamColor pieceColor) {
        // Tells the parent abstract class whether the piece can move infinitely (recursively) after it starts moving
        super(true, pieceColor);
    }

    protected HashSet<int[]> potentialMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<int[]> BishopMoves = new HashSet<>(8);
        BishopMoves.add(new int[]{-1, -1});
        BishopMoves.add(new int[]{-1, 1});
        BishopMoves.add(new int[]{1, -1});
        BishopMoves.add(new int[]{1, 1});
        return BishopMoves;
    }
}
