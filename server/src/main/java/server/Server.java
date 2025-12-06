package server;

import com.google.gson.Gson;
import dataaccess.*;
import endpointresponses.CreateGameResponse;
import endpointresponses.GameListResponse;
import model.AuthData;
import model.GameData;
import model.UserData;
import io.javalin.*;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import endpointrequests.CreateGameBody;
import endpointrequests.JoinGameBody;
import endpointrequests.LoginBody;
import server.websocket.UserCommandHandler;
import service.BadRequestException;
import service.Service;

import java.util.ArrayList;
import java.util.Map;

public class Server {

    private final Service userService;
    private final Javalin javalinServer;
    private final UserCommandHandler userCommandHandler;

    public Server() {
        DataAccess dataAccess;
        try {
            dataAccess = new MySqlDataAccess();
            //dataAccess = new MemoryDataAccess();
        } catch (DatabaseException e) {
            System.out.println(e.getMessage());
            System.out.println("Starting server with MemoryDataAccess instance instead.");
            dataAccess = new MemoryDataAccess();
        }

        userService = new Service(dataAccess);

        javalinServer = Javalin.create((JavalinConfig config) -> config.staticFiles.add("web"));

        userCommandHandler = new UserCommandHandler();

        // Register your endpoints and exception handlers here.
        // Register a user. If successful, an authorization authToken is returned. You may use the authToken with
        // future requests that require authorization. No authorization authToken is required to call this endpoint.
        javalinServer.post("user", (Context ctx) -> register(ctx));
        // Log in a user. If successful, an authorization authToken is returned. You may use the authToken with
        // future requests that require authorization. No authorization authToken is required to call this endpoint.
        javalinServer.post("session", (Context ctx) -> login(ctx));
        // Logs out an authenticated user. An authToken is required to call this endpoint.
        javalinServer.delete("session", (Context ctx) -> logout(ctx));
        // Lists all the games in the dataaccess. This API does not take a request body. The
        // response JSON lists all the games. An authToken is required to call this endpoint.
        javalinServer.get("game", (Context ctx) -> listGames(ctx));
        // Create a new Chess Game. The request body must contain a name for the game. The response JSON contains the ID of
        // created game, or if failed, an error message describing the reason. An authToken is required to call this endpoint.
        javalinServer.post("game", (Context ctx) -> createGame(ctx));
        // Join a Chess Game. The request body must contain the game ID and player color. An authToken is required to call this endpoint.
        javalinServer.put("game", (Context ctx) -> joinGame(ctx));
        // Clear ALL data from the dataaccess. This includes users and all game data. No authorization authToken is required.
        javalinServer.delete("db", (Context ctx) -> deleteDB(ctx));
        // Open a websocket connection to the server while in gameplay mode to send and receive gameplay messages
        javalinServer.ws("ws", ws -> {
            ws.onConnect(userCommandHandler);
            ws.onMessage(userCommandHandler);
            ws.onClose(userCommandHandler);
        });
    }

    private void register(Context ctx) {
        Gson serializer = new Gson();
        String requestJson = ctx.body();

        UserData user = serializer.fromJson(requestJson, UserData.class);
        try {
            AuthData authData = userService.register(user);

            ctx.result(serializer.toJson(authData));
        } catch (BadRequestException e) {
            report400Error(ctx, serializer, e);
        } catch (AlreadyTakenException e) {
            report403Error(ctx, serializer, e);
        } catch (DatabaseException e) {
            report500Error(ctx, serializer, e);
        }
    }

    private void login(Context ctx) {
        Gson serializer = new Gson();
        String requestJson = ctx.body();

        LoginBody loginBody = serializer.fromJson(requestJson, LoginBody.class);
        try {
            AuthData authData = userService.login(loginBody);

            ctx.result(serializer.toJson(authData));
        } catch (BadRequestException e) {
            report400Error(ctx, serializer, e);
        } catch (InvalidCredentialsException e) {
            report401Error(ctx, serializer, e);
        } catch (DatabaseException e) {
            report500Error(ctx, serializer, e);
        }
    }

    private void logout(Context ctx) {
        Gson serializer = new Gson();
        String authToken = ctx.header("authorization");
        try {
            userService.logout(authToken);

            ctx.result();
        } catch (InvalidCredentialsException e) {
            report401Error(ctx, serializer, e);
        } catch (DatabaseException e) {
            report500Error(ctx, serializer, e);
        }
    }

    private void listGames(Context ctx) {
        Gson serializer = new Gson();
        String authToken = ctx.header("authorization");
        try {
            ArrayList<GameData> gameList = userService.list(authToken);

            GameListResponse successResponse = new GameListResponse(gameList);
            ctx.result(serializer.toJson(successResponse));
        } catch (InvalidCredentialsException e) {
            report401Error(ctx, serializer, e);
        } catch (DatabaseException e) {
            report500Error(ctx, serializer, e);
        }
    }

    private void createGame(Context ctx) {
        Gson serializer = new Gson();
        String authToken = ctx.header("authorization");
        String requestJson = ctx.body();

        CreateGameBody createGameBody = serializer.fromJson(requestJson, CreateGameBody.class);
        try {
            int gameID = userService.create(authToken, createGameBody);

            CreateGameResponse successResponse = new CreateGameResponse(gameID);
            ctx.result(serializer.toJson(successResponse));
        } catch (BadRequestException e) {
            report400Error(ctx, serializer, e);
        } catch (InvalidCredentialsException e) {
            report401Error(ctx, serializer, e);
        } catch (DatabaseException e) {
            report500Error(ctx, serializer, e);
        }
    }

    private void joinGame(Context ctx) {
        Gson serializer = new Gson();
        String authToken = ctx.header("authorization");
        String requestJson = ctx.body();
        JoinGameBody joinGameBody = serializer.fromJson(requestJson, JoinGameBody.class);
        try {
            userService.join(authToken, joinGameBody);

            ctx.result();
        } catch (BadRequestException e) {
            report400Error(ctx, serializer, e);
        } catch (InvalidCredentialsException e) {
            report401Error(ctx, serializer, e);
        } catch (AlreadyTakenException e) {
            report403Error(ctx, serializer, e);
        } catch (DatabaseException e) {
            report500Error(ctx, serializer, e);
        }
    }


    private void deleteDB(Context ctx) {
        try {
            userService.clear();
        } catch (DatabaseException e) {
            Gson serializer = new Gson();
            report500Error(ctx, serializer, e);
        }
    }

    private void report400Error(Context ctx, Gson serializer, Exception e) {
        ctx.status(400);
        var errorResponse = Map.of("message", String.format("Error: bad request (%s)", e.getMessage()));
        ctx.result(serializer.toJson(errorResponse));
    }

    private void report401Error(Context ctx, Gson serializer, Exception e) {
        ctx.status(401);
        var errorResponse = Map.of("message", String.format("Error: unauthorized (%s)", e.getMessage()));
        ctx.result(serializer.toJson(errorResponse));
    }

    private void report403Error(Context ctx, Gson serializer, Exception e) {
        ctx.status(403);
        var errorResponse = Map.of("message", String.format("Error: already taken (%s)", e.getMessage()));
        ctx.result(serializer.toJson(errorResponse));
    }

    private void report500Error(Context ctx, Gson serializer, Exception e) {
        ctx.status(500);
        var errorResponse = Map.of("message", String.format("Error: %s", e.getMessage()));
        ctx.result(serializer.toJson(errorResponse));
    }

    public int run(int desiredPort) {
        javalinServer.start(desiredPort);
        return javalinServer.port();
    }

    public void stop() {
        javalinServer.stop();
    }
}
