package websocket.messages;

import chess.ChessGame;
import model.GameData;

public class ServerLoadGameMessage extends ServerMessage {

    private final GameData game;

    public ServerLoadGameMessage(ServerMessageType type, GameData game) {
        super(type);
        this.game = game;
    }

    public GameData getGame() {
        return game;
    }
}
