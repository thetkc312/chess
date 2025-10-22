package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryDataAccess implements DataAccess {

    private final HashMap<String, UserData> userMap= new HashMap<>();

    @Override
    public void clear() {
        userMap.clear();
    }

    @Override
    public void createUser(UserData user) {
        if (userMap.containsKey(user.username())) {
            // TODO: Implement 403 error (username already taken)
        }
        userMap.put(user.username(), user);

    }

    @Override
    public UserData getUser(String username) {
        return userMap.get(username);
    }
}
