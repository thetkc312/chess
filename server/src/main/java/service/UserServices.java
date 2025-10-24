package service;

import dataaccess.AlreadyTakenException;
import dataaccess.DataAccess;
import dataaccess.InvalidCredentialsException;
import model.AuthData;
import model.UserData;

public class UserServices {
    private final DataAccess dataAccess;

    public UserServices(DataAccess dataAccess) {

        this.dataAccess = dataAccess;
    }
    // Register
    public AuthData register(UserData user) throws AlreadyTakenException {
        if (!dataAccess.createUser(user)) {
            throw new AlreadyTakenException("403: User already exists");
        }
        return dataAccess.createAuth(user.username());
    }
    // Login
    public AuthData login(UserData user) throws AlreadyTakenException, InvalidCredentialsException {
        if (dataAccess.authExists(user.username())) {
            throw new AlreadyTakenException("403: User already logged in elsewhere.");
        }
        if (dataAccess.validLogin(user.username(), user.password())) {
            return dataAccess.createAuth(user.username());
        } else {
            throw new InvalidCredentialsException("401: Credentials do not match a known user.");
        }
    }
    // Logout

    // Clear
    public void clear() {
        dataAccess.clear();
    }
}
