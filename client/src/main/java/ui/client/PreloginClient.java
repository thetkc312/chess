package ui.client;

import server.ServerFacade;
import ui.states.ClientStates;

public class PreloginClient {

    private final ServerFacade serverFacade;
    private static final ClientStates MY_STATE = ClientStates.PRELOGIN;

    public PreloginClient(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public EvalResult eval(String cmd, String[] params) {
        return null;
    }

}
