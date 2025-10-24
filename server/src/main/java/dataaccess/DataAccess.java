package dataaccess;

import model.AuthData;
import model.UserData;

public interface DataAccess {
    void clear();
    boolean createUser(UserData user); // True for success, False for failure
    boolean userExists(String username);
    boolean validLogin(String username, String password);
    //boolean logout(AuthData auth);
    AuthData createAuth(String username);
    boolean hasAuthToken(String username);
    boolean validAuth(AuthData authData);

}
