package chess;

import java.util.HashSet;

public abstract class MoveRules {

    protected boolean unlimitedDistance; // True when a piece can move infinitely many times in a direction

    protected HashSet<int[][]> initialMoves; // A double array of format [row, col] where teach [row, col] pair indicates the potential move directions of a piece

    public HashSet<ChessMove> recurseMoves(ChessBoard board, ChessPosition myPosition){
        // TODO: Implementation of recursing searching initialMoves for valid directions, adding the resulting positions to validMoves, then recursing further if unlimitedDistance is true
        HashSet<ChessMove> validMoves = new HashSet<>();
        return validMoves;
    };
}
