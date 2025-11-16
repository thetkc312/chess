package ui.client;

import endpointrequests.CreateGameBody;
import endpointrequests.LoginBody;
import endpointresponses.CreateGameResponse;
import endpointresponses.GameListResponse;
import model.AuthData;
import model.GameData;
import server.ResponseException;
import server.ServerFacade;
import server.StatusReader;
import ui.states.ClientStates;

import java.util.ArrayList;
import java.util.HashMap;

public class PostloginClient {

    private final ServerFacade serverFacade;
    private static final ClientStates MY_STATE = ClientStates.POSTLOGIN;

    private HashMap<Integer, Integer> gamesListed;

    public PostloginClient(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
        this.gamesListed = new HashMap<>();
    }

    public EvalResult eval(String cmd, String[] params) {
        return switch (cmd) {
            case "help", "h" -> help();
            case "quit", "q" -> quit();
            case "logout" -> logout();
            case "create", "c" -> create(params);
            case "list", "l" -> list();
            case "join", "j" -> join(params);
            case "observe", "o" -> observe(params);
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
        logout();
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

    private EvalResult create(String[] params) {
        try {
            if (params.length != 1) throw new ResponseException(StatusReader.ResponseStatus.BAD_REQUEST, "");

            CreateGameBody createGameBody = new CreateGameBody(params[0]);
            // Note that the gameID of the resulting game is discarded
            serverFacade.createGame(createGameBody, serverFacade.getAuthData().authToken());

            String result = """
            You have successfully created a game.
            
            """;
            return new EvalResult(result, ClientStates.POSTLOGIN);
        } catch (ResponseException e) {
            String result = "There was an issue while creating a game: ";
            switch (e.responseStatus) {
                case BAD_REQUEST -> result += "Your game creation input was formatted incorrectly.";
                case UNAUTHORIZED -> result += "Your session may have been logged out. Restarting the program may help.";
                case SERVER_ERROR -> result += "There was a server-side issue, please try again later.";
                default -> result += "Please try again later.";
            }
            result += """
            \nBe sure to provide the game name as follows:
            \tcreate <NAME> - add and name a new game
            
            """;
            return new EvalResult(result, MY_STATE);
        }
    }

    private EvalResult list() {
        try {
            GameListResponse gameListResponse = serverFacade.listGames(serverFacade.getAuthData().authToken());
            ArrayList<GameData> gameListData = gameListResponse.games();
            StringBuilder result = new StringBuilder();
            if (gameListData.isEmpty()) {
                result.append("There are currently no active games.");
            } else {
                result.append("""
                Active Games:
                """);
                for (int i = 1; i <= gameListData.size(); i++) {
                    GameData gameData = gameListData.get(i - 1);
                    gamesListed.put(i, gameData.gameID());
                    result.append(String.format("\t%d) %s\n", i, formatGameData(gameData)));
                }
            }

            result.append("\n");
            return new EvalResult(result.toString(), MY_STATE);
        } catch (ResponseException e) {
            String result = "There was an issue while listing games: ";
            switch (e.responseStatus) {
                case UNAUTHORIZED -> result += "Your session may have already been logged out. Restarting the program may help.";
                case SERVER_ERROR -> result += "There was a server-side issue, please try again later.";
                default -> result += "Please try again later.";
            }
            result += "\n";
            return new EvalResult(result, MY_STATE);
        }
    }

    private EvalResult join(String[] params) {
        return new EvalResult("", ClientStates.QUIT);
    }

    private EvalResult observe(String[] params) {
        return new EvalResult("", ClientStates.QUIT);
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
