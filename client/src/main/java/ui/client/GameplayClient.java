package ui.client;

import server.ServerFacade;
import server.websocket.WebSocketFacade;
import ui.states.ClientStates;
import ui.BoardRenderer;

import java.net.ConnectException;

public class GameplayClient {

    private final ServerFacade serverFacade;
    private static final ClientStates MY_STATE = ClientStates.GAMEPLAY;

    private WebSocketFacade webSocketFacade = null;

    public GameplayClient(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public void startWebSocket() throws ConnectException {
        try {
            this.webSocketFacade = new WebSocketFacade();
        } catch (Exception e) {
            throw new ConnectException(e.getMessage());
        }
    }

    public EvalResult eval(String cmd, String[] params) throws ConnectException {
        if (this.webSocketFacade == null) {
            startWebSocket();
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
        return new EvalResult("", MY_STATE);
    }

    private EvalResult leave() {
        return new EvalResult("", ClientStates.POSTLOGIN);
    }

    private EvalResult move(String[] params) {
        return new EvalResult("", MY_STATE);
    }

    private EvalResult resign() {
        return new EvalResult("", MY_STATE);
    }

    private EvalResult show(String[] params) {
        return new EvalResult("", MY_STATE);
    }
}
