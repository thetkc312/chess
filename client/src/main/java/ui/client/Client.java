package ui.client;

import server.ServerFacade;
import ui.states.UiStates;

public abstract class Client {

    private final ServerFacade serverFacade;

    public UiStates nextState;

    public abstract String eval(String cmd, String[] params);

    public Client(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }
}
