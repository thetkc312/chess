package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import endpointrequests.CreateGameBody;
import endpointrequests.JoinGameBody;
import endpointrequests.LoginBody;

import java.util.ArrayList;

public class Service {
    private final DataAccess dataAccess;

    public Service(DataAccess dataAccess) {

        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData user)
            throws BadRequestException, AlreadyTakenException, DatabaseException {
        if (invalidField(user.username()) || invalidField(user.password()) || invalidField(user.email())) {
            throw new BadRequestException("400: Malformed information for user registration.");
        }
        if (!dataAccess.createUser(user)) {
            throw new AlreadyTakenException("403: User already exists.");
        }
        return dataAccess.createAuth(user.username());
    }

    public AuthData login(LoginBody user)
            throws BadRequestException, InvalidCredentialsException, DatabaseException {
        if (invalidField(user.username()) || invalidField(user.password())) {
            throw new BadRequestException("400: Malformed information for user login.");
        }
        if (dataAccess.validLogin(user.username(), user.password())) {
            return dataAccess.createAuth(user.username());
        } else {
            throw new InvalidCredentialsException("401: Credentials do not match a known user.");
        }
    }

    public void logout(String authToken)
            throws InvalidCredentialsException, DatabaseException {
        if (!dataAccess.logoutAuth(authToken)) {
            throw new InvalidCredentialsException("401: Authentication token does not match a known user for logout.");
        }
    }

    public ArrayList<GameData> list(String authToken)
            throws InvalidCredentialsException, DatabaseException {
        if (!dataAccess.authExists(authToken)) {
            throw new InvalidCredentialsException("401: Authentication token does not match a known user for creating a game.");
        }
        return dataAccess.listGames();
    }

    public int create(String authToken, CreateGameBody createGameBody)
            throws BadRequestException, InvalidCredentialsException, DatabaseException {
        if (invalidField(createGameBody.gameName())) {
            throw new BadRequestException("400: Malformed information for game creation (Game Name).");
        }
        if (!dataAccess.authExists(authToken)) {
            throw new InvalidCredentialsException("401: Authentication token does not match a known user for creating a game.");
        }
        int gameID = dataAccess.createGame(createGameBody.gameName());
        return gameID;
    }

    public void join(String authToken, JoinGameBody joinGameBody)
            throws BadRequestException, InvalidCredentialsException, AlreadyTakenException, DatabaseException {
        if (invalidField(joinGameBody.gameID()) || invalidField(joinGameBody.playerColor()) || !dataAccess.gameExists(joinGameBody.gameID())) {
            throw new BadRequestException("400: Malformed information for joining game.");
        }
        if (!dataAccess.authExists(authToken)) {
            throw new InvalidCredentialsException("401: Authentication token does not match a known user for joining a game.");
        }
        if (!dataAccess.roleOpen(joinGameBody.gameID(), joinGameBody.playerColor())) {
            throw new AlreadyTakenException("403: Game role (Black/White) already taken.");
        }
        String username = dataAccess.getUser(authToken);
        dataAccess.joinGame(joinGameBody.gameID(), username, joinGameBody.playerColor());
    }

    public void clear()
            throws DatabaseException {
        dataAccess.clear();
    }

    private boolean invalidField(Object field) {
        return field == null || field.toString().isBlank();
    }

    private boolean invalidField(int field) {
        return field == 0;
    }
}
