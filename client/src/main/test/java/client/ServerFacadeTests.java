package client;

import org.junit.jupiter.api.*;
import server.ResponseException;
import server.Server;
import server.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        int port = server.run(0);
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

    }

    @Test
    public void clearNegative() throws ResponseException {

    }


    // register()
    @Test
    public void registerPositive() throws ResponseException {

    }

    @Test
    public void registerNegative() throws ResponseException {

    }


    // login()
    @Test
    public void loginPositive() throws ResponseException {

    }

    @Test
    public void loginNegative() throws ResponseException {

    }


    // logout()
    @Test
    public void logoutPositive() throws ResponseException {

    }

    @Test
    public void logoutNegative() throws ResponseException {

    }


    // listGames()
    @Test
    public void listGamesPositive() throws ResponseException {

    }

    @Test
    public void listGamesNegative() throws ResponseException {

    }


    // createGame()
    @Test
    public void createGamePositive() throws ResponseException {

    }

    @Test
    public void createGameNegative() throws ResponseException {

    }


    // joinGame()
    @Test
    public void joinGamePositive() throws ResponseException {

    }

    @Test
    public void joinGameNegative() throws ResponseException {

    }

}
