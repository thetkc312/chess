package chess;

import java.util.HashSet;

public class KingRules extends MoveRules {

    public KingRules(ChessGame.TeamColor pieceColor) {
        // Tells the parent abstract class whether the piece can move infinitely (recursively) after it starts moving
        super(false, pieceColor);
    }

    protected HashSet<int[]> potentialMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<int[]> kingMoves = new HashSet<>(8);
        kingMoves.add(new int[]{-1, -1});
        kingMoves.add(new int[]{-1, 0});
        kingMoves.add(new int[]{-1, 1});
        kingMoves.add(new int[]{0, -1});
        kingMoves.add(new int[]{0, 1});
        kingMoves.add(new int[]{1, -1});
        kingMoves.add(new int[]{1, 0});
        kingMoves.add(new int[]{1, 1});
        return kingMoves;
    }
}
