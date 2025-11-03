package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class MySqlDataAccess implements DataAccess {

    private String databaseName;

    public MySqlDataAccess() throws DataAccessException {
        initializeDatabase();
    }

    @Override
    public void clear() {
        // TODO: Make Service handle results from DataAccess interface to know when to throw a DatabaseException
        // TODO: Integrate DatabaseException catching to throw 500 error codes in Server.java
    }

    @Override
    public boolean createUser(UserData user) {
        return false;
    }

    @Override
    public boolean userExists(String username) {
        return false;
    }

    @Override
    public boolean validLogin(String username, String password) {
        return false;
    }

    @Override
    public AuthData createAuth(String username) {
        return null;
    }

    @Override
    public boolean authExists(String authToken) {
        return false;
    }

    @Override
    public String getUser(String authToken) {
        return "";
    }

    @Override
    public boolean logoutAuth(String authToken) {
        return false;
    }

    @Override
    public int createGame(String gameName) {
        return 0;
    }

    @Override
    public boolean gameExists(int gameID) {
        return false;
    }

    @Override
    public void joinGame(String username, ChessGame.TeamColor teamColor, int gameID) {

    }

    @Override
    public boolean roleOpen(int gameID, ChessGame.TeamColor teamColor) {
        return false;
    }

    @Override
    public ArrayList<GameData> listGames() {
        return null;
    }

    private void initializeDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        databaseName = DatabaseManager.getDatabaseName();
        configureTable(userTableTemplate);
        configureTable(gameTableTemplate);
        configureTable(authTableTemplate);
    }

    private void configureTable(String[] dataTableTemplate) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection()) {
            for (String statement : dataTableTemplate) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            // TODO: Consider implementing a unique DataAccessException subclass for Database issues corresponding to the 500 error code, like the InvalidCredentialsException for the 401 code.
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    private final String[] userTableTemplate = {
            """
            CREATE TABLE IF NOT EXISTS  user_data (
              `username` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs
            """
    };

    private final String[] gameTableTemplate = {
            """
            CREATE TABLE IF NOT EXISTS  game_data (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `gameName` varchar(256) NOT NULL,
              `game` TEXT NOT NULL,
              PRIMARY KEY (`gameID`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs
            """
    };

    private final String[] authTableTemplate = {
            """
            CREATE TABLE IF NOT EXISTS  auth_data (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_as_cs
            """
    };
}
