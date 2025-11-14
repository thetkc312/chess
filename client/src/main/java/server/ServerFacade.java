package server;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

// Handles HTTP communication with the server, throws ResponseException exceptions based on response status codes
public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    // Take nothing, clear database, return nothing
    public void clear() throws ResponseException {
        HttpRequest request = buildRequest("DELETE", "/db", null);
        sendRequest(request);

    }

    // Take complete UserData, register and log in the user, return AuthData
    public AuthData register(UserData userData) throws ResponseException {
        HttpRequest request = buildRequest("POST", "/user", userData);
        HttpResponse<String> response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    // Take UserData missing email, log in the user, return AuthData
    public AuthData login(UserData userData) throws ResponseException {
        // TODO: Build custom body class to store the attributes used specifically by login
        HttpRequest request = buildRequest("POST", "/session", userData);
        HttpResponse<String> response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    // Take String for authToken, logout that active session, return nothing
    public void logout(String authToken) throws ResponseException {

    }

    // Take String for authToken, discover all available games, return ArrayList<GameData>
    public ArrayList<GameData> listGames(String authToken) throws ResponseException {
        return null;
    }

    // Take Strings for authToken and gameName, create a new game, return Integer for gameID
    public int createGame(String authToken, String gameName) throws ResponseException {
        return 0;
    }

    // Take String for authToken, a ChessGame.TeamColor for playerColor and int for gameID, add the player to the game, return nothing
    public void joinGame(String authToken, ChessGame.TeamColor playerColor, int gameID) throws ResponseException {

    }


    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            // TODO: Verify that NO_CONNECTION is the only scenario where client.send would throw an exception.
            throw new ResponseException(ResponseException.ResponseStatus.NO_CONNECTION, ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw ResponseException.fromJson(body);
            }

            throw new ResponseException(ResponseException.fromHttpStatusCode(status), "other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return false;
    }
}
