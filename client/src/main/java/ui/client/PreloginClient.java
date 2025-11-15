package ui.client;

import server.ServerFacade;

public class PreloginClient {

    private final ServerFacade serverFacade;

    public PreloginClient(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public String eval(String cmd, String[] params) {
        return "";
    }

}
