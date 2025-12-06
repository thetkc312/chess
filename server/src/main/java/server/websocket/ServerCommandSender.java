package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsContext;
import websocket.messages.ServerErrorMessage;
import websocket.messages.ServerLoadGameMessage;
import websocket.messages.ServerMessage;
import websocket.messages.ServerNotificationMessage;

public class ServerCommandSender {

    private static final Gson serializer = new Gson();

    public static void sendLoadGame(WsContext ctx, ChessGame game) {
        System.out.println("Sending LOAD_GAME message...");
        ServerMessage serverMessage = new ServerLoadGameMessage(ServerMessage.ServerMessageType.ERROR, game);
        sendMessage(serverMessage, ctx);
    }

    public static void sendError(WsContext ctx, String errorMessage) {
        System.out.println("Sending ERROR message...");
        ServerMessage serverMessage = new ServerErrorMessage(ServerMessage.ServerMessageType.ERROR, errorMessage);
        sendMessage(serverMessage, ctx);
    }

    public static void sendNotification(WsContext ctx, String message) {
        System.out.println("Sending NOTIFICATION message...");
        ServerMessage serverMessage = new ServerNotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        sendMessage(serverMessage, ctx);
    }

    private static void sendMessage(ServerMessage serverMessage, WsContext ctx) {
        String serverMessageString = serializer.toJson(serverMessage);
        ctx.send(serverMessageString);
    }
}
