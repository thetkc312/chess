package server.websocket;

import com.google.gson.Gson;
import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;
import websocket.commands.UserGameCommand;
import websocket.commands.UserMoveCommand;


public class UserCommandHandler implements WsConnectHandler, WsMessageHandler {

    public final ConcurrentHashMap<Session, Session> connections = new ConcurrentHashMap<>();

    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) {
        // TODO: Add this connection to be tracked
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) {
        Gson serializer = new Gson();
        String wsMessageJson = ctx.message();

        UserGameCommand userGameCommand = serializer.fromJson(wsMessageJson, UserGameCommand.class);

        if (!validCommandCredentials(userGameCommand)) {

        }

        switch (userGameCommand.getCommandType()) {
            case CONNECT -> processConnection(userGameCommand);
            case MAKE_MOVE -> processMakeMove(serializer.fromJson(wsMessageJson, UserMoveCommand.class));
            case LEAVE -> processLeave(userGameCommand);
            case RESIGN -> processResign(userGameCommand);
            default -> ServerCommandSender.sendError("Could not recognize UserGameCommand type");
        }
    }

    private boolean validCommandCredentials(UserGameCommand userGameCommand) {
        //TODO: Implement credential checking
        return true;
    }

    private void processConnection(UserGameCommand userGameCommand) {

    }

    private void processMakeMove(UserMoveCommand userMoveCommand) {

    }

    private void processLeave(UserGameCommand userGameCommand) {

    }

    private void processResign(UserGameCommand userGameCommand) {

    }
}
