package ui.client;

import server.ServerFacade;
import ui.states.ClientStates;

public class GameplayClient {

    private final ServerFacade serverFacade;
    private static final ClientStates myState = ClientStates.GAMEPLAY;

    public GameplayClient(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public EvalResult eval(String cmd, String[] params) {
        return null;
    }
}
