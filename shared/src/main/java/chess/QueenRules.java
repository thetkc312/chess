package chess;

import java.util.HashSet;

public class QueenRules extends MoveRules {

    public QueenRules(ChessGame.TeamColor pieceColor) {
        // Tells the parent abstract class whether the piece can move infinitely (recursively) after it starts moving
        super(true, pieceColor);
    }
    protected HashSet<int[]> potentialMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<int[]> queenMoves = new HashSet<>(8);
        queenMoves.add(new int[]{-1, -1});
        queenMoves.add(new int[]{-1, 0});
        queenMoves.add(new int[]{-1, 1});
        queenMoves.add(new int[]{0, -1});
        queenMoves.add(new int[]{0, 1});
        queenMoves.add(new int[]{1, -1});
        queenMoves.add(new int[]{1, 0});
        queenMoves.add(new int[]{1, 1});
        return queenMoves;
    }
}
