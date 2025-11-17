package ui;

import chess.ChessGame;
import chess.ChessPiece;

import java.util.HashMap;
import java.util.Map;

public class BoardRenderer {

    private static final String EMPTY_BOARD = """
           \sabcdefgh\s
           8********8
           7********7
           6********6
           5********5
           4********4
           3********3
           2********2
           1********1
           \sabcdefgh\s""";

    private static final String BOARD_FORMAT = """
           \sabcdefgh\s
           8********8
           7********7
           6********6
           5********5
           4********4
           3********3
           2********2
           1********1
           \sabcdefgh\s""";

    public static String renderBoard(ChessGame game, ChessGame.TeamColor teamColor) {
        String processedBoard = rawBoard(game);
        processedBoard = convertUnicode(processedBoard);
        processedBoard = wrapBoard(processedBoard);
        if (teamColor == ChessGame.TeamColor.BLACK) {
            processedBoard = rotateBoard(processedBoard);
        }
        return processedBoard;
    }

    private static String rawBoard(ChessGame game) {
        return game.getBoard().visualizeBoard();
    }

    private static String convertUnicode(String rawBoard) {
        StringBuilder unicodeBoard = new StringBuilder();
        HashMap<ChessPiece.PieceType, String> pieceMap = new HashMap<>();
        pieceMap.put(ChessPiece.PieceType.QUEEN, EscapeSequences.BLACK_QUEEN_SOLO);
        pieceMap.put(ChessPiece.PieceType.ROOK, EscapeSequences.BLACK_ROOK_SOLO);
        pieceMap.put(ChessPiece.PieceType.BISHOP, EscapeSequences.BLACK_BISHOP_SOLO);
        pieceMap.put(ChessPiece.PieceType.KING, EscapeSequences.BLACK_KING_SOLO);
        pieceMap.put(ChessPiece.PieceType.PAWN, EscapeSequences.BLACK_PAWN_SOLO);
        pieceMap.put(ChessPiece.PieceType.KNIGHT, EscapeSequences.BLACK_KNIGHT_SOLO);
        for (int i = 0; i < rawBoard.length(); i++) {
            Character posChar = rawBoard.charAt(i);
            ChessPiece posPiece = ChessPiece.fromLetter(posChar);
            if (posPiece != null) {
                unicodeBoard.append(pieceMap.get(posPiece.getPieceType()));
            } else {
                unicodeBoard.append(posChar);
            }
        }
        return unicodeBoard.toString();
    }

    private static String wrapBoard(String pieceBoard) {
        String rawBoardLinear = pieceBoard.replaceAll("\\s", "");
        StringBuilder fullBoard = new StringBuilder();
        for (int i = 0, j=0; i < EMPTY_BOARD.length(); i++) {
            if (EMPTY_BOARD.charAt(i) == '*') {
                fullBoard.append(rawBoardLinear.charAt(j));
                j++;
            } else {
                fullBoard.append(EMPTY_BOARD.charAt(i));
            }
        }
        return fullBoard.toString();
    }

    private static String rotateBoard(String board) {
        String[] boardLines = board.split("\\R");
        StringBuilder rotatedBoard = new StringBuilder();
        for (int i = boardLines.length-1; i >= 0; i--) {
            String boardLine = boardLines[i];
            for (int j = boardLine.length()-1; j >= 0; j--) {
                rotatedBoard.append(boardLine.charAt(j));
            }
        }
        return rotatedBoard.toString();
    }
}
