package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class MySqlDataAccess implements DataAccess {

    private String databaseName;
    private int gameID;

    public MySqlDataAccess() throws DataAccessException {
        initializeDatabase();
        gameID = 0;
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
                preparedStatement.setString(2, user.password());
                preparedStatement.setString(3, user.email());
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
                    return rs.next();
                }
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to check if user exists in database: %s", ex.getMessage()));
        }
    }

    @Override
    public boolean validLogin(String username, String password) throws DatabaseException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String userExistsStatement =
                    """
                    SELECT * FROM user_data WHERE username = ? AND password = ?
                    """;
            try (PreparedStatement preparedStatement = connection.prepareStatement(userExistsStatement)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to check for valid login info in database: %s", ex.getMessage()));
        }
    }

    @Override
    public AuthData createAuth(String username) throws DatabaseException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String authAddStatement =
                    """
                    INSERT INTO auth_data (authToken, username) VALUES (?, ?)
                    """;
            try (PreparedStatement preparedStatement = connection.prepareStatement(authAddStatement)) {
                AuthData authData = new AuthData(username, generateAuthToken());
                preparedStatement.setString(1, authData.authToken());
                preparedStatement.setString(2, authData.username());
                preparedStatement.executeUpdate();
                return authData;
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to add authentication entry to database: %s", ex.getMessage()));
        }
    }

    private String generateAuthToken() {
        return UUID.randomUUID().toString();
    }


    @Override
    public boolean authExists(String authToken) throws DatabaseException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String authExistsStatement =
                    """
                    SELECT * FROM auth_data WHERE authToken = ?
                    """;
            try (PreparedStatement preparedStatement = connection.prepareStatement(authExistsStatement)) {
                preparedStatement.setString(1, authToken);
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        return true;
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to check if authentication token exists in database: %s", ex.getMessage()));
        }
        return false;
    }

    @Override
    public String getUser(String authToken) throws DatabaseException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String userGetStatement =
                    """
                    SELECT username FROM auth_data WHERE authToken = ?
                    """;
            try (PreparedStatement preparedStatement = connection.prepareStatement(userGetStatement)) {
                preparedStatement.setString(1, authToken);
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    rs.next();
                    return rs.getString("username");
                }
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to check if user is in database: %s", ex.getMessage()));
        }
    }

    @Override
    public boolean logoutAuth(String authToken) throws DatabaseException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String authDeleteStatement =
                    """
                    DELETE FROM auth_data WHERE authToken = ?
                    """;
            try (PreparedStatement preparedStatement = connection.prepareStatement(authDeleteStatement)) {
                preparedStatement.setString(1, authToken);
                int rowsAffected = preparedStatement.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to log user out of database: %s", ex.getMessage()));
        }
    }

    @Override
    public int createGame(String gameName) throws DatabaseException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String gameAddStatement =
                    """
                    INSERT INTO game_data (gameID, gameName, game) VALUES (?, ?, ?)
                    """;
            try (PreparedStatement preparedStatement = connection.prepareStatement(gameAddStatement)) {
                gameID += 1;
                GameData gameData = new GameData(gameID, null, null, gameName, new ChessGame());
                preparedStatement.setInt(1, gameID);
                preparedStatement.setString(2, gameData.gameName());
                preparedStatement.setString(3, new Gson().toJson(gameData));
                preparedStatement.executeUpdate();
                return gameID;
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to add game to database: %s", ex.getMessage()));
        }
    }

    @Override
    public boolean gameExists(int gameID) throws DatabaseException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String gameExistsStatement =
                    """
                    SELECT * FROM game_data WHERE gameID = ?
                    """;
            try (PreparedStatement preparedStatement = connection.prepareStatement(gameExistsStatement)) {
                preparedStatement.setInt(1, gameID);
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        return true;
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to check if game exists in database: %s", ex.getMessage()));
        }
        return false;
    }

    @Override
    public void joinGame(String username, ChessGame.TeamColor teamColor, int gameID) throws DatabaseException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String gameFindStatement =
                    """
                    SELECT game FROM game_data WHERE gameID = ?
                    """;
            GameData gameData;
            try (PreparedStatement preparedStatement = connection.prepareStatement(gameFindStatement)) {
                preparedStatement.setInt(1, gameID);
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    rs.next();
                    String gameJson = rs.getString("game");
                    gameData = new Gson().fromJson(gameJson, GameData.class);
                }
            }

            String colorUser;
            GameData updatedGameData;
            if (teamColor == ChessGame.TeamColor.WHITE) {
                colorUser = "whiteUsername";
                updatedGameData = new GameData(gameData.gameID(), username, gameData.blackUsername(), gameData.gameName(), gameData.game());
            } else {
                colorUser = "blackUsername";
                updatedGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), username, gameData.gameName(), gameData.game());
            }


            String gameUpdateStatement =
                    """
                    UPDATE game_data SET %s = ?, game = ? WHERE gameID = ?
                    """.formatted(colorUser);
            try (PreparedStatement preparedStatement = connection.prepareStatement(gameUpdateStatement)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, new Gson().toJson(updatedGameData));
                preparedStatement.setInt(3, gameID);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to join user to game in database: %s", ex.getMessage()));
        }

    }

    @Override
    public boolean roleOpen(int gameID, ChessGame.TeamColor teamColor) throws DatabaseException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String colorUser;
            if (teamColor == ChessGame.TeamColor.WHITE) {
                colorUser = "whiteUsername";
            } else {
                colorUser = "blackUsername";
            }
            String roleOpenStatement =
                    """
                    SELECT %s FROM game_data WHERE gameID = ?
                    """.formatted(colorUser);
            try (PreparedStatement preparedStatement = connection.prepareStatement(roleOpenStatement)) {
                preparedStatement.setInt(1, gameID);
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    rs.next();
                    rs.getString(colorUser);
                    return rs.wasNull();
                }
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to check if role is open in database: %s", ex.getMessage()));
        }
    }

    @Override
    public ArrayList<GameData> listGames() throws DatabaseException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String gameListStatement =
                    """
                    SELECT game FROM game_data
                    """;
            try (PreparedStatement preparedStatement = connection.prepareStatement(gameListStatement)) {
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    ArrayList<GameData> gameList = new ArrayList<>();
                    while (rs.next()) {
                        String gameJson = rs.getString("game");
                        gameList.add(new Gson().fromJson(gameJson, GameData.class));
                    }
                    return gameList;
                }
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to list games in database: %s", ex.getMessage()));
        }
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
              `gameID` int NOT NULL,
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
