package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MySqlDataAccess implements DataAccess {

    private String databaseName;

    public MySqlDataAccess() throws DataAccessException {
        initializeDatabase();
    }

    @Override
    public void clear() throws DatabaseException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String statement =
                    """
                    DROP DATABASE %s
                    """.formatted(databaseName);
            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
                initializeDatabase();
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to clear database: %s", ex.getMessage()));
        }
    }

    @Override
    public boolean createUser(UserData user) throws DatabaseException {
        if (this.userExists(user.username())) {
            return false;
        }
        try (Connection connection = DatabaseManager.getConnection()) {
            String userAddStatement =
                    """
                    INSERT INTO user_data (username, password, email) VALUES (?, ?, ?)
                    """;
            try (PreparedStatement preparedStatement = connection.prepareStatement(userAddStatement)) {
                preparedStatement.setString(1, user.username());
                preparedStatement.setString(2, user.email());
                preparedStatement.setString(3, user.password());
                preparedStatement.executeUpdate();
                return true;
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to create user in database: %s", ex.getMessage()));
        }
    }

    @Override
    public boolean userExists(String username) throws DatabaseException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String userExistsStatement =
                    """
                    SELECT * FROM user_data WHERE username = ?
                    """;
            try (PreparedStatement preparedStatement = connection.prepareStatement(userExistsStatement)) {
                preparedStatement.setString(1, username);
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        return true;
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to check if user exists in database: %s", ex.getMessage()));
        }
        return false;
    }

    @Override
    public boolean validLogin(String username, String password) throws DatabaseException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String statement = "";
            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to check for valid login info in database: %s", ex.getMessage()));
        }
        return false;
    }

    @Override
    public AuthData createAuth(String username) throws DatabaseException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String statement = "";
            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to add authentication entry to database: %s", ex.getMessage()));
        }
        return null;
    }

    @Override
    public boolean authExists(String authToken) throws DatabaseException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String statement = "";
            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to check if authentication token database: %s", ex.getMessage()));
        }
        return false;
    }

    @Override
    public String getUser(String authToken) throws DatabaseException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String statement = "";
            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to check if user is in database: %s", ex.getMessage()));
        }
        return "";
    }

    @Override
    public boolean logoutAuth(String authToken) throws DatabaseException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String statement = "";
            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to log user out of database: %s", ex.getMessage()));
        }
        return false;
    }

    @Override
    public int createGame(String gameName) throws DatabaseException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String statement = "";
            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to add game to database: %s", ex.getMessage()));
        }
        return 0;
    }

    @Override
    public boolean gameExists(int gameID) throws DatabaseException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String statement = "";
            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to check if game exists in database: %s", ex.getMessage()));
        }
        return false;
    }

    @Override
    public void joinGame(String username, ChessGame.TeamColor teamColor, int gameID) throws DatabaseException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String statement = "";
            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to join user to game in database: %s", ex.getMessage()));
        }

    }

    @Override
    public boolean roleOpen(int gameID, ChessGame.TeamColor teamColor) throws DatabaseException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String statement = "";
            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to check if role is open in database: %s", ex.getMessage()));
        }
        return false;
    }

    @Override
    public ArrayList<GameData> listGames() throws DatabaseException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String statement = "";
            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to list games in database: %s", ex.getMessage()));
        }
        return null;
    }

    private void initializeDatabase() throws DatabaseException {
        DatabaseManager.createDatabase();
        databaseName = DatabaseManager.getDatabaseName();
        configureTable(userTableTemplate);
        configureTable(gameTableTemplate);
        configureTable(authTableTemplate);
    }

    private void configureTable(String[] dataTableTemplate) throws DatabaseException {
        try (Connection connection = DatabaseManager.getConnection()) {
            for (String statement : dataTableTemplate) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to configure database: %s", ex.getMessage()));
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
