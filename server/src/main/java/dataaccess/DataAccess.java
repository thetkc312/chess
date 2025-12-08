package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;

public interface DataAccess {
    void clear() throws DatabaseException;

    boolean createUser(UserData user) throws DatabaseException; // True for success, False for failure

    boolean userExists(String username) throws DatabaseException;

    boolean validLogin(String username, String password) throws DatabaseException;

    AuthData createAuth(String username) throws DatabaseException;

    boolean authExists(String authToken) throws DatabaseException;

    String getUser(String authToken) throws DatabaseException;

    boolean logoutAuth(String authToken) throws DatabaseException;

    int createGame(String gameName) throws DatabaseException; // Response includes the gameID

    boolean gameExists(int gameID) throws DatabaseException;

    void joinGame(int gameID, String username, ChessGame.TeamColor teamColor) throws DatabaseException; // Response is empty

    boolean roleOpen(int gameID, ChessGame.TeamColor teamColor) throws DatabaseException;

    void executeChessMove(int gameID, ChessMove chessMove) throws DatabaseException;

    void endGame(int gameID) throws DatabaseException;

    void removeUser(int gameID, String username, ChessGame.TeamColor teamColor) throws DatabaseException;

    ArrayList<GameData> listGames() throws DatabaseException;

}
