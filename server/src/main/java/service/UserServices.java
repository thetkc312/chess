package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

public class UserServices {
    private final DataAccess dataAccess;

    public UserServices(DataAccess dataAccess) {

        this.dataAccess = dataAccess;
    }
    // Register
    // Login
    // Logout
    public AuthData register(UserData user) throws DataAccessException {
        if (dataAccess.getUser(user.username()) != null) {
            // TODO: Implement custom ServiceAccessException
            throw new DataAccessException("403: User already exists");
        }
        return new AuthData(user.username(), generateAuthToken());
    }

    private String generateAuthToken() {
        // TODO: Implement actual authToken generation
        return "xyz";
    }
}
