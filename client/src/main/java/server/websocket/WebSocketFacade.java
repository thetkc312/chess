package server.websocket;

import jakarta.websocket.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    private final Session session;

    public WebSocketFacade(int serverPort) throws Exception {
        URI serverUri = new URI("ws://localhost:" + serverPort + "/ws");
        WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();
        this.session = webSocketContainer.connectToServer(this, serverUri);

        this.session.addMessageHandler(new MessageHandler());
    }

    public WebSocketFacade() throws Exception {
        URI serverUri = new URI("ws://localhost:8080/ws");
        WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();
        this.session = webSocketContainer.connectToServer(this, serverUri);

        this.session.addMessageHandler(new MessageHandler());
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }
}
