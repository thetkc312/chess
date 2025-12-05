package ui.client;

import server.ServerFacade;
import ui.states.ClientStates;

public class GameplayClient {

    private final ServerFacade serverFacade;
    private static final ClientStates MY_STATE = ClientStates.GAMEPLAY;

    public GameplayClient(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public EvalResult eval(String cmd, String[] params) {
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
        return null;
    }

    private EvalResult leave() {
        return null;
    }

    private EvalResult move(String[] params) {
        return null;
    }

    private EvalResult resign() {
        return null;
    }

    private EvalResult show(String[] params) {
        return null;
    }
}
