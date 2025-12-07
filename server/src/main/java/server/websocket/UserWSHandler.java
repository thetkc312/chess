package server.websocket;

import chess.ChessBoard;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DatabaseException;
import io.javalin.websocket.*;
import model.GameData;
import websocket.commands.UserGameCommand;
import websocket.commands.UserMoveCommand;
import websocket.messages.ServerLoadGameMessage;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;


public class UserWSHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    // SessionID : WsContext
    private final ConcurrentHashMap<String, WsContext> wsSessions = new ConcurrentHashMap<>();

    // SessionID : Username
    private final ConcurrentHashMap<String, String> sessionUsernames = new ConcurrentHashMap<>();

    // GameID : [ SessionID, SessionID, ... ]
    private final ConcurrentHashMap<Integer, ArrayList<String>> gameSessions = new ConcurrentHashMap<>();

    public final DataAccess dataAccess;

    public UserWSHandler(DataAccess dataAccess) {

        this.dataAccess = dataAccess;
    }

    @Override
    public void handleConnect(WsConnectContext ctxConnect) {
        System.out.println("Establishing WebSocket Connection Session:");
        System.out.println(ctxConnect.sessionId());
        // TODO: Add this session to be tracked
        ctxConnect.enableAutomaticPings();
        wsSessions.put(ctxConnect.sessionId(), ctxConnect);
    }

    @Override
    public void handleClose(WsCloseContext ctxClose) {
        String closingSessionID = ctxClose.sessionId();
        wsSessions.remove(closingSessionID);
        sessionUsernames.remove(closingSessionID);
        for (ArrayList<String> gameParticipants : gameSessions.values()) {
            gameParticipants.remove(closingSessionID);
        }
        System.out.println("Closed session " + ctxClose.sessionId());
    }

    @Override
    public void handleMessage(WsMessageContext ctxMessage) {
        Gson serializer = new Gson();
        String wsMessageJson = ctxMessage.message();
        String wsSessionID = ctxMessage.sessionId();

        UserGameCommand userGameCommand = serializer.fromJson(wsMessageJson, UserGameCommand.class);

        if (!validCommandCredentials(userGameCommand)) {
            ServerCommandSender.sendError(ctxMessage, "There was an error processing the credentials for this command:\n" + userGameCommand);
            return;
        }

        if (userGameCommand.getCommandType() == null) {
            ServerCommandSender.sendError(ctxMessage, "There was an error processing the UserGameCommand type");
            return;
        }
        switch (userGameCommand.getCommandType()) {
            case CONNECT -> processConnect(wsSessionID, userGameCommand);
            case MAKE_MOVE -> processMakeMove(wsSessionID, userGameCommand, serializer.fromJson(wsMessageJson, UserMoveCommand.class).getMove());
            case LEAVE -> processLeave(wsSessionID, userGameCommand);
            case RESIGN -> processResign(wsSessionID, userGameCommand);
        }
    }

    private boolean validCommandCredentials(UserGameCommand userGameCommand) {
        //TODO: Implement credential checking
        System.out.println("Checking UserGameCommand credentials:");

        System.out.println(userGameCommand.getGameID());
        try {
            ArrayList<GameData> gameDataArrayList = dataAccess.listGames();
            ArrayList<Integer> validGameIDList = new ArrayList<>();
            for (GameData gameData : gameDataArrayList) {
                validGameIDList.add(gameData.gameID());
            }
            if (!validGameIDList.contains(userGameCommand.getGameID())) {
                return false;
            }
        } catch (DatabaseException e) {
            return false;
        }

        System.out.println(userGameCommand.getAuthToken());
        try {
            if (!dataAccess.authExists(userGameCommand.getAuthToken())) {
                return false;
            }
        } catch (DatabaseException e) {
            return false;
        }
        return true;
    }

    private String getUsername(String authToken) throws DatabaseException {
        //TODO: Implement username extraction from database
        return dataAccess.getUser(authToken);
    }

    private void processConnect(String rootSessionID, UserGameCommand userGameCommand) {
        System.out.println("Processing CONNECT message...");
        WsContext rootUserSession = wsSessions.get(rootSessionID);
        // If this sessionID is already stored, then the client is redundantly trying to connect a second time
        if (sessionUsernames.contains(rootSessionID)) {
            ServerCommandSender.sendError(rootUserSession, "An error was caused as a CONNECT request was made to the server while this session is already connected.");
            return;
        }
        // else, get the username corresponding to this user's authToken. It should only fail if the server has connection issues.
        String rootUsername;
        try {
            rootUsername = getUsername(userGameCommand.getAuthToken());
        } catch (DatabaseException e) {
            ServerCommandSender.sendError(rootUserSession, "There was an error while extracting the username corresponding to this authentication token");
            return;
        }
        sessionUsernames.put(rootSessionID, rootUsername);

        // If the game the client is connecting to is already documented, simply add its sessionID to the String Array for that game
        int activeGameID = userGameCommand.getGameID();
        if (gameSessions.containsKey(activeGameID)) {
            gameSessions.get(activeGameID).add(rootSessionID);
        // If the game the client is connecting to has not been documented, add it to the set by its activeGameID with this user as the only element of its String Array value
        } else {
            ArrayList<String> newGameSessionIDArrayList = new ArrayList<>();
            newGameSessionIDArrayList.add(rootSessionID);
            gameSessions.put(activeGameID, newGameSessionIDArrayList);
        }

        try {
            ArrayList<GameData> gameDataArrayList = dataAccess.listGames();
            // Find which game this root user is in
            for (GameData gameData : gameDataArrayList) {
                if (gameData.gameID() == activeGameID) {
                    // When root client sends CONNECT, server sends LOAD_GAME back to that root client
                    ServerCommandSender.sendLoadGame(rootUserSession, gameData);

                    ArrayList<String> allGameSessionIDs = gameSessions.get(gameData.gameID());
                    String connectNotification = String.format("User %s has joined the game as ", rootUsername);
                    connectNotification += switch (findUserRole(rootUsername, gameData)) {
                        case WHITE -> "the White Player.";
                        case BLACK -> "the Black Player.";
                        case OBSERVER -> "an observer.";
                    };
                    for (String sessionID : allGameSessionIDs) {
                        if (!sessionID.equals(rootSessionID)) {
                            // Also send a NOTIFICATION ServerMessage to all other participants in this game
                            ServerCommandSender.sendNotification(wsSessions.get(sessionID), connectNotification);
                        }
                    }
                    return;
                }
            }
            throw new DatabaseException("The game the user has requested to connect to could not be located");
        } catch (DatabaseException e) {
            ServerCommandSender.sendError(rootUserSession, "There was an error connecting you to your game.");
            return;
        }
    }

    private UserRole findUserRole(String username, GameData gameData) {
        // WARNING: This method fails when joins a game as white, then also as black.
        // It will notify all other users that the user has joined as white both times.
        if (username.equals(gameData.whiteUsername())) {
            return UserRole.WHITE;
        }
        if (username.equals(gameData.blackUsername())) {
            return UserRole.BLACK;
        }
        return UserRole.OBSERVER;
    }

    private void processMakeMove(String wsSessionID, UserGameCommand userGameCommand, ChessMove move) {
        System.out.println("Processing MAKE_MOVE message...");
        //TODO: Implement make_move processing
        try {
            dataAccess.updateGameBoard(new ChessBoard());
        } catch (DatabaseException e) {
            ServerCommandSender.sendError(wsSessions.get(wsSessionID), "There was an error processing your move.");
            return;
        }
    }

    private void processLeave(String wsSessionID, UserGameCommand userGameCommand) {
        //TODO: Implement leave processing
        System.out.println("Processing LEAVE message...");

    }

    private void processResign(String rootSessionID, UserGameCommand userGameCommand) {
        System.out.println("Processing RESIGN message...");
        WsContext rootUserSession = wsSessions.get(rootSessionID);
        // Get the username corresponding to this user's authToken. It should only fail if the server has connection issues.
        String rootUsername;
        try {
            rootUsername = getUsername(userGameCommand.getAuthToken());
        } catch (DatabaseException e) {
            ServerCommandSender.sendError(rootUserSession, "There was an error while extracting the username corresponding to this authentication token");
            return;
        }

        int activeGameID = userGameCommand.getGameID();
        try {
            ArrayList<GameData> gameDataArrayList = dataAccess.listGames();
            // Find which game this root user is in
            for (GameData gameData : gameDataArrayList) {
                if (gameData.gameID() == activeGameID) {
                    // TODO: Implement a way to track whether a game has been ended. End a game if someone resigns. Do not let anybody resign from an ended game.
                    ArrayList<String> allGameSessionIDs = gameSessions.get(gameData.gameID());
                    String connectNotification = String.format("User %s, ", rootUsername);
                    connectNotification += switch (findUserRole(rootUsername, gameData)) {
                        case WHITE -> "the White Player, ";
                        case BLACK -> "the Black Player, ";
                        case OBSERVER -> throw new DatabaseException("Only players may resign from a game, not observers.");
                    };
                    connectNotification += "has resigned from the game.";
                    // When root client sends RESIGN, server sends NOTIFICATION to all clients
                    for (String sessionID : allGameSessionIDs) {
                        ServerCommandSender.sendNotification(wsSessions.get(sessionID), connectNotification);
                    }
                    return;
                }
            }
            throw new DatabaseException("The game the user has requested to resign from could not be located");
        } catch (DatabaseException e) {
            ServerCommandSender.sendError(rootUserSession, "There was an error resigning from your game.");
            return;
        }
    }
}
