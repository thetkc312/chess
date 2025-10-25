package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryDataAccess implements DataAccess {

    private final HashMap<String, UserData> userMap = new HashMap<>();
    private final HashMap<String, String> authMap = new HashMap<>(); // Hash map of format AuthToken: Username
    private final HashMap<String, String> authUserMap = new HashMap<>(); // Hash map of format Username: AuthToken
    //private final HashMap<String, UserData> gameMap= new HashMap<>();

    @Override
    public void clear() {
        userMap.clear();
        authMap.clear();
        authUserMap.clear();
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
        return userMap.get(username).password().equals(password);
    }

    @Override
    public AuthData createAuth(String username) {
        String authToken = generateAuthToken();
        authMap.put(authToken, username);
        authUserMap.put(username, authToken);
        return new AuthData(username, authToken);
    }

    @Override
    public String getUserAuth(String username) {
        return authUserMap.get(username);
    }

    @Override
    public boolean hasAuthToken(String username) {
        return authUserMap.containsKey(username);
    }

    @Override
    public boolean validAuth(AuthData authData) {
        return authMap.get(authData.authToken()).equals(authData.username());
    }

    @Override
    public boolean logoutAuth(String authToken) {
        if (!authMap.containsKey(authToken)) {
            return false;
        }
        authMap.remove(authToken);
        String username = authMap.get(authToken);
        authUserMap.remove(username);
        return true;

    }

    private String generateAuthToken() {
        // TODO: Implement actual authToken generation
        return UUID.randomUUID().toString();
    }
}
