package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DatabaseException;
import io.javalin.websocket.*;
import model.GameData;
import websocket.UserRole;
import websocket.commands.UserGameCommand;
import websocket.commands.UserMoveCommand;

import java.util.ArrayList;
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
        return dataAccess.getUser(authToken);
    }

    private void processConnect(String rootSessionID, UserGameCommand userGameCommand) {
        System.out.println("Processing CONNECT message...");
        WsContext rootUserSession = wsSessions.get(rootSessionID);
        // If this sessionID is already stored, then the client is redundantly trying to connect a second time
        if (sessionUsernames.contains(rootSessionID)) {
            ServerCommandSender.sendError(rootUserSession,
                                          "An error was caused as a CONNECT request was made " +
                                                  "to the server while this session is already connected.");
            return;
        }
        // else, get the username corresponding to this user's authToken. It should only fail if the server has connection issues.
        String rootUsername;
        try {
            rootUsername = getUsername(userGameCommand.getAuthToken());
        } catch (DatabaseException e) {
            ServerCommandSender.sendError(rootUserSession,
                                          "There was an error while extracting the username " +
                                                  "corresponding to this authentication token");
            return;
        }
        sessionUsernames.put(rootSessionID, rootUsername);

        // If the game the client is connecting to is already documented, simply add its sessionID to the String Array for that game
        int activeGameID = userGameCommand.getGameID();
        if (gameSessions.containsKey(activeGameID)) {
            gameSessions.get(activeGameID).add(rootSessionID);
        // If the game the client is connecting to has not been documented, add it to the set
        // by its activeGameID with this user as the only element of its String Array value
        } else {
            ArrayList<String> newGameSessionIDArrayList = new ArrayList<>();
            newGameSessionIDArrayList.add(rootSessionID);
            gameSessions.put(activeGameID, newGameSessionIDArrayList);
        }

        try {
            ArrayList<GameData> gameDataArrayList = dataAccess.listGames();
            // Find which game this root user is in
            GameData foundGameData = null;
            for (GameData gameData : gameDataArrayList) {
                if (gameData.gameID() == activeGameID) {
                    foundGameData = gameData;
                }
            }
            if (foundGameData != null) {
                // When root client sends CONNECT, server sends LOAD_GAME back to that root client
                ServerCommandSender.sendLoadGame(rootUserSession, foundGameData);

                ArrayList<String> allGameSessionIDs = gameSessions.get(foundGameData.gameID());
                String connectNotification = String.format("User %s has joined the game as ", rootUsername);
                connectNotification += switch (findUserRole(rootUsername, foundGameData)) {
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
            } else {
                throw new DatabaseException("The game the user has requested to connect to could not be located");
            }
        } catch (DatabaseException e) {
            ServerCommandSender.sendError(rootUserSession, "There was an error connecting you to your game.");
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

    private void processMakeMove(String rootSessionID, UserGameCommand userGameCommand, ChessMove move) {
        System.out.println("Processing MAKE_MOVE message...");
        WsContext rootUserSession = wsSessions.get(rootSessionID);
        String rootUsername = sessionUsernames.get(rootSessionID);
        String opponentUsername = "_____";
        int activeGameID = userGameCommand.getGameID();
        // Check if the game is already over or the user is moving out of turn
        try {
            ArrayList<GameData> gameDataArrayList = dataAccess.listGames();
            // Find which game this root user is in
            for (GameData gameData : gameDataArrayList) {
                if (gameData.gameID() == activeGameID) {
                    if (!gameData.gameActive()) {
                        throw new InvalidMoveException(
                                "There has been an error as you attempted to make a move in a chess game that is not active.");
                    }
                    UserRole movingPieceTeam = findUserRole(rootUsername, gameData);
                    ChessGame.TeamColor gameActiveTeam = gameData.game().getTeamTurn();
                    ChessGame.TeamColor opponentTeam;
                    if (movingPieceTeam == UserRole.WHITE && gameData.blackUsername() != null) {
                        opponentUsername = gameData.blackUsername();
                    } else if (movingPieceTeam == UserRole.BLACK && gameData.whiteUsername() != null) {
                        opponentUsername = gameData.whiteUsername();
                    }
                    if ((movingPieceTeam == UserRole.WHITE && gameActiveTeam == ChessGame.TeamColor.WHITE)
                            || (movingPieceTeam == UserRole.BLACK && gameActiveTeam == ChessGame.TeamColor.BLACK)) {
                        break;
                    } else {
                        throw new InvalidMoveException("There has been an error as you attempted to make a move out of turn.");
                    }
                }
            }
        } catch (InvalidMoveException e) {
            ServerCommandSender.sendError(rootUserSession, e.getMessage());
            return;
        } catch (DatabaseException e) {
            ServerCommandSender.sendError(rootUserSession, "There was an error accessing the game where the move is to be made.");
            return;
        }

        try {
            dataAccess.executeChessMove(activeGameID, move);

            ArrayList<GameData> gameDataArrayList = dataAccess.listGames();
            // Find which game this root user is in
            GameData foundGameData = findGameData(gameDataArrayList, activeGameID);
            if (foundGameData != null) {
                // When root client sends makes a move, server sends LOAD_GAME to all clients
                ArrayList<String> allGameSessionIDs = gameSessions.get(activeGameID);
                for (String sessionID : allGameSessionIDs) {
                    ServerCommandSender.sendLoadGame(wsSessions.get(sessionID), foundGameData);
                }

                ChessGame chessGame = foundGameData.game();
                // Note that by now, the move has already been made, so the moving piece is found at the end position
                ChessPiece movingPiece = chessGame.getBoard().getPiece(move.getEndPosition());
                String moveNotification = String.format("User %s has moved their %s from %s to %s.",
                                                        rootUsername, movingPiece, move.getStartPosition(), move.getEndPosition());
                for (String sessionID : allGameSessionIDs) {
                    if (!sessionID.equals(rootSessionID)) {
                        // Also send a NOTIFICATION ServerMessage to all other participants in this game
                        ServerCommandSender.sendNotification(wsSessions.get(sessionID), moveNotification);
                    }
                }

                // Check if the move has resulted in a stalemate, checkmate or check for the enemy team
                ChessGame.TeamColor enemyColor = chessGame.getTeamTurn();
                String gameConditionNotification = null;
                if (chessGame.isInStalemate(enemyColor)) {
                    gameConditionNotification = String.format("This move has put the game into a stalemate between %s and %s",
                                                              rootUsername, opponentUsername);
                    dataAccess.endGame(activeGameID);
                } else if (chessGame.isInCheckmate(enemyColor)) {
                    gameConditionNotification = String.format("This move by %s has put the %s player, %s, into Checkmate!",
                                                              rootUsername, enemyColor, opponentUsername);
                    dataAccess.endGame(activeGameID);
                } else if (chessGame.isInCheck(enemyColor)) {
                    gameConditionNotification = String.format("This move by %s has put the %s player, %s, into Check!",
                                                              rootUsername, enemyColor, opponentUsername);
                }
                // If so, send a NOTIFICATION ServerMessage to all participants in this game
                if (gameConditionNotification != null) {
                    for (String sessionID : allGameSessionIDs) {
                        ServerCommandSender.sendNotification(wsSessions.get(sessionID), gameConditionNotification);
                    }
                }
            } else {
                throw new DatabaseException("The game where the move was made could not be located");
            }
        } catch (DatabaseException e) {
            ServerCommandSender.sendError(rootUserSession, "There was an error processing your move.");
            return;
        }
    }

    private void processLeave(String rootSessionID, UserGameCommand userGameCommand) {
        System.out.println("Processing LEAVE message...");
        WsContext rootUserSession = wsSessions.get(rootSessionID);
        int activeGameID = userGameCommand.getGameID();
        // Get the username corresponding to this user's authToken.
        String rootUsername = sessionUsernames.get(rootSessionID);

        try {
            ArrayList<GameData> gameDataArrayList = dataAccess.listGames();
            // Find which game this root user is in
            GameData foundGameData = findGameData(gameDataArrayList, activeGameID);
            if (foundGameData != null) {
                ArrayList<String> allGameSessionIDs = gameSessions.get(foundGameData.gameID());
                String leaveNotification = String.format("User %s, ", rootUsername);
                UserRole userRole = findUserRole(rootUsername, foundGameData);
                if (userRole == UserRole.WHITE) {
                    dataAccess.removeUser(activeGameID, rootUsername, ChessGame.TeamColor.WHITE);
                    leaveNotification += "the White Player, ";
                } else if (userRole == UserRole.BLACK) {
                    dataAccess.removeUser(activeGameID, rootUsername, ChessGame.TeamColor.BLACK);
                    leaveNotification += "the Black Player, ";
                } else {
                    leaveNotification += "an observer, ";
                }
                leaveNotification += "has left the game.";

                rootUserSession.closeSession();

                // When root client player sends LEAVE, server sends NOTIFICATION to all other clients
                for (String sessionID : allGameSessionIDs) {
                    if (!sessionID.equals(rootSessionID)) {
                        ServerCommandSender.sendNotification(wsSessions.get(sessionID), leaveNotification);
                    }
                }
            } else {
                throw new DatabaseException("The game the user has requested to resign from could not be located");
            }
        } catch (DatabaseException e) {
            ServerCommandSender.sendError(rootUserSession, "There was an error resigning from your game.");
        }
    }

    private void processResign(String rootSessionID, UserGameCommand userGameCommand) {
        System.out.println("Processing RESIGN message...");
        WsContext rootUserSession = wsSessions.get(rootSessionID);
        int activeGameID = userGameCommand.getGameID();
        // Get the username corresponding to this user's authToken.
        String rootUsername = sessionUsernames.get(rootSessionID);

        try {
            ArrayList<GameData> gameDataArrayList = dataAccess.listGames();
            // Find which game this root user is in
            GameData foundGameData = null;
            for (GameData gameData : gameDataArrayList) {
                if (gameData.gameID() == activeGameID) {
                    foundGameData = gameData;
                }
            }
            if (foundGameData != null) {
                ArrayList<String> allGameSessionIDs = gameSessions.get(foundGameData.gameID());
                String resignNotification = String.format("User %s, ", rootUsername);
                resignNotification += switch (findUserRole(rootUsername, foundGameData)) {
                    case WHITE -> "the White Player, ";
                    case BLACK -> "the Black Player, ";
                    case OBSERVER -> throw new DatabaseException("Only players may resign from a game, not observers.");
                };
                resignNotification += "has resigned from the game.";

                if (foundGameData.gameActive()) {
                    dataAccess.endGame(activeGameID);
                } else {
                    throw new DatabaseException("A player cannot resign from a game that has already ended.");
                }

                // When root client sends RESIGN, server sends NOTIFICATION to all clients
                for (String sessionID : allGameSessionIDs) {
                    ServerCommandSender.sendNotification(wsSessions.get(sessionID), resignNotification);
                }
            } else {
                throw new DatabaseException("The game the user has requested to resign from could not be located");
            }
        } catch (DatabaseException e) {
            ServerCommandSender.sendError(rootUserSession, "There was an error resigning from your game.");
            return;
        }
    }

    private GameData findGameData(ArrayList<GameData> gameDataArrayList, int activeGameID) {
        GameData foundGameData = null;
        for (GameData gameData : gameDataArrayList) {
            if (gameData.gameID() == activeGameID) {
                foundGameData = gameData;
            }
        }
        return foundGameData;
    }
}
