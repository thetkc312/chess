package client;

import chess.ChessGame;
import endpointrequests.CreateGameBody;
import endpointrequests.JoinGameBody;
import endpointrequests.LoginBody;
import endpointresponses.CreateGameResponse;
import endpointresponses.GameListResponse;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.*;

import java.util.ArrayList;


public class ServerFacadeTests {

    private Server server;
    private ServerFacade serverFacade;

    private static final UserData USER_BOB = new UserData("bob", "b0b", "bob@gmail.com");
    private static final LoginBody USER_BOB_LOGIN = new LoginBody("bob", "b0b");
    private static final CreateGameBody CREATE_GAME_BODY = new CreateGameBody("newGame");
    private static final String FAKE_AUTH_TOKEN = "fakeAuthToken";


    @BeforeEach
    public void refresh() {
        server = new Server();
        int port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade(port);
    }

    @AfterEach
    public void reset() {
        try {
            serverFacade.clear();
        } catch (ResponseException e) {}
        try {
            server.stop();
        } catch (ResponseException e) {}
    }


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
        AuthData registerResult = serverFacade.register(USER_BOB);
        Assertions.assertEquals(USER_BOB.username(), registerResult.username());
    }

    @Test
    public void registerNegative() throws ResponseException {
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.register(new UserData("", "", "")));
    }


    // These methods are dependent on serverFacade.register working first
    @Test
    public void loginPositive() throws ResponseException {
        serverFacade.register(USER_BOB);
        AuthData loginResult = serverFacade.login(USER_BOB_LOGIN);
        Assertions.assertEquals(USER_BOB_LOGIN.username(), loginResult.username());
    }

    @Test
    public void loginNegativeUnregistered() throws ResponseException {
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.login(USER_BOB_LOGIN));
    }

    @Test
    public void loginNegativeMalformed() throws ResponseException {
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.login(new LoginBody("", "")));
    }

    @Test
    public void loginNegativeWrong() throws ResponseException {
        serverFacade.register(USER_BOB);
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.login(new LoginBody(USER_BOB.username(), "")));
    }


    // These methods are dependent on serverFacade.register working first
    @Test
    public void logoutPositive() throws ResponseException {
        AuthData registerResult = serverFacade.register(USER_BOB);
        Assertions.assertDoesNotThrow(() -> serverFacade.logout(registerResult.authToken()));

    }

    @Test
    public void logoutNegativeUnregistered() throws ResponseException {
        AuthData registerResult = serverFacade.register(USER_BOB);
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.logout(FAKE_AUTH_TOKEN));

    }


    // These methods are dependent on serverFacade.createGame working first
    @Test
    public void listGamesPositive() throws ResponseException {
        AuthData registerResult = serverFacade.register(USER_BOB);
        CreateGameResponse gameResponse = serverFacade.createGame(CREATE_GAME_BODY, registerResult.authToken());
        GameListResponse gameListResult = Assertions.assertDoesNotThrow(() -> serverFacade.listGames(registerResult.authToken()));
        ArrayList<GameData> listResult = gameListResult.games();
        Assertions.assertNotNull(listResult);
        Assertions.assertEquals(1, listResult.size());
        Assertions.assertNotNull(listResult.getFirst());
        Assertions.assertInstanceOf(GameData.class, listResult.getFirst());

    }

    @Test
    public void listGamesNegativeBadAuth() throws ResponseException {
        AuthData registerResult = serverFacade.register(USER_BOB);
        CreateGameResponse gameResponse = serverFacade.createGame(CREATE_GAME_BODY, registerResult.authToken());
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.listGames(FAKE_AUTH_TOKEN));

    }


    // These methods are dependent on serverFacade.register working first
    @Test
    public void createGamePositive() throws ResponseException {
        AuthData registerResult = serverFacade.register(USER_BOB);
        CreateGameResponse gameResponse = Assertions.assertDoesNotThrow(() -> serverFacade.createGame(CREATE_GAME_BODY, registerResult.authToken()));
        Assertions.assertNotNull(gameResponse);

    }

    @Test
    public void createGameNegativeBadAuth() throws ResponseException {
        AuthData registerResult = serverFacade.register(USER_BOB);
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.createGame(CREATE_GAME_BODY, FAKE_AUTH_TOKEN));

    }


    //  These methods are dependent on serverFacade.register and serverFacade.createGame working first
    @Test
    public void joinGamePositive() throws ResponseException {
        AuthData registerResult = serverFacade.register(USER_BOB);
        CreateGameResponse gameResponse = serverFacade.createGame(CREATE_GAME_BODY, registerResult.authToken());
        JoinGameBody joinGameBody = new JoinGameBody(gameResponse.gameID(), ChessGame.TeamColor.WHITE);
        Assertions.assertDoesNotThrow(() -> serverFacade.joinGame(joinGameBody, registerResult.authToken()));
    }

    @Test
    public void joinGameNegativeNonexistant() throws ResponseException {
        JoinGameBody joinGameBody = new JoinGameBody(0, ChessGame.TeamColor.WHITE);
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.joinGame(joinGameBody, FAKE_AUTH_TOKEN));

    }

}
