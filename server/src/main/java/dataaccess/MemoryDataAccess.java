package dataaccess;

import datamodel.AuthData;
import datamodel.UserData;

import java.util.HashMap;

public class MemoryDataAccess implements DataAccess {

    private final HashMap<String, UserData> userMap= new HashMap<>();
    private final HashMap<String, AuthData> authMap= new HashMap<>();
    //private final HashMap<String, UserData> gameMap= new HashMap<>();

    @Override
    public void clear() {
        userMap.clear();
        authMap.clear();
    }

    @Override
    public boolean createUser(UserData user) {
        if (userExists(user.username())) {
            return false;
        }
        userMap.put(user.username(), user);
        return true;
    }

    @Override
    public boolean userExists(String username) {
        return userMap.containsKey(username);
    }

    @Override
    public boolean validLogin(String username, String password) {
        if (!userMap.containsKey(username)) {
            return false;
        }
        return userMap.get(username).password() == password;
    }

    @Override
    public AuthData createAuth(String username) {
        AuthData authData = new AuthData(username, generateAuthToken());
        authMap.put(username, authData);
        return authData;
    }

    @Override
    public boolean authExists(String username) {
        return authMap.containsKey(username);
    }

    @Override
    public boolean validAuth(AuthData authData) {
        return authMap.get(authData.username()) == authData;
    }

    private String generateAuthToken() {
        // TODO: Implement actual authToken generation
        return "xyz";
    }
}
