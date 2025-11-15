package client;

import chess.ChessGame;
import endpointrequests.CreateGameBody;
import endpointrequests.JoinBody;
import endpointrequests.LoginBody;
import endpointresponses.CreateGameResponse;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.*;

import java.util.ArrayList;


public class ServerFacadeTests {

    private Server server;
    private ServerFacade serverFacade;

    private static final int port = 8080;
    private static final String serverURL = "http://localhost:" + port;

    private static final UserData userBob = new UserData("bob", "b0b", "bob@gmail.com");
    private static final LoginBody userBobLogin = new LoginBody("bob", "b0b");
    private static final CreateGameBody createGameBody = new CreateGameBody("newGame");
    private static final String fakeAuthToken = "fakeAuthToken";

    @BeforeAll
    public static void init() {
        //server = new Server();
        //server.run(port);
        //System.out.println("Started test HTTP server on " + port);
        //serverFacade = new ServerFacade(serverURL);
    }

    @BeforeEach
    public void refresh() {
        server = new Server();
        server.run(port);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade(serverURL);
    }

    @AfterEach
    public void reset() {
        try {
            serverFacade.clear();
        } catch (ResponseException _) {}
        try {
            server.stop();
        } catch (ResponseException _) {}
    }

//    @AfterAll
//    static void stopServer() {
//        server.stop();
//    }


    // clear()
    @Test
    public void clearPositive() throws ResponseException {
        Assertions.assertDoesNotThrow(() -> serverFacade.clear());


    }

    @Test
    public void clearNegative() throws ResponseException {
        server.stop();
        ResponseException e = Assertions.assertThrows(ResponseException.class, () -> serverFacade.clear());
        Assertions.assertEquals(StatusReader.ResponseStatus.NO_CONNECTION, e.responseStatus);
    }


    @Test
    public void registerPositive() throws ResponseException {
        AuthData registerResult = serverFacade.register(userBob);
        Assertions.assertEquals(userBob.username(), registerResult.username());
    }

    @Test
    public void registerNegative() throws ResponseException {
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.register(new UserData("", "", "")));
    }


    // These methods are dependent on serverFacade.register working first
    @Test
    public void loginPositive() throws ResponseException {
        serverFacade.register(userBob);
        AuthData loginResult = serverFacade.login(userBobLogin);
        Assertions.assertEquals(userBobLogin.username(), loginResult.username());
    }

    @Test
    public void loginNegativeUnregistered() throws ResponseException {
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.login(userBobLogin));
    }

    @Test
    public void loginNegativeMalformed() throws ResponseException {
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.login(new LoginBody("", "")));
    }

    @Test
    public void loginNegativeWrong() throws ResponseException {
        serverFacade.register(userBob);
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.login(new LoginBody(userBob.username(), "")));
    }


    // These methods are dependent on serverFacade.register working first
    @Test
    public void logoutPositive() throws ResponseException {
        AuthData registerResult = serverFacade.register(userBob);
        Assertions.assertDoesNotThrow(() -> serverFacade.logout(registerResult.authToken()));

    }

    @Test
    public void logoutNegativeUnregistered() throws ResponseException {
        AuthData registerResult = serverFacade.register(userBob);
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.logout(fakeAuthToken));

    }


    // These methods are dependent on serverFacade.createGame working first
    @Test
    public void listGamesPositive() throws ResponseException {
        AuthData registerResult = serverFacade.register(userBob);
        CreateGameResponse gameResponse = serverFacade.createGame(createGameBody, registerResult.authToken());
        ArrayList listResult = Assertions.assertDoesNotThrow(() -> serverFacade.listGames(registerResult.authToken()));
        Assertions.assertNotNull(listResult);
        Assertions.assertEquals(1, listResult.size());
        Assertions.assertNotNull(listResult.getFirst());
        Assertions.assertInstanceOf(GameData.class, listResult.getFirst());

    }

    @Test
    public void listGamesNegativeBadAuth() throws ResponseException {
        AuthData registerResult = serverFacade.register(userBob);
        CreateGameResponse gameResponse = serverFacade.createGame(createGameBody, registerResult.authToken());
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.listGames(fakeAuthToken));

    }


    // These methods are dependent on serverFacade.register working first
    @Test
    public void createGamePositive() throws ResponseException {
        AuthData registerResult = serverFacade.register(userBob);
        CreateGameResponse gameResponse = Assertions.assertDoesNotThrow(() -> serverFacade.createGame(createGameBody, registerResult.authToken()));
        Assertions.assertNotNull(gameResponse);

    }

    @Test
    public void createGameNegativeBadAuth() throws ResponseException {
        AuthData registerResult = serverFacade.register(userBob);
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.createGame(createGameBody, fakeAuthToken));

    }


    //  These methods are dependent on serverFacade.register and serverFacade.createGame working first
    @Test
    public void joinGamePositive() throws ResponseException {
        AuthData registerResult = serverFacade.register(userBob);
        CreateGameResponse gameResponse = serverFacade.createGame(createGameBody, registerResult.authToken());
        JoinBody joinGameBody = new JoinBody(ChessGame.TeamColor.WHITE, gameResponse.gameID());
        Assertions.assertDoesNotThrow(() -> serverFacade.joinGame(joinGameBody, registerResult.authToken()));
    }

    @Test
    public void joinGameNegativeNonexistant() throws ResponseException {
        JoinBody joinGameBody = new JoinBody(ChessGame.TeamColor.WHITE, 0);
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.joinGame(joinGameBody, fakeAuthToken));

    }

}
