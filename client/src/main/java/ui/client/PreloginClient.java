package ui.client;

import endpointrequests.LoginBody;
import model.AuthData;
import model.UserData;
import server.ResponseException;
import server.ServerFacade;
import server.StatusReader;
import ui.states.ClientStates;

public class PreloginClient {

    private final ServerFacade serverFacade;
    private static final ClientStates MY_STATE = ClientStates.PRELOGIN;

    public PreloginClient(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public EvalResult eval(String cmd, String[] params) {
        return switch (cmd) {
            case "help", "h" -> help();
            case "quit", "q" -> quit();
            case "login", "l" -> login(params);
            case "register", "r" -> register(params);
            default -> help();
        };
    }

    private EvalResult help() {
        String result = """
        Here are the actions available to you before logging in:
        \thelp - see the possible commands
        \tquit - exit the program
        \tlogin <USERNAME> <PASSWORD> - login with an existing account
        \tregister <USERNAME> <PASSWORD> <EMAIL> - create a new account
        
        """;
        return new EvalResult(result, MY_STATE);
    }

    private EvalResult quit() {
        return new EvalResult("", ClientStates.QUIT);
    }

    private EvalResult login(String[] params) {
        try {
            if (params.length != 2) throw new ResponseException(StatusReader.ResponseStatus.BAD_REQUEST, "");

            LoginBody loginBody = new LoginBody(params[0], params[1]);
            AuthData authData = serverFacade.login(loginBody);
            serverFacade.setAuthData(authData);

            String result = """
            You have successfully logged in!
            
            """;
            return new EvalResult(result, ClientStates.POSTLOGIN);
        } catch (ResponseException e) {
            String result = "There was an issue while logging in: ";
            switch (e.responseStatus) {
                case BAD_REQUEST -> result += "Your login credentials were formatted incorrectly.";
                case UNAUTHORIZED -> result += "Your login credentials don't match any known users.";
                case SERVER_ERROR -> result += "There was a server-side issue, please try again later.";
                default -> result += "Please try again later.";
            }
            result += """
            \nBe sure to provide your username and password as follows:
            \tlogin <USERNAME> <PASSWORD> - login with an existing account
            
            """;
            return new EvalResult(result, MY_STATE);
        }
    }

    private EvalResult register(String[] params) {
        try {
            if (params.length != 3) throw new ResponseException(StatusReader.ResponseStatus.BAD_REQUEST, "");

            UserData registerBody = new UserData(params[0], params[1], params[2]);
            AuthData authData = serverFacade.register(registerBody);
            serverFacade.setAuthData(authData);

            String result = """
            You have successfully registered and are now logged in!
            
            """;
            return new EvalResult(result, ClientStates.POSTLOGIN);
        } catch (ResponseException e) {
            String result = "There was an issue while registering: ";
            switch (e.responseStatus) {
                case BAD_REQUEST -> result += "Your registration input was formatted incorrectly.";
                case ALREADY_TAKEN -> result += "Username is already taken.";
                case SERVER_ERROR -> result += "There was a server-side issue, please try again later.";
                default -> result += "Please try again later.";
            }
            result += """
            \nBe sure to provide your username, password and email as follows:
            \tregister <USERNAME> <PASSWORD> <EMAIL> - create a new account
            
            """;
            return new EvalResult(result, MY_STATE);
        }
    }

}
