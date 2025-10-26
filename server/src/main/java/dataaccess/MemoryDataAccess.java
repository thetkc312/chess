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
    private final HashMap<Integer, GameData> gameMap = new HashMap<>(); // Hash map of format GameID: GameData
    private int gameID = 1;

    @Override
    public void clear() {
        userMap.clear();
        authMap.clear();
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
        return new AuthData(username, authToken);
    }

    @Override
    public boolean authExists(String authToken) {
        return authMap.containsKey(authToken);
    }

    @Override
    public String getUser(String authToken) {
        return authMap.get(authToken);
    }

    @Override
    public boolean logoutAuth(String authToken) {
        if (!authMap.containsKey(authToken)) {
            return false;
        }
        authMap.remove(authToken);
        return true;

    }

    private String generateAuthToken() {
        return UUID.randomUUID().toString();
    }

    @Override
    public int createGame(String gameName) {
        gameID += 1;
        gameMap.put(gameID, new GameData(gameID, null, null, gameName, new ChessGame()));
        return gameID;
    }

    @Override
    public boolean gameExists(int gameID) {
        return gameMap.containsKey(gameID);
    }

    @Override
    public boolean roleOpen(int gameID, ChessGame.TeamColor teamColor) {
        if (teamColor == ChessGame.TeamColor.BLACK) {
            return gameMap.get(gameID).blackUsername() == null;
        } else {
            return gameMap.get(gameID).whiteUsername() == null;
        }
    }

    @Override
    public void joinGame(String username, ChessGame.TeamColor teamColor, int gameID) {
        String whiteUsername = gameMap.get(gameID).whiteUsername();
        String blackUsername = gameMap.get(gameID).blackUsername();
        if (teamColor == ChessGame.TeamColor.BLACK) {
            blackUsername = username;
        } else {
            whiteUsername = username;
        }
        String oldGameName = gameMap.get(gameID).gameName();
        ChessGame oldGame = gameMap.get(gameID).game();
        GameData newGameData = new GameData(gameID, whiteUsername, blackUsername, oldGameName, oldGame);
        gameMap.put(gameID, newGameData);
    }

    @Override
    public ArrayList<GameData> listGames() {
        return new ArrayList<>(gameMap.values());
    }

    // TODO: Start by listing game related methods and declaring the types of errors they throw
}
