package chess;

import org.junit.jupiter.api.extension.ParameterResolutionException;

import java.awt.*;
import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece[][] boardSquares = new ChessPiece[8][8];

    public ChessBoard() {    }

    public static ChessBoard fromString(String inputBoardString) {
        int stringPos = 0;
        ChessBoard stringBoard = new ChessBoard();
        for (int rowPos = 8; rowPos >= 1; rowPos--, stringPos++) {
            for (int colPos = 1; colPos <= 8; colPos++, stringPos++) {
                ChessPosition boardPos = new ChessPosition(rowPos, colPos);
                ChessPiece pieceFromChar = ChessPiece.fromLetter(inputBoardString.charAt(stringPos));
                if (pieceFromChar != null) {
                    stringBoard.addPiece(boardPos, pieceFromChar);
                }
            }
        }
        return stringBoard;
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to (1-indexed)
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        boardSquares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position to check for a piece
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return boardSquares[position.getRow() - 1][position.getColumn() - 1];
    }

    public ChessPosition findPiece(ChessPiece pieceOfInterest) {
        String boardString = this.visualizeBoard();
        char pieceChar = pieceOfInterest.toChar();
        int locatedCharPos = boardString.indexOf(pieceChar);
        // If the corresponding piece could not be found, -1 is returned
        if (locatedCharPos < 0) {
            return null;
        } else {
            int rowPos = 8 - locatedCharPos / 9;
            int colPos = locatedCharPos % 9 + 1;
            return new ChessPosition(rowPos, colPos);
        }
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        String defaultBoard =
                "rnbqkbnr\n" +
                "pppppppp\n" +
                "--------\n" +
                "--------\n" +
                "--------\n" +
                "--------\n" +
                "PPPPPPPP\n" +
                "RNBQKBNR";
        int stringPos = 0;
        for (int rowPos = 8; rowPos >= 1; rowPos--, stringPos++) {
            for (int colPos = 1; colPos <= 8; colPos++, stringPos++) {
                boardSquares[rowPos-1][colPos-1] = null;
                ChessPiece pieceFromChar = ChessPiece.fromLetter(defaultBoard.charAt(stringPos));
                if (pieceFromChar != null) {
                    addPiece(new ChessPosition(rowPos, colPos), pieceFromChar);
                }
            }
        }
    }

    public String visualizeBoard() {
        StringBuilder boardString = new StringBuilder(72);
        for (int rowPos = 8; rowPos >= 1; rowPos--) {
            for (int colPos = 1; colPos <= 8; colPos++) {
                ChessPiece somePiece = this.getPiece(new ChessPosition(rowPos, colPos));
                if (somePiece == null) {
                    boardString.append('-');
                } else {
                    boardString.append(somePiece.toChar());
                }
            }
            boardString.append('\n');
        }
        return boardString.toString();
    }

    public ChessBoard deepCopy() {
        String stringVisualization = visualizeBoard();
        return ChessBoard.fromString(stringVisualization);
    }

    @Override
    public String toString() {
        return "ChessBoard Object:\n" + this.visualizeBoard();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(boardSquares, that.boardSquares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(boardSquares);
    }
}
