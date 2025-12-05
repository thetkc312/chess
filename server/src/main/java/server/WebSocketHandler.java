package server;

import com.google.gson.Gson;
import io.javalin.websocket.*;
import model.UserData;
import org.jetbrains.annotations.NotNull;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler {

    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) {
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) {
        Gson serializer = new Gson();
        String wsMessageJson = ctx.message();

        UserData user = serializer.fromJson(wsMessageJson, UserData.class);
    }
}
