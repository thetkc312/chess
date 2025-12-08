package server.websocket;

import chess.ChessGame;
import websocket.UserRole;

public class ActiveGameTracker {
    private int gameID;
    private UserRole userRole;

    public ActiveGameTracker(int gameID, UserRole userRole) {
        this.gameID = gameID;
        this.userRole = userRole;
    }

    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public ChessGame.TeamColor getUserTeam() {
        return switch (userRole) {
            case WHITE -> ChessGame.TeamColor.WHITE;
            case BLACK -> ChessGame.TeamColor.BLACK;
            default -> null;
        };
    }

    public void setUserTeam(ChessGame.TeamColor teamColor) {
        switch (teamColor) {
            case ChessGame.TeamColor.WHITE: setUserRole(UserRole.WHITE); break;
            case ChessGame.TeamColor.BLACK: setUserRole(UserRole.BLACK); break;
        };
    }
}
