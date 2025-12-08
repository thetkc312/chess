package chess;

import java.util.Map;
import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int row;
    private final int column;


    private final static Map<Character, Integer> FILE_COL_MAP = Map.of('a', 1,
                                                                       'b', 2,
                                                                       'c', 3,
                                                                       'd', 4,
                                                                       'e', 5,
                                                                       'f', 6,
                                                                       'g', 7,
                                                                       'h', 8);
    private final static Map<Character, Integer> RANK_ROW_MAP = Map.of('1', 1,
                                                                       '2', 2,
                                                                       '3', 3,
                                                                       '4', 4,
                                                                       '5', 5,
                                                                       '6', 6,
                                                                       '7', 7,
                                                                       '8', 8);

    public ChessPosition(int row, int column) {
        this.row = row;
        this.column = column;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return column;
    }

    public String getFileRank() {
        StringBuilder fileRank = new StringBuilder();
        for (Map.Entry<Character, Integer> entry : FILE_COL_MAP.entrySet()) {
            if (column == entry.getValue()) {
                fileRank.append(entry.getKey());
            }
        }
        for (Map.Entry<Character, Integer> entry : RANK_ROW_MAP.entrySet()) {
            if (row == entry.getValue()) {
                fileRank.append(entry.getKey());
            }
        }
        return fileRank.toString();
    }

    /**
     * @return a new ChessPosition object derived from some movement based on this piece's position
     */
    public ChessPosition getMovedPosition(int moveRow, int moveCol) {
        return new ChessPosition(row + moveRow, column + moveCol);
    }

    /**
     *
     * @return whether this position is on the board
     */
    public boolean isOnBoard() {
        return !(row < 1 || row > 8 || column < 1 || column > 8);
    }

    public static ChessPosition positionFromFileRank(String fileRank) throws IllegalArgumentException {
        if (fileRank.length() != 2) {
            throw new IllegalArgumentException("Chess position is incorrectly formatted and does not have a length of 2.");
        }
        int column = ChessPosition.fileToCol(fileRank.charAt(0));
        int row = ChessPosition.rankToRow(fileRank.charAt(1));
        return new ChessPosition(row, column);
    }

    private static int fileToCol(char fileChar) throws IllegalArgumentException {
        fileChar = Character.toLowerCase(fileChar);
        if (FILE_COL_MAP.containsKey(fileChar)) {
            return FILE_COL_MAP.get(fileChar);
        } else {
            throw new IllegalArgumentException("Chess position is incorrectly formatted and the file could not be interpreted.");
        }
    }

    private static int rankToRow(char rowChar) throws IllegalArgumentException {
        rowChar = Character.toLowerCase(rowChar);
        if (RANK_ROW_MAP.containsKey(rowChar)) {
            return RANK_ROW_MAP.get(rowChar);
        } else {
            throw new IllegalArgumentException("Chess position is incorrectly formatted and the rank could not be interpreted.");
        }
    }

    @Override
    public String toString() {
        return String.format("[%d,%d]", row, column);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPosition that = (ChessPosition) o;
        return row == that.row && column == that.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }
}
