package server.websocket;

import io.javalin.websocket.WsConnectContext;

public class ServerCommandSender {

    private WsConnectContext ctx;

    public ServerCommandSender(WsConnectContext ctx) {
        this.ctx = ctx;
    }

    public void sendLoadGame() {

    }

    public void sendError(String errorMessage) {

    }

    public void sendNotification(String message) {

    }
}
