package server.websocket;

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
}
