package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;

import java.util.Map;

public class Server {

    private final Javalin javalinServer;

    public Server() {
        javalinServer = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        // TODO: Implement actual delete functionality, see where to do this
        javalinServer.post("user", ctx -> register(ctx));
        javalinServer.delete("db", ctx -> ctx.result("{}"));

    }

    private void register(Context ctx) {
        Gson serializer = new Gson();
        String requestJson = ctx.body();
        Map request = serializer.fromJson(requestJson, Map.class);

        // FIXME: Implement actual authtoken generating
        Map result = Map.of("username", request.get("username"), "authToken", "myHardCodedAuthToken");
        ctx.result(serializer.toJson(result));
    }

    public int run(int desiredPort) {
        javalinServer.start(desiredPort);
        return javalinServer.port();
    }

    public void stop() {
        javalinServer.stop();
    }
}
