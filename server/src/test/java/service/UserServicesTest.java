package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServicesTest {

    @Test
    void clear() {

    }

    @Test
    void register() throws Exception {
        // TODO: Parametrize DataAccess usage
        DataAccess db = new MemoryDataAccess();
        UserData user = new UserData("joe", "j@j.com", "toomanysecrets");
        UserServices userService = new UserServices(db);
        AuthData authData = userService.register(user);
        assertNotNull(authData);
        assertEquals(user.username(), authData.username());
        assertTrue(!authData.authToken().isEmpty());
    }

    @Test
    void registerInvalidUsername() throws Exception {
        // TODO: Parametrize DataAccess usage
        DataAccess db = new MemoryDataAccess();
        UserData user = new UserData("", "j@j.com", "toomanysecrets");
    }

    @Test
    void registerNullUsername() throws Exception {
        // TODO: Parametrize DataAccess usage
        DataAccess db = new MemoryDataAccess();
        UserData user = new UserData(null, "j@j.com", "toomanysecrets");
    }

}
