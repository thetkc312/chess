package server.websocket;

import com.google.gson.Gson;
import jakarta.websocket.*;
import model.AuthData;

import java.net.URI;

public class WebSocketFacade extends Endpoint {

    private final Session session;
    private final ServerMessageObserver serverMessageObserver;
    private final ActiveGameTracker activeGameTracker;
    private final AuthData authData;

    public WebSocketFacade(ServerMessageObserver serverMessageObserver, ActiveGameTracker activeGameTracker, AuthData authData) throws Exception {
        this.serverMessageObserver = serverMessageObserver;
        this.activeGameTracker = activeGameTracker;
        this.authData = authData;
        URI serverUri = new URI("ws://localhost:8080/ws");
        WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();
        this.session = webSocketContainer.connectToServer(this, serverUri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String wsMessageJson) {
                serverMessageObserver.registerServerMessage(wsMessageJson);
            }
        });

        connectGame();
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }

    public void connectGame() {
        // TODO: Implement sending connect message with WS communication to leave game and update others
    }

    public void leaveGame() {
        // TODO: Implement sending leave message with WS communication to leave game and update others

    }

    public void moveInGame() {
        // TODO: Implement sending move message with WS communication to perform move and update others

    }

    public void forfeitGame() {
        // TODO: Implement sending resign message with WS communication to resign game and update others

    }
}
