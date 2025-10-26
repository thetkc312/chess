package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;

public interface DataAccess {
    void clear();

    boolean createUser(UserData user); // True for success, False for failure
    boolean userExists(String username);
    boolean validLogin(String username, String password);

    AuthData createAuth(String username);
    boolean validAuth(String authToken);
    boolean logoutAuth(String authToken);

    int createGame(String gameName); // Response includes the gameID
    boolean joinGame(ChessGame.TeamColor teamColor, int gameID); // Response is empty
    ArrayList<GameData> listGames();

}
