package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class DataAccessMemory implements DataAccess {

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
        gameMap.put(gameID, new GameData(gameID, null, null, gameName, new ChessGame(), true));
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
    public void executeChessMove(int gameID, ChessMove chessMove) throws DatabaseException {
        ChessGame chessGame = gameMap.get(gameID).game();
        try {
            chessGame.makeMove(chessMove);
        } catch (InvalidMoveException e) {
            throw new DatabaseException("This is an invalid move and could not be made.", e);
        }
    }

    @Override
    public void endGame(int gameID) throws DatabaseException {
        GameData oldGame = gameMap.get(gameID);
        GameData newGameData = new GameData(gameID, oldGame.whiteUsername(), oldGame.blackUsername(), oldGame.gameName(), oldGame.game(), false);
        gameMap.put(gameID, newGameData);
    }

    @Override
    public void removeUser(int gameID, String username, ChessGame.TeamColor teamColor) throws DatabaseException {
        String whiteUsername = gameMap.get(gameID).whiteUsername();
        String blackUsername = gameMap.get(gameID).blackUsername();
        if (teamColor == ChessGame.TeamColor.BLACK) {
            if (username.equals(blackUsername)) {
                blackUsername = null;
            } else {
                throw new DatabaseException(String.format("Failed removing %s from game #%d: Does not match Black username %s", username, gameID, blackUsername));
            }
        } else {
            if (username.equals(whiteUsername)) {
                whiteUsername = null;
            } else {
                throw new DatabaseException(String.format("Failed removing %s from game #%d: Does not match Black username %s", username, gameID, blackUsername));
            }
        }

        ChessGame oldGame = gameMap.get(gameID).game();
        GameData newGameData = new GameData(gameID, whiteUsername, blackUsername, gameMap.get(gameID).gameName(), oldGame, true);
        gameMap.put(gameID, newGameData);
    }

    @Override
    public void joinGame(int gameID, String username, ChessGame.TeamColor teamColor) {
        String whiteUsername = gameMap.get(gameID).whiteUsername();
        String blackUsername = gameMap.get(gameID).blackUsername();
        if (teamColor == ChessGame.TeamColor.BLACK) {
            blackUsername = username;
        } else {
            whiteUsername = username;
        }
        String oldGameName = gameMap.get(gameID).gameName();
        ChessGame oldGame = gameMap.get(gameID).game();
        GameData newGameData = new GameData(gameID, whiteUsername, blackUsername, oldGameName, oldGame, true);
        gameMap.put(gameID, newGameData);
    }

    @Override
    public ArrayList<GameData> listGames() {
        return new ArrayList<>(gameMap.values());
    }
}
