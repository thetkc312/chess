package server;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.net.http.HttpClient;
import java.util.ArrayList;

// Handles HTTP communication with the server, throws ResponseException exceptions based on response status codes
public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    // Take nothing, clear database, return nothing
    public void clear() throws ResponseException {

    }

    // Take complete UserData, register and log in the user, return AuthData
    public AuthData register(UserData userData) throws ResponseException {
        return null;
    }

    // Take UserData missing email, log in the user, return AuthData
    public AuthData login(UserData userData) throws ResponseException {
        return null;
    }

    // Take String for authToken, logout that active session, return nothing
    public void logout(String authToken) throws ResponseException {

    }

    // Take String for authToken, discover all available games, return ArrayList<GameData>
    public ArrayList<GameData> listGames(String authToken) throws ResponseException {
        return null;
    }

    // Take Strings for authToken and gameName, create a new game, return Integer for gameID
    public int createGame(String authToken, String gameName) throws ResponseException {
        return 0;
    }

    // Take String for authToken, a ChessGame.TeamColor for playerColor and int for gameID, add the player to the game, return nothing
    public void joinGame(String authToken, ChessGame.TeamColor playerColor, int gameID) throws ResponseException {

    }
}
