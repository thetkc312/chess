package websocket.messages;

import chess.ChessGame;

public class ServerErrorMessage extends ServerMessage {
    private final String errorMessage;

    public ServerErrorMessage(ServerMessageType type, String errorMessage) {
        super(type);
        this.errorMessage = errorMessage;
    }
}
