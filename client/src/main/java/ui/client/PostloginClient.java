package ui.client;

import endpointrequests.LoginBody;
import model.AuthData;
import server.ResponseException;
import server.ServerFacade;
import server.StatusReader;
import ui.states.ClientStates;

public class PostloginClient {

    private final ServerFacade serverFacade;
    private static final ClientStates MY_STATE = ClientStates.POSTLOGIN;

    public PostloginClient(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public EvalResult eval(String cmd, String[] params) {
        return switch (cmd) {
            case "help", "h" -> help();
            case "quit", "q" -> quit();
            case "logout" -> logout();
            case "create", "c" -> create();
            case "list", "l" -> list();
            case "join", "j" -> join();
            case "observe", "o" -> observe();
            default -> help();
        };
    }

    private EvalResult help() {
        String result = """
        Here are the actions available to you while logged in:
        \thelp - see the possible commands
        \tquit - exit the program
        \tlogout - sign out and return to the initial interface
        \tcreate <NAME> - add and name a new game
        \tlist - view all active games
        \tjoin <ID> [WHITE|BLACK] - enter a game by its ID as the specified player
        \tobserve <ID> - watch a game as a spectator
        
        """;
        return new EvalResult(result, MY_STATE);
    }

    private EvalResult quit() {
        return new EvalResult("", ClientStates.QUIT);
    }

    private EvalResult logout() {
        try {
            serverFacade.logout(serverFacade.getAuthData().authToken());
            serverFacade.clearAuthData();

            String result = """
            You have successfully logged out.
            
            """;
            return new EvalResult(result, ClientStates.PRELOGIN);
        } catch (ResponseException e) {
            String result = "There was an issue while logging out: ";
            switch (e.responseStatus) {
                case UNAUTHORIZED -> result += "Your session may have already been logged out. Restarting the program may help.";
                case SERVER_ERROR -> result += "There was a server-side issue, please try again later.";
                default -> result += "Please try again later.";
            }
            result += "\n";
            return new EvalResult(result, MY_STATE);
        }
    }

    private EvalResult create() {
        return new EvalResult("", ClientStates.QUIT);
    }

    private EvalResult list() {
        return new EvalResult("", ClientStates.QUIT);
    }

    private EvalResult join() {
        return new EvalResult("", ClientStates.QUIT);
    }

    private EvalResult observe() {
        return new EvalResult("", ClientStates.QUIT);
    }
}
