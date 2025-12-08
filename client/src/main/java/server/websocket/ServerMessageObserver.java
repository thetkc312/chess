package server.websocket;

import websocket.messages.ServerErrorMessage;
import websocket.messages.ServerLoadGameMessage;
import websocket.messages.ServerNotificationMessage;

public class ServerMessageObserver {

    public ServerMessageObserver() {
    }

    public void processLoadGame(ServerLoadGameMessage loadGameMessage) {
        // TODO: Implement load_game rendering
    }

    public void processError(ServerErrorMessage errorMessage) {
        // TODO: Implement error rendering
    }

    public void processNotification(ServerNotificationMessage notificationMessage) {
        // TODO: Implement notification rendering
    }
}
