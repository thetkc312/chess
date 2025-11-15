package ui.client;

import server.ServerFacade;
import ui.states.ClientStates;

public class PostloginClient {

    private final ServerFacade serverFacade;
    private static final ClientStates myState = ClientStates.POSTLOGIN;

    public PostloginClient(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public EvalResult eval(String cmd, String[] params) {
        return null;
    }
}
