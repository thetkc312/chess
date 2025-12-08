package ui.client;

import chess.ChessGame;
import server.ServerFacade;
import server.websocket.ActiveGameTracker;
import server.websocket.ServerMessageObserver;
import server.websocket.WebSocketFacade;
import ui.states.ClientStates;

import java.net.ConnectException;

public class GameplayClient {
    private static final ClientStates MY_STATE = ClientStates.GAMEPLAY;

    private final ServerFacade serverFacade;
    private final ActiveGameTracker activeGameTracker;
    private final ServerMessageObserver serverMessageObserver;

    private WebSocketFacade webSocketFacade = null;
    private ChessGame chessGameLatestVersion = null;

    public GameplayClient(ServerFacade serverFacade, ActiveGameTracker activeGameTracker) {
        this.serverFacade = serverFacade;
        this.activeGameTracker = activeGameTracker;
        this.serverMessageObserver = new ServerMessageObserver();
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
        return switch (cmd) {
            case "help", "h" -> help();
            case "redraw", "r" -> redraw();
            case "leave", "l" -> leave();
            case "move", "m" -> move(params);
            case "resign" -> resign();
            case "show", "s" -> show(params);
            default -> help();
        };
    }

    private EvalResult help() {
        String result = """
        Here are the actions available to you while in a game:
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
        // TODO: Implement rendering redraw results with standard HTTP request to get board
        serverFacade.listGames(serverFacade.getAuthData().authToken());
        // Update chessGameLatestVersion;
        return new EvalResult("", MY_STATE);
    }

    private EvalResult leave() {
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

    private EvalResult resign() {
        // TODO: Implement rendering resign results with WS communication to resign game and update others
        webSocketFacade.forfeitGame();
        return new EvalResult("", MY_STATE);
    }

    private EvalResult show(String[] params) {
        // TODO: Implement rendering show results with standard HTTP request to get board
        serverFacade.listGames(serverFacade.getAuthData().authToken());
        return new EvalResult("", MY_STATE);
    }
}
