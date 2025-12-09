package server.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import jakarta.websocket.*;
import model.AuthData;
import websocket.commands.UserGameCommand;
import websocket.commands.UserMoveCommand;

import java.io.IOException;
import java.net.URI;

public class WebSocketFacade extends Endpoint {

    private static final Gson SERIALIZER = new Gson();

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

    public void connectGame() throws IOException {
        UserGameCommand connect = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authData.authToken(), activeGameTracker.getGameID());
        session.getBasicRemote().sendText(SERIALIZER.toJson(connect));
    }

    public void leaveGame() throws IOException {
        UserGameCommand leave = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authData.authToken(), activeGameTracker.getGameID());
        session.getBasicRemote().sendText(SERIALIZER.toJson(leave));

    }

    public void moveInGame(ChessMove mv) throws IOException {
        UserMoveCommand move = new UserMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authData.authToken(), activeGameTracker.getGameID(), mv);
        session.getBasicRemote().sendText(SERIALIZER.toJson(move));
    }

    public void forfeitGame() throws IOException {
        UserGameCommand forfeit = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authData.authToken(), activeGameTracker.getGameID());
        session.getBasicRemote().sendText(SERIALIZER.toJson(forfeit));
    }
}
