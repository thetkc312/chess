package dataaccess;

import datamodel.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DataAccessTest {

    @Test
    void clear() {
        // TODO: Parametrize DataAccess usage
        DataAccess db = new MemoryDataAccess();
        db.createUser(new UserData("joe", "j@j.com", "toomanysecrets"));
        db.clear();
        assertNull(db.userExists("joe"));
    }

    @Test
    void createUser() {
        // TODO: Parametrize DataAccess usage
        DataAccess db = new MemoryDataAccess();
        UserData user = new UserData("joe", "j@j.com", "toomanysecrets");
        db.createUser(user);
        assertEquals(user, db.userExists(user.username()));
    }

    @Test
    void getUser() {
        // TODO: Write getUser test
    }
}
