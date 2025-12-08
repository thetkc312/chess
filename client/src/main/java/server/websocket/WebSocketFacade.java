package server.websocket;

import com.google.gson.Gson;
import jakarta.websocket.*;
import model.AuthData;
import websocket.messages.ServerErrorMessage;
import websocket.messages.ServerLoadGameMessage;
import websocket.messages.ServerMessage;
import websocket.messages.ServerNotificationMessage;

import java.net.URI;

public class WebSocketFacade extends Endpoint {

    private final Session session;
    private final ServerMessageObserver serverMessageObserver;

    public WebSocketFacade(ServerMessageObserver serverMessageObserver) throws Exception {
        this.serverMessageObserver = serverMessageObserver;
        URI serverUri = new URI("ws://localhost:8080/ws");
        WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();
        this.session = webSocketContainer.connectToServer(this, serverUri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String wsMessageJson) {
                Gson serializer = new Gson();
                ServerMessage serverMessage = serializer.fromJson(wsMessageJson, ServerMessage.class);
                switch (serverMessage.getServerMessageType()) {
                    case LOAD_GAME -> serverMessageObserver.processLoadGame(serializer.fromJson(wsMessageJson, ServerLoadGameMessage.class));
                    case ERROR -> serverMessageObserver.processError(serializer.fromJson(wsMessageJson, ServerErrorMessage.class));
                    case NOTIFICATION -> serverMessageObserver.processNotification(serializer.fromJson(wsMessageJson, ServerNotificationMessage.class));
                }
            }
        });
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }

    public void leaveGame(ActiveGameTracker activeGameTracker, AuthData authData) {
        // TODO: Implement rendering leave results with WS communication to leave game and update others

    }

    public void moveInGame(ActiveGameTracker activeGameTracker, AuthData authData) {
        // TODO: Implement rendering move results with WS communication to perform move and update others

    }

    public void forfeitGame(ActiveGameTracker activeGameTracker, AuthData authData) {
        // TODO: Implement rendering resign results with WS communication to resign game and update others

    }
}
