package ui.client;

import server.ServerFacade;

public class GameplayClient {

    private final ServerFacade serverFacade;

    public GameplayClient(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public String eval(String cmd, String[] params) {
        return "";
    }
}
