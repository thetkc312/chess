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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class DataAccessTest {

    private static UserData userBob;
    private static UserData userBot;
    private static UserData userBad;
    private static String gameName;
    private DataAccess dataAccess;
    private GameData gameData;
    private static String assignedAuthToken;

    @BeforeAll
    public static void setup() {
        userBob = new UserData("Bob", "bigboybob", "bob@bob.bob");
        userBot = new UserData("Bot", "bigboybot", "bot@bot.bot");
        userBad = new UserData("Bad", "", "bad@bad.bad");
        gameName = "BobBot";
        assignedAuthToken = "assignedAuthToken";
    }


    @BeforeEach
    public void reset() throws DatabaseException {
        DatabaseManager.createDatabase();
        dropDatabase();
        dataAccess = new MySqlDataAccess();
        gameData = new GameData(1, null, null, "BobBot", new ChessGame());
    }

    @Test
    void clearValid() throws DatabaseException {
        addUserBob();
        addAuthBob();
        addGameBob();

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
        addUserBob();
        assertTrue(dataAccess.userExists(userBob.username()));
    }
    @Test
    void userExistsInvalid() throws DatabaseException {
        assertFalse(dataAccess.userExists(userBob.username()));
    }

    @Test
    void validLoginValid() throws DatabaseException {
        addUserBob();
        assertTrue(dataAccess.validLogin(userBob.username(), userBob.password()));
    }
    @Test
    void validLoginInvalid() throws DatabaseException {
        addUserBob();
        assertFalse(dataAccess.validLogin(userBob.username(), userBot.password()));
    }

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
    @Test
    void createAuthInvalid() throws DatabaseException {
        dropDatabase();
        assertThrows(DatabaseException.class, () -> dataAccess.createAuth(userBob.username()));
    }

    @Test
    void authExistsValid() throws DatabaseException {
        addAuthBob();
        assertTrue(dataAccess.authExists(assignedAuthToken));
    }
    @Test
    void authExistsInvalid() throws DatabaseException {
        assertFalse(dataAccess.authExists(assignedAuthToken));
    }

    @Test
    void getUserValid() throws DatabaseException {
        addAuthBob();
        assertEquals(userBob.username(), dataAccess.getUser(assignedAuthToken));
    }
    @Test
    void getUserInvalid() throws DatabaseException {
        addAuthBob();
        assertThrows(DatabaseException.class, () -> dataAccess.getUser("fakeAuthToken"));
    }

    @Test
    void logoutAuthValid() throws DatabaseException {
        addAuthBob();

        assertTrue(dataAccess.logoutAuth(assignedAuthToken));

        try (Connection connection = DatabaseManager.getConnection()) {
            String authExistsStatement =
                    """
                    SELECT * FROM auth_data WHERE authToken = ?
                    """;
            try (PreparedStatement preparedStatement = connection.prepareStatement(authExistsStatement)) {
                preparedStatement.setString(1, assignedAuthToken);
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    assertFalse(rs.next());
                }
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to check if user exists in dataaccess: %s", ex.getMessage()));
        }
    }
    @Test
    void logoutAuthInvalid() throws DatabaseException {
        addAuthBob();

        assertFalse(dataAccess.logoutAuth("fakeAuthToken"));

        try (Connection connection = DatabaseManager.getConnection()) {
            String authExistsStatement =
                    """
                    SELECT * FROM auth_data WHERE authToken = ?
                    """;
            try (PreparedStatement preparedStatement = connection.prepareStatement(authExistsStatement)) {
                preparedStatement.setString(1, assignedAuthToken);
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    assertTrue(rs.next());
                }
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to check if user exists in dataaccess: %s", ex.getMessage()));
        }
    }

    @Test
    void createGameValid() throws DatabaseException {
        int gameResultID = dataAccess.createGame(gameData.gameName());

        try (Connection connection = DatabaseManager.getConnection()) {
            String gameExistsStatement =
                    """
                    SELECT * FROM game_data WHERE gameID = ?
                    """;
            try (PreparedStatement preparedStatement = connection.prepareStatement(gameExistsStatement)) {
                preparedStatement.setInt(1, gameResultID);
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    assertTrue(rs.next());
                    assertFalse(rs.next());
                }
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to check if user exists in dataaccess: %s", ex.getMessage()));
        }
    }
    @Test
    void createGameInvalid() throws DatabaseException {
        addGameBob();

        assertThrows(DatabaseException.class, () -> dataAccess.createGame("fakeGameName"));

        try (Connection connection = DatabaseManager.getConnection()) {
            String gameExistsStatement =
                    """
                    SELECT * FROM game_data WHERE gameName = ?
                    """;
            try (PreparedStatement preparedStatement = connection.prepareStatement(gameExistsStatement)) {
                preparedStatement.setString(1, "fakeGameName");
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    assertFalse(rs.next());
                }
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to check if user exists in dataaccess: %s", ex.getMessage()));
        }
    }

    @Test
    void gameExistsValid() throws DatabaseException {
        addGameBob();
        assertTrue(dataAccess.gameExists(gameData.gameID()));
    }
    @Test
    void gameExistsInvalid() throws DatabaseException {
        assertFalse(dataAccess.gameExists(gameData.gameID()));
    }

    @Test
    void joinGameValid() throws DatabaseException {
        addGameBob();

        dataAccess.joinGame(userBob.username(), ChessGame.TeamColor.WHITE, gameData.gameID());
        dataAccess.joinGame(userBot.username(), ChessGame.TeamColor.BLACK, gameData.gameID());
        GameData newGameData = new GameData(gameData.gameID(), userBob.username(), userBot.username(), gameData.gameName(), gameData.game());

        try (Connection connection = DatabaseManager.getConnection()) {
            String gameExistsStatement =
                    """
                    SELECT * FROM game_data WHERE gameID = ? AND whiteUsername = ? AND blackUsername = ? AND gameName = ? AND game = ?
                    """;
            try (PreparedStatement preparedStatement = connection.prepareStatement(gameExistsStatement)) {
                preparedStatement.setInt(1, gameData.gameID());
                preparedStatement.setString(2, userBob.username());
                preparedStatement.setString(3, userBot.username());
                preparedStatement.setString(4, gameData.gameName());
                preparedStatement.setString(5, new Gson().toJson(newGameData));
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    assertTrue(rs.next());
                    assertFalse(rs.next());
                }
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to check if user exists in dataaccess: %s", ex.getMessage()));
        }

        dataAccess.joinGame(userBob.username(), ChessGame.TeamColor.BLACK, gameData.gameID());
        newGameData = new GameData(gameData.gameID(), userBob.username(), userBob.username(), gameData.gameName(), gameData.game());

        try (Connection connection = DatabaseManager.getConnection()) {
            String gameExistsStatement =
                    """
                    SELECT * FROM game_data WHERE gameID = ? AND whiteUsername = ? AND blackUsername = ? AND gameName = ? AND game = ?
                    """;
            try (PreparedStatement preparedStatement = connection.prepareStatement(gameExistsStatement)) {
                preparedStatement.setInt(1, gameData.gameID());
                preparedStatement.setString(2, userBob.username());
                preparedStatement.setString(3, userBob.username());
                preparedStatement.setString(4, gameData.gameName());
                preparedStatement.setString(5, new Gson().toJson(newGameData));
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    assertTrue(rs.next());
                    assertFalse(rs.next());
                }
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to check if user exists in dataaccess: %s", ex.getMessage()));
        }
    }
    @Test
    void joinGameInvalid() throws DatabaseException {
        assertThrows(DatabaseException.class, () -> dataAccess.joinGame(userBob.username(), ChessGame.TeamColor.WHITE, gameData.gameID()));

        addGameBob();

        assertThrows(DatabaseException.class, () -> dataAccess.joinGame(userBob.username(), ChessGame.TeamColor.WHITE, gameData.gameID()+1));
    }

    @Test
    void roleOpenValid() throws DatabaseException {
        addGameBobBlack();
        assertTrue(dataAccess.roleOpen(gameData.gameID(), ChessGame.TeamColor.WHITE));
    }
    @Test
    void roleOpenInvalid() throws DatabaseException {
        addGameBobBlack();
        assertFalse(dataAccess.roleOpen(gameData.gameID(), ChessGame.TeamColor.BLACK));
    }

    @Test
    void listGamesValid() throws DatabaseException {
        assertEquals(0, dataAccess.listGames().size());
        GameData newGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), userBob.username(), gameData.gameName(), gameData.game());

        try (Connection connection = DatabaseManager.getConnection()) {
            String gameAddStatement =
                    """
                    INSERT INTO game_data (gameID, blackUsername, gameName, game) VALUES (?, ?, ?, ?)
                    """;
            try (PreparedStatement preparedStatement = connection.prepareStatement(gameAddStatement)) {
                preparedStatement.setInt(1, newGameData.gameID());
                preparedStatement.setString(2, newGameData.blackUsername());
                preparedStatement.setString(3, newGameData.gameName());
                preparedStatement.setString(4, new Gson().toJson(newGameData));
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to create user in dataaccess: %s", ex.getMessage()));
        }
        ArrayList<GameData> listedGames = dataAccess.listGames();
        assertEquals(1, listedGames.size());
        assertEquals(gameData.gameID(), listedGames.getFirst().gameID());
        assertNull(listedGames.getFirst().whiteUsername());
        assertEquals(userBob.username(), listedGames.getFirst().blackUsername());
        assertEquals(gameData.gameName(), listedGames.getFirst().gameName());
        assertEquals(gameData.game(), listedGames.getFirst().game());
    }
    @Test
    void listGames() throws DatabaseException {
        dropDatabase();
        assertThrows(DatabaseException.class, () -> dataAccess.listGames());
    }

    private void addUserBob() throws DatabaseException {
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
    }

    private void addAuthBob() throws DatabaseException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String authAddStatement =
                    """
                    INSERT INTO auth_data (authToken, username) VALUES (?, ?)
                    """;
            try (PreparedStatement preparedStatement = connection.prepareStatement(authAddStatement)) {
                preparedStatement.setString(1, assignedAuthToken);
                preparedStatement.setString(2, userBob.username());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to create user in dataaccess: %s", ex.getMessage()));
        }
    }

    private void addGameBob() throws DatabaseException {
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
    }

    private void addGameBobBlack() throws DatabaseException {
        try (Connection connection = DatabaseManager.getConnection()) {
            String gameAddStatement =
                    """
                    INSERT INTO game_data (gameID, blackUsername, gameName, game) VALUES (?, ?, ?, ?)
                    """;
            try (PreparedStatement preparedStatement = connection.prepareStatement(gameAddStatement)) {
                preparedStatement.setInt(1, gameData.gameID());
                preparedStatement.setString(2, userBob.username());
                preparedStatement.setString(3, gameData.gameName());
                preparedStatement.setString(4, new Gson().toJson(gameData));
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DatabaseException(String.format("Unable to create user in dataaccess: %s", ex.getMessage()));
        }
    }

    private void dropDatabase() throws DatabaseException {
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
    }
}
