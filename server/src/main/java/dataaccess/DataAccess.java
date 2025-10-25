package dataaccess;

import model.AuthData;
import model.UserData;

public interface DataAccess {
    void clear();
    boolean createUser(UserData user); // True for success, False for failure
    boolean userExists(String username);
    boolean validLogin(String username, String password);
    AuthData createAuth(String username);
    String getUserAuth(String username);
    boolean hasAuthToken(String username);
    boolean validAuth(AuthData authData);
    boolean logoutAuth(String authToken);

}
