package ui.client;

import server.ServerFacade;

public class PostloginClient {

    private final ServerFacade serverFacade;

    public PostloginClient(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public String eval(String cmd, String[] params) {
        return "";
    }
}
