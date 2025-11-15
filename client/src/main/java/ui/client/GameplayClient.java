package ui.client;

import server.ServerFacade;

public class GameplayClient extends Client {

    public GameplayClient(ServerFacade serverFacade) {
        super(serverFacade);
    }

    public String eval(String cmd, String[] params) {
        return "";
    }
}
