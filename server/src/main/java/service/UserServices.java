package service;

import dataaccess.AlreadyTakenException;
import dataaccess.DataAccess;
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
    // Register
    public AuthData register(UserData user) throws BadRequestException, AlreadyTakenException {
        if (invalidField(user.username()) || invalidField(user.password()) || invalidField(user.email())) {
            throw new BadRequestException("400: Malformed information for user registration.");
        }
        if (!dataAccess.createUser(user)) {
            throw new AlreadyTakenException("403: User already exists.");
        }
        return dataAccess.createAuth(user.username());
    }
    // Login
    public AuthData login(UserData user) throws BadRequestException, AlreadyTakenException, InvalidCredentialsException {
        if (invalidField(user.username()) || invalidField(user.password())) {
            throw new BadRequestException("400: Malformed information for user login.");
        }
        if (dataAccess.validLogin(user.username(), user.password())) {
            // This code would log out a user each time they were logged in elsewhere. This became
            // unnecessary with the behavior that a user can be logged in to multiple sessions at once.
//            if (dataAccess.hasAuthToken(user.username())) {
//                String authToken = dataAccess.getUserAuth(user.username());
//                dataAccess.logoutAuth(authToken);
//            }
            return dataAccess.createAuth(user.username());
        } else {
            throw new InvalidCredentialsException("401: Credentials do not match a known user.");
        }
    }
    // Logout
    public void logout(String authToken) throws InvalidCredentialsException {
        if (!dataAccess.logoutAuth(authToken)) {
            throw new InvalidCredentialsException("401: Authentication token does not match a known user for logout.");
        }
    }

    // List
    public ArrayList<GameData> list(String authToken) throws InvalidCredentialsException {
        if (!dataAccess.validAuth(authToken)) {
            throw new InvalidCredentialsException("401: Authentication token does not match a known user for creating a game.");
        }
        return dataAccess.listGames();
    }

    // Create
    public int create(String authToken, String gameName) throws BadRequestException, InvalidCredentialsException {
        if (invalidField(gameName)) {
            throw new BadRequestException("400: Malformed information for game creation (Game Name).");
        }
        if (!dataAccess.validAuth(authToken)) {
            throw new InvalidCredentialsException("401: Authentication token does not match a known user for creating a game.");
        }
        int gameID = dataAccess.createGame(gameName);
        return gameID;
    }

    // Clear
    public void clear() {
        dataAccess.clear();
    }

    private boolean invalidField(String field) {
        return (field == null) || (field.isBlank());
    }
}
