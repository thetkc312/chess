package websocket.commands;

import chess.ChessMove;

public class UserMoveCommand extends UserGameCommand {

    private final ChessMove move;

    public UserMoveCommand(CommandType commandType, String authToken, Integer gameID, ChessMove move) {
        super(commandType, authToken, gameID);
        this.move = move;
    }

    public ChessMove getMove() {
        return move;
    }
}
