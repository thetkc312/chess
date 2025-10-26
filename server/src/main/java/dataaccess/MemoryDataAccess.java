package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class MemoryDataAccess implements DataAccess {

    private final HashMap<String, UserData> userMap = new HashMap<>();
    private final HashMap<String, String> authMap = new HashMap<>(); // Hash map of format AuthToken: Username
    private final HashMap<String, String> authUserMap = new HashMap<>(); // Hash map of format Username: AuthToken
    private final HashMap<Integer, GameData> gameMap = new HashMap<>(); // Hash map of format GameID: GameData
    private int gameID = 0;

    @Override
    public void clear() {
        userMap.clear();
        authMap.clear();
        authUserMap.clear();
        gameMap.clear();
    }

    @Override
    public boolean createUser(UserData user) {
        if (userExists(user.username())) {
            return false;
        }
        userMap.put(user.username(), user);
        return true;
    }

    @Override
    public boolean userExists(String username) {
        return userMap.containsKey(username);
    }

    @Override
    public boolean validLogin(String username, String password) {
        if (!userMap.containsKey(username)) {
            return false;
        }
        return userMap.get(username).password().equals(password);
    }

    @Override
    public AuthData createAuth(String username) {
        String authToken = generateAuthToken();
        authMap.put(authToken, username);
        authUserMap.put(username, authToken);
        return new AuthData(username, authToken);
    }

    @Override
    public String getUserAuth(String username) {
        return authUserMap.get(username);
    }

    @Override
    public boolean hasAuthToken(String username) {
        return authUserMap.containsKey(username);
    }

    @Override
    public boolean validAuth(String authToken) {
        return authMap.containsKey(authToken);
    }

    @Override
    public boolean logoutAuth(String authToken) {
        if (!authMap.containsKey(authToken)) {
            return false;
        }
        authMap.remove(authToken);
        String username = authMap.get(authToken);
        authUserMap.remove(username);
        return true;

    }

    private String generateAuthToken() {
        // TODO: Implement actual authToken generation
        return UUID.randomUUID().toString();
    }

    @Override
    public int createGame(String gameName) {
        gameID += 1;
        gameMap.put(gameID, new GameData(gameID, "", "", gameName, new ChessGame()));
        return gameID;
    }

    @Override
    public boolean joinGame(ChessGame.TeamColor teamColor, int gameID) {
        return false;
    }

    @Override
    public ArrayList<GameData> listGames() {
        return new ArrayList<>(gameMap.values());
    }

    // TODO: Start by listing game related methods and declaring the types of errors they throw
}
