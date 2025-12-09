package server.websocket;

import com.google.gson.Gson;
import model.GameData;
import ui.BoardRenderer;
import ui.ConsolePrinter;
import websocket.messages.ServerMessage;
import websocket.messages.ServerLoadGameMessage;
import websocket.messages.ServerErrorMessage;
import websocket.messages.ServerNotificationMessage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerMessageObserver {

    private static final Gson SERIALIZER = new Gson();

    private final BlockingQueue<String> blockingQueue = new LinkedBlockingQueue<>();
    private final ActiveGameTracker activeGameTracker;

    private volatile boolean running = true;

    public ServerMessageObserver(ActiveGameTracker activeGameTracker) {
        this.activeGameTracker = activeGameTracker;
    }

    public void registerServerMessage(String wsMessageJson) {
        blockingQueue.add(wsMessageJson);
    }

    public void messageListener() {
        while (running) {
            try {
                String wsMessageJson = blockingQueue.take();
                processMessage(wsMessageJson);
            } catch (InterruptedException ignore) {
            }
        }
    }

    public void stop() {
        running = false;
        Thread.currentThread().interrupt();
    }

    private void processMessage(String wsMessageJson) {
        ServerMessage serverMessage = SERIALIZER.fromJson(wsMessageJson, ServerMessage.class);
        switch (serverMessage.getServerMessageType()) {
            case LOAD_GAME -> processLoadGame(SERIALIZER.fromJson(wsMessageJson, ServerLoadGameMessage.class));
            case ERROR -> processError(SERIALIZER.fromJson(wsMessageJson, ServerErrorMessage.class));
            case NOTIFICATION -> processNotification(SERIALIZER.fromJson(wsMessageJson, ServerNotificationMessage.class));
        }
    }

    private void processLoadGame(ServerLoadGameMessage loadGameMessage) {
        GameData gameData = loadGameMessage.getGame();
        String result = formatGameData(gameData);
        result += "\n";
        result += BoardRenderer.renderBoard(gameData.game(), activeGameTracker.getUserTeam());
        ConsolePrinter.safePrint(result);
    }

    private void processError(ServerErrorMessage errorMessage) {
        String result = errorMessage.getErrorMessage();
        ConsolePrinter.safePrint(result);
    }

    private void processNotification(ServerNotificationMessage notificationMessage) {
        String result = notificationMessage.getMessage();
        ConsolePrinter.safePrint(result);
    }

    private String formatGameData(GameData gameData) {
        String uiGameData = "";
        uiGameData += gameData.gameName();
        uiGameData += ": White Team - ";
        uiGameData += representPlayerName(gameData.whiteUsername());
        uiGameData += " | Black Team - ";
        uiGameData += representPlayerName(gameData.blackUsername());
        return uiGameData;
    }

    private String representPlayerName(String playerName) {
        if (playerName == null) {
            return "_____";
        }
        return playerName;
    }
}
