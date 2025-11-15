package ui.client;

import server.ServerFacade;

public class PostloginClient extends Client {

    public PostloginClient(ServerFacade serverFacade) {
        super(serverFacade);
    }

    public String eval(String cmd, String[] params) {
        return "";
    }
}
