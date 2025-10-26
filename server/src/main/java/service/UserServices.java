package service;

import chess.ChessGame;
import dataaccess.AlreadyTakenException;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.InvalidCredentialsException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.ArrayList;

public class UserServices {
    private final DataAccess dataAccess;

    public UserServices(DataAccess dataAccess) {

        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData user) throws BadRequestException, AlreadyTakenException, DataAccessException {
        if (invalidField(user.username()) || invalidField(user.password()) || invalidField(user.email())) {
            throw new BadRequestException("400: Malformed information for user registration.");
        }
        if (!dataAccess.createUser(user)) {
            throw new AlreadyTakenException("403: User already exists.");
        }
        return dataAccess.createAuth(user.username());
    }

    public AuthData login(UserData user) throws BadRequestException, InvalidCredentialsException, DataAccessException {
        if (invalidField(user.username()) || invalidField(user.password())) {
            throw new BadRequestException("400: Malformed information for user login.");
        }
        if (dataAccess.validLogin(user.username(), user.password())) {
            return dataAccess.createAuth(user.username());
        } else {
            throw new InvalidCredentialsException("401: Credentials do not match a known user.");
        }
    }

    public void logout(String authToken) throws InvalidCredentialsException, DataAccessException {
        if (!dataAccess.logoutAuth(authToken)) {
            throw new InvalidCredentialsException("401: Authentication token does not match a known user for logout.");
        }
    }

    public ArrayList<GameData> list(String authToken) throws InvalidCredentialsException, DataAccessException {
        if (!dataAccess.authExists(authToken)) {
            throw new InvalidCredentialsException("401: Authentication token does not match a known user for creating a game.");
        }
        return dataAccess.listGames();
    }

    public int create(String authToken, String gameName) throws BadRequestException, InvalidCredentialsException, DataAccessException {
        if (invalidField(gameName)) {
            throw new BadRequestException("400: Malformed information for game creation (Game Name).");
        }
        if (!dataAccess.authExists(authToken)) {
            throw new InvalidCredentialsException("401: Authentication token does not match a known user for creating a game.");
        }
        int gameID = dataAccess.createGame(gameName);
        return gameID;
    }

    public void joinGame(String authToken, int gameID, ChessGame.TeamColor teamColor) throws BadRequestException, InvalidCredentialsException, AlreadyTakenException, DataAccessException {
        if (invalidField(gameID) || invalidField(teamColor) || !dataAccess.gameExists(gameID)) {
            throw new BadRequestException("400: Malformed information for joining game.");
        }
        if (!dataAccess.authExists(authToken)) {
            throw new InvalidCredentialsException("401: Authentication token does not match a known user for joining a game.");
        }
        if (!dataAccess.roleOpen(gameID, teamColor)) {
            throw new AlreadyTakenException("403: Game role (Black/White) already taken.");
        }
        String username = dataAccess.getUser(authToken);
        dataAccess.joinGame(username, teamColor, gameID);
    }

    public void clear() throws DataAccessException {
        dataAccess.clear();
    }

    private boolean invalidField(Object field) {
        return field == null || field.toString().isBlank();
    }

    private boolean invalidField(int field) {
        return field == 0;
    }
}
