package server.websocket;

import chess.ChessBoard;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DatabaseException;
import io.javalin.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.commands.UserMoveCommand;

import java.util.concurrent.ConcurrentHashMap;


public class UserWSHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    // SessionID : WsContext
    public final ConcurrentHashMap<String, WsContext> wsSessions = new ConcurrentHashMap<>();

    // GameID : [ SessionID, SessionID, ... ]
    public final ConcurrentHashMap<String, String[]> gameSessions = new ConcurrentHashMap<>();

    public final DataAccess dataAccess;

    public UserWSHandler(DataAccess dataAccess) {

        this.dataAccess = dataAccess;
    }

    @Override
    public void handleConnect(WsConnectContext ctxConnect) {
        // TODO: Add this session to be tracked
        ctxConnect.enableAutomaticPings();
        wsSessions.put(ctxConnect.sessionId(), ctxConnect);
        System.out.println("Establishing WebSocket Connection Session:");
        System.out.println(ctxConnect.sessionId());
    }

    @Override
    public void handleClose(WsCloseContext ctxClose) {
        wsSessions.remove(ctxClose.sessionId());
        System.out.println("Closed session " + ctxClose.sessionId());
    }

    @Override
    public void handleMessage(WsMessageContext ctxMessage) {
        Gson serializer = new Gson();
        String wsMessageJson = ctxMessage.message();
        String wsSessionID = ctxMessage.sessionId();

        UserGameCommand userGameCommand = serializer.fromJson(wsMessageJson, UserGameCommand.class);

        if (!validCommandCredentials(userGameCommand)) {
            ServerCommandSender.sendError(ctxMessage, "The credentials for this command are invalid:\n" + userGameCommand);
        }

        int gameID = userGameCommand.getGameID();
        String username = getUsername(userGameCommand.getAuthToken());
        try {
            switch (userGameCommand.getCommandType()) {
                case CONNECT -> processConnect(wsSessionID, gameID);
                case MAKE_MOVE -> processMakeMove(wsSessionID, gameID, serializer.fromJson(wsMessageJson, UserMoveCommand.class).getMove());
                case LEAVE -> processLeave(wsSessionID, gameID);
                case RESIGN -> processResign(wsSessionID, gameID);
            }
        } catch (NullPointerException e) {
            ServerCommandSender.sendError(ctxMessage, "Could not recognize UserGameCommand type");
        }
    }

    private boolean validCommandCredentials(UserGameCommand userGameCommand) {
        //TODO: Implement credential checking
        System.out.println("Checking UserGameCommand credentials:");
        System.out.println(userGameCommand.getGameID());
        System.out.println(userGameCommand.getAuthToken());
        return true;
    }

    private String getUsername(String authToken) {
        //TODO: Implement username extraction from database
        return "fakeUsername";
    }

    private void processConnect(String wsSessionID, int gameID) {
        //TODO: Implement connection processing
        System.out.println("Processing CONNECT message...");

    }

    private void processMakeMove(String wsSessionID, int gameID, ChessMove move) {
        System.out.println("Processing MAKE_MOVE message...");
        //TODO: Implement make_move processing
        try {
            dataAccess.updateGameBoard(new ChessBoard());
        } catch (DatabaseException e) {
            ServerCommandSender.sendError(wsSessions.get(wsSessionID), "There was an error processing your move.");
        }
    }

    private void processLeave(String wsSessionID, int gameID) {
        //TODO: Implement leave processing
        System.out.println("Processing LEAVE message...");

    }

    private void processResign(String wsSessionID, int gameID) {
        //TODO: Implement resign processing
        System.out.println("Processing RESIGN message...");

    }
}
