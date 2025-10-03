package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard activeBoard;
    private TeamColor activeTeam;

    // Instantiation of a ChessGame object will create and reset a ChessBoard object
    public ChessGame() {
        activeBoard = new ChessBoard();
        activeBoard.resetBoard();
        activeTeam = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return activeTeam;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        activeTeam = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (startPosition.isOnBoard()) {
            ChessPiece movePiece = activeBoard.getPiece(startPosition);
            if (movePiece != null) {
                return movePiece.pieceMoves(activeBoard, startPosition);
            }
        }
        return null;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        Collection<ChessMove> hypotheticalMoves = validMoves(startPosition);
        // hypotheticalMoves will only be null if the starting position was off the board or empty
        if (hypotheticalMoves == null) {
            // Check why hypotheticalMoves is null
            if (!startPosition.isOnBoard()) {
                throw new InvalidMoveException("Provided ChessMove object has a starting position that is off the board.");
            } else {
                throw new InvalidMoveException("Provided ChessMove object has a starting position where there is no piece.");
            }
        }
        // Check if the desired move would move a piece from the enemy team
        if (activeTeam != activeBoard.getPiece(startPosition).getTeamColor()) {
            throw new InvalidMoveException("Provided ChessMove object would move a piece out of turn.");
        }
        // Check if the desired move is among the hypothetically possible moves
        if (!hypotheticalMoves.contains(move))
            throw new InvalidMoveException("Provided ChessMove object does not represent a valid move for this piece in this board-state.");
        // TODO: Check if the desired move would put the King in check
        /*
        ChessBoard hypotheticalBoard = activeBoard.deepCopy();
        applyMove(hypotheticalBoard, move);
        ChessPosition kingPosition = hypotheticalBoard.findPiece(new ChessPiece(activeTeam, ChessPiece.PieceType.KING));
        if (reverseSearchCheckAll(hypotheticalBoard, kingPosition, activeTeam))
            throw new InvalidMoveException("Provided ChessMove object is a possible move, but would leave the king in check");
         */
        // TODO: Check if the King is currently in check and this move does not address that

        // Logic for moving a chess piece on the board when it is known that the move is valid
        applyMove(activeBoard, move);
        if (activeTeam == TeamColor.WHITE) {
            activeTeam = TeamColor.BLACK;
        } else {
            activeTeam = TeamColor.WHITE;
        }
    }

    private void applyMove(ChessBoard someBoard, ChessMove someMove) {
        ChessPosition startPosition = someMove.getStartPosition();
        ChessPosition endPosition = someMove.getEndPosition();
        ChessPiece.PieceType promotionType = someMove.getPromotionPiece();
        ChessPiece movingPiece = someBoard.getPiece(startPosition);
        if (promotionType == null) {
            movingPiece = new ChessPiece(movingPiece.getTeamColor(), movingPiece.getPieceType());
        } else {
            movingPiece = new ChessPiece(movingPiece.getTeamColor(), promotionType);
        }
        someBoard.addPiece(startPosition, null);
        // Since this logic is only reached if the move is among the hypotheticalMoves (which must be valid), we don't need to check if the endPosition is valid.
        someBoard.addPiece(endPosition, movingPiece);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPiece targetKing = new ChessPiece(teamColor, ChessPiece.PieceType.KING);
        ChessPosition kingPosition = activeBoard.findPiece(targetKing);
        ChessBoard hypotheticalBoard = activeBoard.deepCopy();
        return reverseSearchCheckAll(hypotheticalBoard, kingPosition, teamColor);
    }

    private boolean reverseSearchCheckAll(ChessBoard hypotheticalBoard, ChessPosition kingPosition, TeamColor teamColor) {
        return (reverseSearchCheck(hypotheticalBoard.deepCopy(), kingPosition, teamColor, ChessPiece.PieceType.QUEEN) ||
                reverseSearchCheck(hypotheticalBoard.deepCopy(), kingPosition, teamColor, ChessPiece.PieceType.ROOK) ||
                reverseSearchCheck(hypotheticalBoard.deepCopy(), kingPosition, teamColor, ChessPiece.PieceType.BISHOP) ||
                reverseSearchCheck(hypotheticalBoard.deepCopy(), kingPosition, teamColor, ChessPiece.PieceType.KNIGHT) ||
                reverseSearchCheck(hypotheticalBoard.deepCopy(), kingPosition, teamColor, ChessPiece.PieceType.PAWN) ||
                reverseSearchCheck(hypotheticalBoard.deepCopy(), kingPosition, teamColor, ChessPiece.PieceType.KING));
    }

    private boolean reverseSearchCheck(ChessBoard hypotheticalBoard, ChessPosition kingPosition, TeamColor teamColor, ChessPiece.PieceType checkPieceType) {
        // For each piece type, see where a piece of that type could move from the kings position, and check if any of those positions contain enemy pieces of that type
        Collection<ChessMove> reverseMoves;
        ChessPiece reverseCheckPiece = new ChessPiece(teamColor, checkPieceType);
        hypotheticalBoard.addPiece(kingPosition, reverseCheckPiece);
        reverseMoves = reverseCheckPiece.pieceMoves(hypotheticalBoard, kingPosition);
        for (ChessMove reverseMove : reverseMoves) {
            ChessPosition reverseMoveEndPos = reverseMove.getEndPosition();
            ChessPiece reverseMoveEndPiece = hypotheticalBoard.getPiece(reverseMoveEndPos);
            if (reverseMoveEndPiece == null)
                continue;
            if (reverseMoveEndPiece.getTeamColor() != teamColor && reverseMoveEndPiece.getPieceType() == checkPieceType) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        activeBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return activeBoard.deepCopy();
    }

    @Override
    public String toString() {
        return String.format("ChessGame{activeTeam: %s, activeBoard:\n%s\n}", activeTeam, activeBoard);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(activeBoard, chessGame.activeBoard) && activeTeam == chessGame.activeTeam;
    }

    @Override
    public int hashCode() {
        return Objects.hash(activeBoard, activeTeam);
    }
}
