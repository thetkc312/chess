package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.EndpointBodies.JoinBody;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTest {

    private static UserData userBob;
    private static UserData userBot;
    private static UserData userBad;
    private static String gameName;
    private DataAccess dataAccess;
    private Service service;

    @BeforeAll
    public static void setup() {
        userBob = new UserData("Bob", "bob@bob.bob", "bigboybob");
        userBot = new UserData("Bot", "bot@bot.bot", "bigboybot");
        userBad = new UserData("Bad", "bad@bad.bad", "");
        gameName = "BobBot";
    }

    @BeforeEach
    public void reset() {
        //gameData = new GameData(606, null, null, "BobBot", new ChessGame());
        dataAccess = new MemoryDataAccess();
        service = new Service(dataAccess);
    }

    @Test
    void registerValid() throws BadRequestException, AlreadyTakenException, DataAccessException {
        AuthData authDataBob = service.register(userBob);
        assertNotNull(authDataBob);
        assertTrue(dataAccess.userExists(userBob.username()));
        assertEquals(userBob.username(), authDataBob.username());
        assertTrue(dataAccess.authExists(authDataBob.authToken()));

        AuthData authDataBot = service.register(userBot);
        assertNotNull(authDataBot);
        assertTrue(dataAccess.userExists(userBot.username()));
        assertEquals(userBot.username(), authDataBot.username());
        assertTrue(dataAccess.authExists(authDataBot.authToken()));

        assertNotEquals(authDataBob.username(), authDataBot.username());
        assertNotEquals(authDataBob.authToken(), authDataBot.authToken());
    }
    @Test
    void registerInvalid() throws BadRequestException, AlreadyTakenException, DataAccessException {
        assertThrows(BadRequestException.class, () -> service.register(userBad));
        assertFalse(dataAccess.userExists(userBad.username()));
        service.register(userBob);
        assertThrows(AlreadyTakenException.class, () -> service.register(userBob));
    }

    @Test
    void loginValid() throws BadRequestException, InvalidCredentialsException, DataAccessException {
        dataAccess.createUser(userBob);
        AuthData authData = service.login(userBob);
        assertNotNull(authData);
        assertNotNull(authData.username());
        assertNotNull(authData.authToken());
        assertEquals(userBob.username(), authData.username());
        assertTrue(dataAccess.userExists(userBob.username()));
        assertTrue(dataAccess.authExists(authData.authToken()));
        assertTrue(dataAccess.validLogin(userBob.username(), userBob.password()));

        AuthData authData1 = service.login(userBob);
        assertNotNull(authData1);
        assertNotNull(authData1.username());
        assertNotNull(authData1.authToken());
        assertEquals(userBob.username(), authData1.username());
        assertTrue(dataAccess.authExists(authData1.authToken()));

        assertNotEquals(authData.authToken(), authData1.authToken());
        assertEquals(authData.username(), authData1.username());

    }
    @Test
    void loginInvalid() throws BadRequestException, InvalidCredentialsException, DataAccessException {
        assertThrows(BadRequestException.class, () -> service.login(userBad));
        dataAccess.createUser(userBob);
        UserData fakeUser = new UserData(userBob.username(), userBob.email(), "Wrong");
        assertThrows(InvalidCredentialsException.class, () -> service.login(fakeUser));
    }

    @Test
    void logoutValid() throws InvalidCredentialsException, DataAccessException {
        dataAccess.createUser(userBob);
        AuthData authData = dataAccess.createAuth(userBob.username());
        dataAccess.createUser(userBot);
        AuthData authData1 = dataAccess.createAuth(userBot.username());
        service.logout(authData.authToken());
        assertTrue(dataAccess.userExists(userBob.username()));
        assertFalse(dataAccess.authExists(authData.authToken()));
        assertTrue(dataAccess.authExists(authData1.authToken()));
    }
    @Test
    void logoutInvalid() throws InvalidCredentialsException, DataAccessException {
        dataAccess.createUser(userBob);
        AuthData authData = dataAccess.createAuth(userBob.username());

        assertThrows(InvalidCredentialsException.class, () -> service.logout("Wrong"));
        assertTrue(dataAccess.userExists(userBob.username()));
        assertTrue(dataAccess.authExists(authData.authToken()));
    }

    @Test
    void listValid() throws InvalidCredentialsException, DataAccessException {
        dataAccess.createUser(userBob);
        AuthData bobAuth = dataAccess.createAuth(userBob.username());
        assertEquals(0, service.list(bobAuth.authToken()).size());

        int gameID = dataAccess.createGame(userBob.username());
        ArrayList<GameData> listResponse = service.list(bobAuth.authToken());
        assertEquals(1, listResponse.size());
        assertEquals(gameID, listResponse.getFirst().gameID());
    }
    @Test
    void listInvalid() throws InvalidCredentialsException, DataAccessException {
        dataAccess.createUser(userBob);
        AuthData authData = dataAccess.createAuth(userBob.username());

        assertThrows(InvalidCredentialsException.class, () -> service.list("Wrong"));

    }

    @Test
    void createValid() throws BadRequestException, InvalidCredentialsException, DataAccessException {
        dataAccess.createUser(userBob);
        AuthData authData = dataAccess.createAuth(userBob.username());
        int gameID = service.create(authData.authToken(), userBob.username().concat("Game"));
        assertTrue(dataAccess.gameExists(gameID));
    }
    @Test
    void createInvalid() throws BadRequestException, InvalidCredentialsException, DataAccessException {
        dataAccess.createUser(userBob);
        AuthData authData = dataAccess.createAuth(userBob.username());
        assertThrows(BadRequestException.class, () -> service.create(authData.authToken(), ""));
        assertThrows(InvalidCredentialsException.class, () -> service.create("Wrong", userBob.username().concat("Game")));
        assertEquals(0, dataAccess.listGames().size());
    }

    @Test
    void joinValid() throws BadRequestException, InvalidCredentialsException, AlreadyTakenException, DataAccessException {
        dataAccess.createUser(userBob);
        AuthData authData = dataAccess.createAuth(userBob.username());
        int gameID = dataAccess.createGame(userBob.username().concat("Game"));
        service.join(authData.authToken(), new JoinBody(ChessGame.TeamColor.WHITE, gameID));
        assertEquals(userBob.username(), dataAccess.listGames().getFirst().whiteUsername());
        assertNull(dataAccess.listGames().getFirst().blackUsername());
        service.join(authData.authToken(), new JoinBody(ChessGame.TeamColor.BLACK, gameID));
        assertEquals(userBob.username(), dataAccess.listGames().getFirst().blackUsername());
    }
    @Test
    void joinInvalid() throws BadRequestException, InvalidCredentialsException, AlreadyTakenException, DataAccessException {
        dataAccess.createUser(userBob);
        AuthData authData = dataAccess.createAuth(userBob.username());
        int gameID = dataAccess.createGame(userBob.username().concat("Game"));
        assertThrows(BadRequestException.class, () -> service.join(authData.authToken(), new JoinBody(ChessGame.TeamColor.WHITE, 0)));
        assertThrows(BadRequestException.class, () -> service.join(authData.authToken(), new JoinBody(null, gameID)));
        assertTrue(dataAccess.roleOpen(gameID, ChessGame.TeamColor.WHITE));
        assertThrows(InvalidCredentialsException.class, () -> service.join("Wrong", new JoinBody(ChessGame.TeamColor.WHITE, gameID)));
        assertTrue(dataAccess.roleOpen(gameID, ChessGame.TeamColor.WHITE));
    }

    @Test
    void clearValid() throws DataAccessException {
        dataAccess.createUser(userBob);
        AuthData authData = dataAccess.createAuth(userBob.username());
        int gameID = dataAccess.createGame(gameName);
        service.clear();
        assertFalse(dataAccess.userExists(userBob.username()));
        assertFalse(dataAccess.authExists(authData.authToken()));
        assertFalse(dataAccess.gameExists(gameID));
    }

}
