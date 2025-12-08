package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;
import java.util.HashMap;

public class BoardRenderer {

    // * indicated a position to be filled by a chess piece
    private static final String EMPTY_BOARD = """
           \sABCDEFGH\s
           8********8
           7********7
           6********6
           5********5
           4********4
           3********3
           2********2
           1********1
           \sABCDEFGH\s""";

    private static final int BOARD_SQUARE_WIDTH = 3;

    // "x" for border, "l" for light, "d" for dark
    private static final String BOARD_SQUARE_FORMAT = """
           xxxxxxxxxx
           xldldldldx
           xdldldldlx
           xldldldldx
           xdldldldlx
           xldldldldx
           xdldldldlx
           xldldldldx
           xdldldldlx
           xxxxxxxxxx""";

    public static String renderBoard(ChessGame game, ChessGame.TeamColor teamColor) {
        String processedBoard = rawBoard(game);
        processedBoard = wrapBoard(processedBoard);
        if (teamColor == ChessGame.TeamColor.BLACK) {
            processedBoard = rotateBoard(processedBoard);
        }
        processedBoard = padBoard(processedBoard);
        processedBoard = formatUnicode(processedBoard, null);
        return processedBoard;
    }

    public static String renderBoardMoves(ChessGame game, ChessGame.TeamColor teamColor, ChessPosition chessPosition) {
        String processedBoard = rawBoard(game);
        processedBoard = wrapBoard(processedBoard);
        // TODO: Evaluate moveOptions
        if (teamColor == ChessGame.TeamColor.BLACK) {
            processedBoard = rotateBoard(processedBoard);
            // TODO: Add rotation filtering for moveOptions
        }
        // TODO: Implement special coloration for moveOptions
        processedBoard = padBoard(processedBoard);
        return processedBoard;

    }

    private static String rawBoard(ChessGame game) {
        return game.getBoard().visualizeBoard();
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
            rotatedBoard.append("\n");
        }
        return rotatedBoard.toString();
    }

    private static String padBoard(String board) {
        StringBuilder paddedBoard = new StringBuilder();

        String[] boardLines = board.split("\\R");
        for (int i = 0; i < boardLines.length; i++) {
            String boardLine = boardLines[i];

            for (int j = 0; j < boardLine.length(); j++) {
                String boardChar = String.valueOf(boardLine.charAt(j));

                paddedBoard.append(" ");
                paddedBoard.append(boardChar);
                if (i == 0 | i == boardLines.length - 1 || j == 0 | j == boardLine.length() - 1) {
                    paddedBoard.append(EscapeSequences.EMPTY_SOLO);
                } else {
                    paddedBoard.append(" ");
                }
            }
            paddedBoard.append("\n");
        }
        return paddedBoard.toString();
    }

    private static String formatUnicode(String rawBoard, Collection<ChessMove> moveOptions) {
        StringBuilder unicodeBoard = new StringBuilder();

        String[] boardLines = rawBoard.split("\\R");
        String[] boardFormatLines = BOARD_SQUARE_FORMAT.split("\\R");
        // Iterate over each row, tracking row position
        for (int rowPos = 0; rowPos < boardLines.length; rowPos++) {
            String boardLine = boardLines[rowPos];
            // Iterate through the columns of text
            for (int j = 0; j < boardLine.length(); j++) {
                // Track the column position based on the board square width
                int colPos = j / BOARD_SQUARE_WIDTH;
                // If it's one of moveOptions' start or end positions, give it a special color
                if (moveOptions != null) {
                    // TODO: Implement special square rendering for move options
                }
                // If it's the start of a new square, set the background color.
                if (j % BOARD_SQUARE_WIDTH == 0) {
                    switch (boardFormatLines[rowPos].charAt(colPos)) {
                        case 'x' -> unicodeBoard.append(EscapeSequences.SET_BG_COLOR_DARK_GREEN);
                        case 'l' -> unicodeBoard.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
                        case 'd' -> unicodeBoard.append(EscapeSequences.SET_BG_COLOR_DARK_GREY);
                    }
                }

                char posChar = boardLine.charAt(j);
                unicodeBoard.append(convertCharToUnicode(posChar, rowPos, colPos));
            }
            unicodeBoard.append(EscapeSequences.RESET_BG_COLOR);
            unicodeBoard.append("\n");
        }
        return unicodeBoard.toString();
    }

    private static String convertCharToUnicode (char posChar, int rowPos, int colPos) {
        HashMap<ChessPiece.PieceType, String> pieceMap = new HashMap<>();
        pieceMap.put(ChessPiece.PieceType.QUEEN, EscapeSequences.BLACK_QUEEN_SOLO);
        pieceMap.put(ChessPiece.PieceType.ROOK, EscapeSequences.BLACK_ROOK_SOLO);
        pieceMap.put(ChessPiece.PieceType.BISHOP, EscapeSequences.BLACK_BISHOP_SOLO);
        pieceMap.put(ChessPiece.PieceType.KING, EscapeSequences.BLACK_KING_SOLO);
        pieceMap.put(ChessPiece.PieceType.PAWN, EscapeSequences.BLACK_PAWN_SOLO);
        pieceMap.put(ChessPiece.PieceType.KNIGHT, EscapeSequences.BLACK_KNIGHT_SOLO);

        StringBuilder unicodeString = new StringBuilder();
        // If a character is not whitespace...
        if (!Character.isWhitespace(posChar)) {
            // And it's on the piece section of the board...
            if ( rowPos > 0 && rowPos < 9 &&  colPos > 0 && colPos < 9) {
                ChessPiece posPiece = ChessPiece.fromLetter(posChar);
                // And it can be rendered as a piece...
                if (posPiece != null) {
                    // Set the Unicode color based on the piece color
                    if (posPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        unicodeString.append(EscapeSequences.SET_TEXT_COLOR_WHITE);
                    } else {
                        unicodeString.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
                    }
                    // Convert from the plaintext representation to the fancy character
                    unicodeString.append(pieceMap.get(posPiece.getPieceType()));
                    unicodeString.append(EscapeSequences.RESET_TEXT_COLOR);
                    // Otherwise, it must be an empty space. Add a wide space in its place.
                } else {
                    unicodeString.append(EscapeSequences.EMPTY_SOLO);
                }
                // Otherwise, it must be a row or column label. Add it in green unchanged.
            } else {
                unicodeString.append(EscapeSequences.SET_TEXT_COLOR_GREEN);
                unicodeString.append(EscapeSequences.SET_TEXT_BOLD);
                unicodeString.append(posChar);
                unicodeString.append(EscapeSequences.RESET_TEXT_BOLD_FAINT);
                unicodeString.append(EscapeSequences.RESET_TEXT_COLOR);
            }
            // Otherwise, it must be a whitespace character. Add it unchanged.
        } else {
            unicodeString.append(posChar);
        }
        return unicodeString.toString();
    }
}
