package ui.client;

import chess.ChessPosition;
import endpointresponses.GameListResponse;
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

    public GameplayClient(ServerFacade serverFacade, ActiveGameTracker activeGameTracker) {
        this.serverFacade = serverFacade;
        this.activeGameTracker = activeGameTracker;
        this.serverMessageObserver = new ServerMessageObserver(activeGameTracker);
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
            throw new ConnectException("In order to start the Gameplay Client, the activeGameTracker must be updated to reflect the role of the player in their game.");
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
        GameData gameData = getGameData(activeGameTracker.getGameID());
        String result = "Redrawing game: \n\t";
        result += formatGameData(gameData);
        result += "\n\n";
        result += BoardRenderer.renderBoard(gameData.game(), activeGameTracker.getUserTeam());
        return new EvalResult(result, MY_STATE);
    }

    private EvalResult leave() throws IOException {
        // TODO: Implement rendering leave results with WS communication to leave game and update others
        webSocketFacade.leaveGame();
        webSocketFacade = null;
        serverMessageObserver.stop();
        return new EvalResult("", ClientStates.POSTLOGIN);
    }

    private EvalResult move(String[] params) {
        // TODO: Implement rendering move results with WS communication to perform move and update others
        // Update chessGameLatestVersion;
        webSocketFacade.moveInGame();
        return new EvalResult("", MY_STATE);
    }

    private EvalResult resign() throws IOException {
        // TODO: Implement rendering resign results with WS communication to resign game and update others
        webSocketFacade.forfeitGame();
        return new EvalResult("", MY_STATE);
    }

    private EvalResult show(String[] params) {
        try {
            if (params.length != 1) {
                throw new ResponseException(StatusReader.ResponseStatus.BAD_REQUEST, "Incorrect number of input parameters");
            }

            ChessPosition piecePosition = ChessPosition.positionFromFileRank(params[0]);

            GameData gameData = getGameData(activeGameTracker.getGameID());
            String result = "Showing legal moves for piece at %s in game: \n\t".formatted(piecePosition.getFileRank());
            result += formatGameData(gameData);
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

    private GameData getGameData(int activeGameID) throws ResponseException {
        GameListResponse gameListResponse = serverFacade.listGames(serverFacade.getAuthData().authToken());
        GameData gameData = gameListResponse.findGameData(activeGameID);
        if (gameData == null) {
            throw new ResponseException(StatusReader.ResponseStatus.BAD_REQUEST, "No game with the provided gameID could be located.");
        }
        return gameData;
    }

    private String formatGameData(GameData gameData) {
        String uiGameData = "";
        uiGameData += gameData.gameName();
        uiGameData += ": White Team - ";
        uiGameData += representPlayerName(gameData.whiteUsername());
        uiGameData += " | Black Team - ";
        uiGameData += representPlayerName(gameData.blackUsername());
        return uiGameData;
    }

    private String representPlayerName(String playerName) {
        if (playerName == null) {
            return "_____";
        }
        return playerName;
    }
}
