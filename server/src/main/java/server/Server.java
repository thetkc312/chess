package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;
import io.javalin.*;
import io.javalin.http.Context;
import service.UserServices;

public class Server {

    private final Javalin javalinServer;
    private final UserServices userService;

    public Server() {
        var DataAccess = new MemoryDataAccess();
        userService = new UserServices(DataAccess);
        javalinServer = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        // TODO: Implement actual delete functionality, see where to do this
        javalinServer.post("user", ctx -> register(ctx));
        javalinServer.delete("db", ctx -> ctx.result("{}"));

    }

    private void register(Context ctx) {
        try {
            Gson serializer = new Gson();
            String requestJson = ctx.body();

            UserData user = serializer.fromJson(requestJson, UserData.class);
            AuthData authData = userService.register(user);

            ctx.result(serializer.toJson(authData));
        } catch (DataAccessException e) {
            // TODO: Implement actual 403 error for attempting to create a user that is already taken
            String message = "";
        }

    }

    public int run(int desiredPort) {
        javalinServer.start(desiredPort);
        return javalinServer.port();
    }

    public void stop() {
        javalinServer.stop();
    }
}
