package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import service.BadRequestException;
import service.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DataAccessTest {

    private static UserData userBob;
    private static UserData userBot;
    private static UserData userBad;
    private static String gameName;
    private DataAccess dataAccess;
    private GameData gameData;

    @BeforeAll
    public static void setup() {
        userBob = new UserData("Bob", "bob@bob.bob", "bigboybob");
        userBot = new UserData("Bot", "bot@bot.bot", "bigboybot");
        userBad = new UserData("Bad", "bad@bad.bad", "");
        gameName = "BobBot";
    }


    @BeforeEach
    public void reset() throws DatabaseException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String statement =
                    """
                    DROP DATABASE %s
                    """.formatted(DatabaseManager.getDatabaseName());
            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to clear dataaccess: %s", ex.getMessage()));
        }
        dataAccess = new MySqlDataAccess();
        gameData = new GameData(1, null, null, "BobBot", new ChessGame());
    }

    @Test
    void clearValid() throws DatabaseException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String userAddStatement =
                    """
                    INSERT INTO user_data (username, password, email) VALUES (?, ?, ?)
                    """;
            try (PreparedStatement preparedStatement = connection.prepareStatement(userAddStatement)) {
                preparedStatement.setString(1, userBob.username());
                preparedStatement.setString(2, BCrypt.hashpw(userBob.password(), BCrypt.gensalt()));
                preparedStatement.setString(3, userBob.email());
                preparedStatement.executeUpdate();
            }
            String authAddStatement =
                    """
                    INSERT INTO auth_data (authToken, username) VALUES (?, ?)
                    """;
            try (PreparedStatement preparedStatement = connection.prepareStatement(authAddStatement)) {
                preparedStatement.setString(1, "randomAuthToken");
                preparedStatement.setString(2, userBob.username());
                preparedStatement.executeUpdate();
            }
            String gameAddStatement =
                    """
                    INSERT INTO game_data (gameID, gameName, game) VALUES (?, ?, ?)
                    """;
            try (PreparedStatement preparedStatement = connection.prepareStatement(gameAddStatement)) {
                preparedStatement.setInt(1, gameData.gameID());
                preparedStatement.setString(2, gameData.gameName());
                preparedStatement.setString(3, new Gson().toJson(gameData));
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to create user in dataaccess: %s", ex.getMessage()));
        }

        dataAccess.clear();

        try (Connection connection = DatabaseManager.getConnection()) {
            String userExistsStatement =
                    """
                    SELECT * FROM user_data
                    """;
            try (PreparedStatement preparedStatement = connection.prepareStatement(userExistsStatement)) {
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    assertFalse(rs.next());
                }
            }
            String authExistsStatement =
                    """
                    SELECT * FROM auth_data
                    """;
            try (PreparedStatement preparedStatement = connection.prepareStatement(authExistsStatement)) {
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    assertFalse(rs.next());
                }
            }
            String gameExistsStatement =
                    """
                    SELECT * FROM game_data
                    """;
            try (PreparedStatement preparedStatement = connection.prepareStatement(gameExistsStatement)) {
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    assertFalse(rs.next());
                }
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to check if user exists in dataaccess: %s", ex.getMessage()));
        }
    }

    @Test
    void createUserValid() throws DatabaseException {
        assertTrue(dataAccess.createUser(userBob));

        try (Connection connection = DatabaseManager.getConnection()) {
            String userExistsStatement =
                    """
                    SELECT * FROM user_data WHERE username = ? AND email = ?
                    """;
            try (PreparedStatement preparedStatement = connection.prepareStatement(userExistsStatement)) {
                preparedStatement.setString(1, userBob.username());
                preparedStatement.setString(2, userBob.email());
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    assertTrue(rs.next());
                    assertTrue(BCrypt.checkpw(userBob.password(), rs.getString("password")));
                }
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to check if user exists in dataaccess: %s", ex.getMessage()));
        }
    }
    @Test
    void createUserInvalid() throws DatabaseException {
        dataAccess.createUser(userBob);
        assertFalse(dataAccess.createUser(userBob));
    }

    @Test
    void userExistsValid() throws DatabaseException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String userAddStatement =
                    """
                    INSERT INTO user_data (username, password, email) VALUES (?, ?, ?)
                    """;
            try (PreparedStatement preparedStatement = connection.prepareStatement(userAddStatement)) {
                preparedStatement.setString(1, userBob.username());
                preparedStatement.setString(2, BCrypt.hashpw(userBob.password(), BCrypt.gensalt()));
                preparedStatement.setString(3, userBob.email());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to create user in dataaccess: %s", ex.getMessage()));
        }
        assertTrue(dataAccess.userExists(userBob.username()));
    }
    @Test
    void userExistsInvalid() throws DatabaseException {
        assertFalse(dataAccess.userExists(userBob.username()));
    }

