package chess;

import java.util.HashSet;

public class KnightRules extends MoveRules {

    public KnightRules(ChessGame.TeamColor pieceColor) {
        // Tells the parent abstract class whether the piece can move infinitely (recursively) after it starts moving
        super(false, pieceColor);
    }

    // None of the queen's potential moves are influenced by the board state
    protected HashSet<int[]> potentialMovements(ChessBoard board, ChessPosition myPosition) {
        HashSet<int[]> knightMoves = new HashSet<>(8);
        knightMoves.add(new int[]{-2, -1});
        knightMoves.add(new int[]{-2, 1});
        knightMoves.add(new int[]{-1, -2});
        knightMoves.add(new int[]{-1, 2});
        knightMoves.add(new int[]{1, -2});
        knightMoves.add(new int[]{1, 2});
        knightMoves.add(new int[]{2, -1});
        knightMoves.add(new int[]{2, 1});
        return knightMoves;
    }
}
