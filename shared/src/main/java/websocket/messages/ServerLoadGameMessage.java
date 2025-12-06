package websocket.messages;

import chess.ChessGame;

public class ServerLoadGameMessage extends ServerMessage {
    private final ChessGame game;

    public ServerLoadGameMessage(ServerMessageType type, ChessGame game) {
        super(type);
        this.game = game;
    }
}
