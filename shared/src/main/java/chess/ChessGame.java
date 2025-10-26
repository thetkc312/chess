package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard activeBoard;
    private TeamColor activeTeam;
    private ArrayList<ChessMove> moveHistory;

    // Instantiation of a ChessGame object will create and reset a ChessBoard object
    public ChessGame() {
        activeBoard = new ChessBoard();
        activeBoard.resetBoard();
        activeTeam = TeamColor.WHITE;
        moveHistory = new ArrayList<>();
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
                Collection<ChessMove> legalValidMoves = new HashSet<>();
                for (ChessMove movementValidMove : movePiece.pieceMoves(activeBoard, startPosition)) {
                    // Confirm that the desired move does not end with the King in check
                    ChessBoard hypotheticalBoard = activeBoard.deepCopy();
                    applyMove(hypotheticalBoard, movementValidMove);
                    ChessPosition kingPosition = hypotheticalBoard.findPiece(new ChessPiece(movePiece.getTeamColor(), ChessPiece.PieceType.KING)).iterator().next();
                    // If this move results in a hypotheticalBoard where there are no ways that the moving team's King is in check, add it to the legalValidMove set
                    if (reverseSearchCheckAll(hypotheticalBoard, kingPosition, movePiece.getTeamColor()).isEmpty())
                        legalValidMoves.add(movementValidMove);
                }
                return legalValidMoves;
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

        // Verify that the starting position is on the board
        if (!startPosition.isOnBoard()) {
            throw new InvalidMoveException("Provided ChessMove object has a starting position that is off the board.");
        }

        Collection<ChessMove> hypotheticalMoves = validMoves(startPosition);
        // hypotheticalMoves will only be null if the starting position was empty
        if (hypotheticalMoves == null) {
            throw new InvalidMoveException("Provided ChessMove object has a starting position where there is no piece.");
        }

        // Check if the desired move would move a piece from the team that is not active (out of turn move)
        if (activeTeam != activeBoard.getPiece(startPosition).getTeamColor()) {
            throw new InvalidMoveException("Provided ChessMove object would move a piece out of turn.");
        }

        // Verify that the desired move is among the hypothetically possible moves
        if (!hypotheticalMoves.contains(move))
            throw new InvalidMoveException("Provided ChessMove object does not represent a valid move for this piece in this board-state.");

        // Logic for moving a chess piece on the board when it is known that the move is valid
        applyMove(activeBoard, move);
        moveHistory.add(move);
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
        ChessPosition kingPosition = activeBoard.findPiece(targetKing).iterator().next();
        // If the HashSet of moves causing check is not empty, then the piece is in check
        return (!reverseSearchCheckAll(activeBoard, kingPosition, teamColor).isEmpty());
    }

    private HashSet<ChessMove> reverseSearchCheckAll(ChessBoard hypotheticalBoard, ChessPosition kingPosition, TeamColor teamColor) {
        HashSet<ChessMove> movesCausingCheck = new HashSet<>(0);
        movesCausingCheck.addAll(reverseSearchCheck(hypotheticalBoard.deepCopy(), kingPosition, teamColor, ChessPiece.PieceType.QUEEN, false));
        movesCausingCheck.addAll(reverseSearchCheck(hypotheticalBoard.deepCopy(), kingPosition, teamColor, ChessPiece.PieceType.ROOK, false));
        movesCausingCheck.addAll(reverseSearchCheck(hypotheticalBoard.deepCopy(), kingPosition, teamColor, ChessPiece.PieceType.BISHOP, false));
        movesCausingCheck.addAll(reverseSearchCheck(hypotheticalBoard.deepCopy(), kingPosition, teamColor, ChessPiece.PieceType.KNIGHT, true));
        movesCausingCheck.addAll(reverseSearchCheck(hypotheticalBoard.deepCopy(), kingPosition, teamColor, ChessPiece.PieceType.PAWN, false));
        movesCausingCheck.addAll(reverseSearchCheck(hypotheticalBoard.deepCopy(), kingPosition, teamColor, ChessPiece.PieceType.KING, false));
        return movesCausingCheck;
    }

    private HashSet<ChessMove> reverseSearchCheck(ChessBoard hypotheticalBoard, ChessPosition kingPosition, TeamColor teamColor, ChessPiece.PieceType checkPieceType, boolean pieceCanJump) {
        // For each piece type, see where a piece of that type could move from the kings position
        HashSet<ChessMove> movesCausingCheck = new HashSet<>(0);
        ChessPiece reverseCheckPiece = new ChessPiece(teamColor, checkPieceType);
        hypotheticalBoard.addPiece(kingPosition, reverseCheckPiece);
        Collection<ChessMove> reverseMoves = reverseCheckPiece.pieceMoves(hypotheticalBoard, kingPosition);
        for (ChessMove reverseMove : reverseMoves) {
            ChessPosition reverseMoveEndPos = reverseMove.getEndPosition();
            ChessPiece reverseMoveEndPiece = hypotheticalBoard.getPiece(reverseMoveEndPos);
            if (reverseMoveEndPiece == null)
                continue;
            // Check if any of those positions contain enemy pieces of that type. If so, that piece could move into the king's space and is causing a check.
            if (reverseMoveEndPiece.getTeamColor() != teamColor && reverseMoveEndPiece.getPieceType() == checkPieceType) {
                // If pieceCanJump is false, it might be possible block the piece somewhere along its path, so all reverse moves end positions are candidates for ending check.
                if (!pieceCanJump) {
                    movesCausingCheck.addAll(reverseMoves);
                    break;
                }
                movesCausingCheck.add(reverseMove);
            }
        }
        return movesCausingCheck;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // First, see if the king of teamColor is in check. If so, save all the associated reverseSearchCheck moves.
        ChessPiece targetKing = new ChessPiece(teamColor, ChessPiece.PieceType.KING);
        ChessPosition kingPosition = activeBoard.findPiece(targetKing).iterator().next();
        HashSet<ChessMove> checkingMoves = reverseSearchCheckAll(activeBoard, kingPosition, teamColor);
        if (checkingMoves.isEmpty())
            return false;
        // For all pieces of the team in check, see if there are any validMoves (moves that don't leave the king in Check). If there are no valid moves, the king is in Checkmate.
        return !validMovesExist(activeBoard, teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        // First, see if the king is in check. If so, it cannot be stalemate.
        if (isInCheck(teamColor))
            return false;
        // If there are no valid moves, this is a stalemate.
        return !validMovesExist(activeBoard, teamColor);
    }

    private boolean validMovesExist(ChessBoard someBoard, ChessGame.TeamColor someTeam) {
        // Consider all chess pieces
        for (ChessPiece.PieceType pieceType : ChessPiece.PieceType.values()) {
            ChessPiece piece = new ChessPiece(someTeam, pieceType);
            Collection<ChessPosition> livingPieces = someBoard.findPiece(piece);
            // If any pieces of that type could be found belonging to this team...
            for (ChessPosition potentialCheckSavior : livingPieces) {
                // ... return true if any one among them has a valid move.
                if (!validMoves(potentialCheckSavior).isEmpty())
                    return true;
            }
        }
        // If all validMoves generated for this team's pieces are empty, there are no valid moves.
        return false;
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
