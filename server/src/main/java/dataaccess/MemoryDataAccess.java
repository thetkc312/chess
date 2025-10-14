package dataaccess;

import datamodel.UserData;

import java.util.HashMap;
import java.util.HashSet;

public class MemoryDataAccess implements DataAccess {

    private final HashMap<String, UserData> userMap= new HashMap<>();

    @Override
    public void clear() {
        userMap.clear();
    }

    @Override
    public void createUser(UserData user) {
        // TODO: Implement 403 error (username already taken)
        userMap.put(user.username(), user);

    }

    @Override
    public UserData getUser(String username) {
        return userMap.get(username);
    }
}
