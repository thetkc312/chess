package ui.client;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;
import server.ResponseException;
import server.ServerFacade;
import server.StatusReader;
import server.websocket.ActiveGameTracker;
import server.websocket.ServerMessageObserver;
import server.websocket.WebSocketFacade;
import ui.BoardRenderer;
import ui.ConsolePrinter;
import ui.states.ClientStates;

import java.io.IOException;
import java.net.ConnectException;

public class GameplayClient {
    private static final ClientStates MY_STATE = ClientStates.GAMEPLAY;

    private final ServerFacade serverFacade;
    private final ActiveGameTracker activeGameTracker;
    private final ServerMessageObserver serverMessageObserver;

    private WebSocketFacade webSocketFacade = null;
    private boolean attemptingResign;

    public GameplayClient(ServerFacade serverFacade, ActiveGameTracker activeGameTracker) {
        this.serverFacade = serverFacade;
        this.activeGameTracker = activeGameTracker;
        this.serverMessageObserver = new ServerMessageObserver(activeGameTracker);
        attemptingResign = false;
    }

    public void startWebSocket() throws ConnectException {
        try {
            Thread wsListenerThread = new Thread(serverMessageObserver::messageListener);
            wsListenerThread.setDaemon(true);
            wsListenerThread.start();
            this.webSocketFacade = new WebSocketFacade(serverMessageObserver, activeGameTracker, serverFacade.getAuthData());
        } catch (Exception e) {
            throw new ConnectException(e.getMessage());
        }
    }

    public EvalResult eval(String cmd, String[] params) throws ConnectException {
        if (this.webSocketFacade == null) {
            startWebSocket();
        }
        if (activeGameTracker.getUserRole() == null) {
            throw new ConnectException(
                    "In order to start the Gameplay Client, the activeGameTracker must be updated to reflect the role of the player in their game.");
        }
        try {
            return switch (cmd) {
                case "help", "h" -> help();
                case "redraw", "r" -> redraw();
                case "leave", "l" -> leave();
                case "move", "m" -> move(params);
                case "resign" -> resign();
                case "show", "s" -> show(params);
                default -> help();
            };
        } catch (IOException ignore) {
            ConsolePrinter.safePrint("There was an error sending your action to the server, please try again later.\n");
            return help();
        }
    }

    private EvalResult help() {
        attemptingResign = false;
        String result = """
        Here are the actions available to you while in a game (write piece positions as <FILE><RANK>):
        \thelp - see the possible commands
        \tredraw - draw the board again
        \tleave - exit this game
        \tmove <PIECE POSITION> <MOVE POSITION> - move one of your pieces to a position
        \tresign - after confirmation, forfeit the game without leaving
        \tshow <PIECE POSITION> - display the valid legal move options for a piece on the board
        
        """;
        return new EvalResult(result, MY_STATE);
    }

    private EvalResult redraw() {
        attemptingResign = false;
        GameData gameData = GameGetter.getGameData(activeGameTracker.getGameID(), serverFacade);
        String result = "Redrawing game: \n\t";
        result += GameGetter.formatGameData(gameData);
        result += "\n\n";
        result += BoardRenderer.renderBoard(gameData.game(), activeGameTracker.getUserTeam());
        return new EvalResult(result, MY_STATE);
    }

    private EvalResult leave() throws IOException {
        attemptingResign = false;

        webSocketFacade.leaveGame();
        webSocketFacade = null;
        serverMessageObserver.stop();
        activeGameTracker.setUserRole(null);
        return new EvalResult("", ClientStates.POSTLOGIN);
    }

    private EvalResult move(String[] params) throws IOException {
        attemptingResign = false;
        try {
            if (params.length != 2 & params.length != 3) {
                throw new ResponseException(StatusReader.ResponseStatus.BAD_REQUEST, "Incorrect number of input parameters");
            }

            ChessPosition startPosition = ChessPosition.positionFromFileRank(params[0]);
            ChessPosition endPosition = ChessPosition.positionFromFileRank(params[1]);
            ChessPiece.PieceType promotionType = null;
            if (params.length == 3) {
                ChessPiece promotionPiece = ChessPiece.fromLetter(params[2].charAt(0));
                if (promotionPiece != null) {
                    promotionType = promotionPiece.getPieceType();
                }
            }
            ChessMove move = new ChessMove(startPosition, endPosition, promotionType);

            webSocketFacade.moveInGame(move);
            return new EvalResult("", MY_STATE);

        } catch (IllegalArgumentException|ResponseException e) {
            String result = "There was an issue while trying show the legal moves for a piece.";
            result += """
            \nBe sure to request to show the legal moves for a piece as follows:
            \tshow <file><rank> - watch a game as a spectator
            \t\ti.e. show a1
            
            """;
            return new EvalResult(result, MY_STATE);
        }
    }

    private EvalResult resign() throws IOException {
        if (!attemptingResign) {
            attemptingResign = true;
            return new EvalResult("Please type 'resign' again to confirm if you would like to resign:\n", MY_STATE);
        } else {
            attemptingResign = false;
            webSocketFacade.forfeitGame();
            return new EvalResult("", MY_STATE);
        }
    }

    private EvalResult show(String[] params) {
        try {
            if (params.length != 1) {
                throw new ResponseException(StatusReader.ResponseStatus.BAD_REQUEST, "Incorrect number of input parameters");
            }

            ChessPosition piecePosition = ChessPosition.positionFromFileRank(params[0]);

            GameData gameData = GameGetter.getGameData(activeGameTracker.getGameID(), serverFacade);
            String result = "Showing legal moves for piece at %s in game: \n\t".formatted(piecePosition.getFileRank());
            result += GameGetter.formatGameData(gameData);
            result += "\n\n";
            result += BoardRenderer.renderBoardMoves(gameData.game(), activeGameTracker.getUserTeam(), piecePosition);
            return new EvalResult(result, MY_STATE);

        } catch (IllegalArgumentException|ResponseException e) {
            String result = "There was an issue while trying show the legal moves for a piece.";
            result += """
            \nBe sure to request to show the legal moves for a piece as follows:
            \tshow <file><rank> - watch a game as a spectator
            \t\ti.e. show a1
            
            """;
            return new EvalResult(result, MY_STATE);
        }
    }
}
