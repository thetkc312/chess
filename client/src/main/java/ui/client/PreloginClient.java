package ui.client;

import server.ServerFacade;

public class PreloginClient extends Client {

    public PreloginClient(ServerFacade serverFacade, ServerFacade serverFacade1) {
        super(serverFacade);
    }

    public String eval(String cmd, String[] params) {
        return "";
    }

}
