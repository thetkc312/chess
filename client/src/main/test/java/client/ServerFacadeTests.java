package client;

import org.junit.jupiter.api.*;
import server.ResponseException;
import server.Server;
import server.ServerFacade;
import server.StatusReader;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;
    private static final int port = 0;

    @BeforeAll
    public static void init() {
        server = new Server();
        server.run(port);
        System.out.println("Started test HTTP server on " + port);
        String serverURL = "http://localhost:" + port;
        serverFacade = new ServerFacade(serverURL);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


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
        server.run(port);
    }


    // register()
    @Test
    public void registerPositive() throws ResponseException {
        Assertions.fail();
    }

    @Test
    public void registerNegative() throws ResponseException {
        Assertions.fail();

    }


    // login()
    @Test
    public void loginPositive() throws ResponseException {
        Assertions.fail();

    }

    @Test
    public void loginNegative() throws ResponseException {
        Assertions.fail();

    }


    // logout()
    @Test
    public void logoutPositive() throws ResponseException {
        Assertions.fail();

    }

    @Test
    public void logoutNegative() throws ResponseException {
        Assertions.fail();

    }


    // listGames()
    @Test
    public void listGamesPositive() throws ResponseException {
        Assertions.fail();

    }

    @Test
    public void listGamesNegative() throws ResponseException {
        Assertions.fail();

    }


    // createGame()
    @Test
    public void createGamePositive() throws ResponseException {
        Assertions.fail();

    }

    @Test
    public void createGameNegative() throws ResponseException {
        Assertions.fail();

    }


    // joinGame()
    @Test
    public void joinGamePositive() throws ResponseException {
        Assertions.fail();

    }

    @Test
    public void joinGameNegative() throws ResponseException {
        Assertions.fail();

    }

}
