package chess;

import java.util.HashSet;

public class BishopRules extends MoveRules {

    public BishopRules(ChessGame.TeamColor pieceColor) {
        // Tells the parent abstract class whether the piece can move infinitely (recursively) after it starts moving
        super(true, pieceColor);
    }

    protected HashSet<int[]> potentialMovements(ChessBoard board, ChessPosition myPosition) {
        HashSet<int[]> bishopMoves = new HashSet<>(8);
        bishopMoves.add(new int[]{-1, -1});
        bishopMoves.add(new int[]{-1, 1});
        bishopMoves.add(new int[]{1, -1});
        bishopMoves.add(new int[]{1, 1});
        return bishopMoves;
    }
}