//    @Test
//    void validLoginValid() throws DatabaseException {
//    }
//    @Test
//    void validLoginInvalid() throws DatabaseException {
//    }

    @Test
    void createAuthValid() throws DatabaseException {
        AuthData authDataResult = dataAccess.createAuth(userBob.username());
        assertNotNull(authDataResult);

        try (Connection connection = DatabaseManager.getConnection()) {
            String userExistsStatement =
                    """
                    SELECT * FROM auth_data WHERE authToken = ? AND username = ?
                    """;
            try (PreparedStatement preparedStatement = connection.prepareStatement(userExistsStatement)) {
                preparedStatement.setString(1, authDataResult.authToken());
                preparedStatement.setString(2, userBob.username());
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    assertTrue(rs.next());
                }
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to check if user exists in dataaccess: %s", ex.getMessage()));
        }
    }
//    @Test
//    void createAuthInvalid() throws DatabaseException {
//    }

    @Test
    void authExistsValid() throws DatabaseException {

        try (Connection connection = DatabaseManager.getConnection()) {
            String authAddStatement =
                    """
                    INSERT INTO auth_data (authToken, username) VALUES (?, ?)
                    """;
            try (PreparedStatement preparedStatement = connection.prepareStatement(authAddStatement)) {
                preparedStatement.setString(1, "randomAuthToken");
                preparedStatement.setString(2, userBob.username());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to create user in dataaccess: %s", ex.getMessage()));
        }
        assertTrue(dataAccess.authExists("randomAuthToken"));
    }
    @Test
    void authExistsInvalid() throws DatabaseException {
        assertFalse(dataAccess.authExists("randomAuthToken"));
    }

//    @Test
//    void getUserValid() throws DatabaseException {
//    }
//    @Test
//    void getUserInvalid() throws DatabaseException {
//    }
//
//    @Test
//    void logoutAuthValid() throws DatabaseException {
//    }
//    @Test
//    void logoutAuthInvalid() throws DatabaseException {
//    }
//
//    @Test
//    void createGameValid() throws DatabaseException {
//    }
//    @Test
//    void createGameInvalid() throws DatabaseException {
//    }

    @Test
    void gameExistsValid() throws DatabaseException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String gameAddStatement =
                    """
                    INSERT INTO game_data (gameID, gameName, game) VALUES (?, ?, ?)
                    """;
            try (PreparedStatement preparedStatement = connection.prepareStatement(gameAddStatement)) {
                preparedStatement.setInt(1, gameData.gameID());
                preparedStatement.setString(2, gameData.gameName());
                preparedStatement.setString(3, new Gson().toJson(gameData));
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to create user in dataaccess: %s", ex.getMessage()));
        }
        assertTrue(dataAccess.gameExists(gameData.gameID()));
    }
    @Test
    void gameExistsInvalid() throws DatabaseException {
        assertFalse(dataAccess.gameExists(gameData.gameID()));
    }

//    @Test
//    void joinGameValid() throws DatabaseException {
//    }
//    @Test
//    void joinGameInvalid() throws DatabaseException {
//    }
//
//    @Test
//    void roleOpenValid() throws DatabaseException {
//    }
//    @Test
//    void roleOpenInvalid() throws DatabaseException {
//    }
//
//    @Test
//    void listGamesValid() throws DatabaseException {
//    }
//    @Test
//    void listGames() throws DatabaseException {
//    }
}
