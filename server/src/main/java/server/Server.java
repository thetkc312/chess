package server;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import dataaccess.AlreadyTakenException;
import dataaccess.DataAccessException;
import dataaccess.InvalidCredentialsException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;
import io.javalin.*;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import service.UserServices;

public class Server {

    private final Javalin javalinServer;
    private final UserServices userService;

    public Server() {
        var DataAccess = new MemoryDataAccess();
        userService = new UserServices(DataAccess);
        javalinServer = Javalin.create( (JavalinConfig config) -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalinServer.post("user", (Context ctx) -> register(ctx)); // Register a user. If successful, an authorization authToken is returned. You may use the authToken with future requests that require authorization. No authorization authToken is required to call this endpoint.
        javalinServer.post("session", (Context ctx) -> login(ctx)); // TODO: Log in a user. If successful, an authorization authToken is returned. You may use the authToken with future requests that require authorization. No authorization authToken is required to call this endpoint.
        //javalinServer.delete("session", (Context ctx) -> logout(ctx)); // TODO: Logs out an authenticated user. An authToken is required to call this endpoint.
        //javalinServer.get("game", (Context ctx) -> getGames(ctx)); // TODO: Lists all the games in the database. This API does not take a request body. The response JSON lists all the games. An authToken is required to call this endpoint.
        //javalinServer.post("game", (Context ctx) -> newGame(ctx)); // TODO: Create a new Chess Game. The request body must contain a name for the game. The response JSON contains the ID of created game, or if failed, an error message describing the reason. An authToken is required to call this endpoint.
        //javalinServer.put("game", (Context ctx) -> joinGame(ctx)); // TODO: Join a Chess Game. The request body must contain the game ID and player color. An authToken is required to call this endpoint.
        javalinServer.delete("db", (Context ctx) -> deleteDB(ctx)); // Clear ALL data from the database. This includes users and all game data. No authorization authToken is required.

    }

    private void register(Context ctx) {
        Gson serializer = new Gson();
        String requestJson = ctx.body();

        UserData user = serializer.fromJson(requestJson, UserData.class);
        try {
            AuthData authData = userService.register(user);

            ctx.result(serializer.toJson(authData));
        } catch (AlreadyTakenException e) {
            ctx.status(403); //FIXME: Actually set the status code
            ctx.result(e.getMessage());
        }
    }

    private void login(Context ctx) {
        Gson serializer = new Gson();
        String requestJson = ctx.body();

        UserData user = serializer.fromJson(requestJson, UserData.class); //FIXME: Consider if the "email" field needs to be filled in this instance
        try {
            AuthData authData = userService.login(user);

            ctx.result(serializer.toJson(authData));
        } catch (AlreadyTakenException e) {
            ctx.status(403);
            ctx.result(e.getMessage());
        } catch (InvalidCredentialsException e) {
            ctx.status(401);
            ctx.result(e.getMessage());
        }
    }

    private void deleteDB(Context ctx) {
        userService.clear();
    }

    public int run(int desiredPort) {
        javalinServer.start(desiredPort);
        return javalinServer.port();
    }

    public void stop() {
        javalinServer.stop();
    }
}
