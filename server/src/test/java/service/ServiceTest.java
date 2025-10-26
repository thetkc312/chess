package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import passoff.model.TestAuthResult;

import javax.xml.crypto.Data;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTest {

    private static UserData userBob;
    private static UserData userBot;
    private static String gameName;
    private DataAccess dataAccess;
    private Service service;

    @BeforeAll
    public static void setup() {
        userBob = new UserData("Bob", "bob@bob.bob", "bigboybob");
        userBot = new UserData("Bot", "bot@bot.bot", "bigboybot");
    }

    @BeforeEach
    public void reset() {
        //gameData = new GameData(606, null, null, "BobBot", new ChessGame());
        gameName = "BobBot";
        dataAccess = new MemoryDataAccess();
        service = new Service(dataAccess);
    }

    @Test
    void registerValid() {

    }
    @Test
    void registerInvalid() {
        UserData user;
    }

    @Test
    void loginValid() {
        UserData user;
    }
    @Test
    void loginInvalid(UserData user)
            throws BadRequestException, InvalidCredentialsException, DataAccessException {
    }

    @Test
    void logoutValid(String authToken)
            throws InvalidCredentialsException, DataAccessException {
    }
    @Test
    void logoutInvalid(String authToken)
            throws InvalidCredentialsException, DataAccessException {
    }

    @Test
    void listValid(String authToken)
            throws InvalidCredentialsException, DataAccessException {
    }
    @Test
    void listInvalid(String authToken)
            throws InvalidCredentialsException, DataAccessException {
    }

    @Test
    void createValid(String authToken, String gameName)
            throws BadRequestException, InvalidCredentialsException, DataAccessException {
    }
    @Test
    void createInvalid(String authToken, String gameName)
            throws BadRequestException, InvalidCredentialsException, DataAccessException {
    }

    @Test
    void joinGameValid(String authToken, int gameID, ChessGame.TeamColor teamColor)
            throws BadRequestException, InvalidCredentialsException, AlreadyTakenException, DataAccessException {
    }
    @Test
    void joinGameInvalid(String authToken, int gameID, ChessGame.TeamColor teamColor)
            throws BadRequestException, InvalidCredentialsException, AlreadyTakenException, DataAccessException {
    }

    @Test
    void clearValid()
            throws DataAccessException {
        dataAccess.createUser(userBob);
        AuthData authData = dataAccess.createAuth(userBob.username());
        int gameID = dataAccess.createGame(gameName);
        service.clear();
        assertFalse(dataAccess.userExists(userBob.username()));
        assertFalse(dataAccess.authExists(authData.authToken()));
        assertFalse(dataAccess.gameExists(gameID));
    }

}
