package server;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import endpointresponses.CreateGameResponse;
import endpointresponses.GameListResponse;
import model.AuthData;
import model.GameData;
import model.UserData;

import endpointrequests.CreateGameBody;
import endpointrequests.JoinBody;
import endpointrequests.LoginBody;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

// Handles HTTP communication with the server, throws ResponseException exceptions based on response status codes
public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    private AuthData authData = null;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public ServerFacade(int serverPort) {
        this.serverUrl = "http://localhost:" + serverPort;

    }

    // Take nothing, clear database, return nothing
    public void clear() throws ResponseException {
        HttpRequest request = buildRequest("DELETE", "/db", null);
        HttpResponse<String> response = sendRequest(request);
        handleResponse(response, null);
    }

    // Take complete UserData, register and log in the user, return AuthData
    public AuthData register(UserData registerBody) throws ResponseException {
        // Note that no "RegisterBody" class exists, because the "UserData" class already fulfills all of its attributes
        HttpRequest request = buildRequest("POST", "/user", registerBody);
        HttpResponse<String> response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    // Take UserData missing email, log in the user, return AuthData
    public AuthData login(LoginBody loginBody) throws ResponseException {
        HttpRequest request = buildRequest("POST", "/session", loginBody);
        HttpResponse<String> response = sendRequest(request);
        return handleResponse(response, AuthData.class);
    }

    // Take String for authToken, logout that active session, return nothing
    public void logout(String authToken) throws ResponseException {
        HttpRequest request = buildRequest("DELETE", "/session", null, authToken);
        HttpResponse<String> response = sendRequest(request);
        handleResponse(response, null);
    }

    // Take String for authToken, discover all available games, return ArrayList<GameData>
    public GameListResponse listGames(String authToken) throws ResponseException {
        HttpRequest request = buildRequest("GET", "/game", null, authToken);
        HttpResponse<String> response = sendRequest(request);
        return handleResponse(response, GameListResponse.class);
    }

    // Take Strings for authToken and gameName, create a new game, return Integer for gameID
    public CreateGameResponse createGame(CreateGameBody createGameBody, String authToken) throws ResponseException {
        HttpRequest request = buildRequest("POST", "/game", createGameBody, authToken);
        HttpResponse<String> response = sendRequest(request);
        return handleResponse(response, CreateGameResponse.class);
    }

    // Take String for authToken, a ChessGame.TeamColor for playerColor and int for gameID, add the player to the game, return nothing
    public void joinGame(JoinBody joinGameBody, String authToken) throws ResponseException {
        HttpRequest request = buildRequest("GET", "/game", joinGameBody, authToken);
        HttpResponse<String> response = sendRequest(request);
        handleResponse(response, null);
    }


    public void setAuthData(AuthData authData) {
        this.authData = authData;
    }
    public void clearAuthData() {
        this.authData = null;
    }

    public AuthData getAuthData() {
        return authData;
    }


    // TODO: Modify all of the methods taking an authToken as a parameter to use the variables within the class.
    private void checkAuthToken() throws ResponseException {
        if (authData == null) {
            throw new ResponseException(StatusReader.ResponseStatus.UNAUTHORIZED, "AuthData is null");
        }
    }

    private HttpRequest buildRequest(String method, String path, Object body) {
        return buildRequest(method, path, body, null);
    }

    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        if (authToken != null) {
            request.header("authorization", authToken);
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
            throw new ResponseException(StatusReader.ResponseStatus.NO_CONNECTION, ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        StatusReader.ResponseStatus responseStatus = StatusReader.getStatusFromCode(response.statusCode());
        String responseBody = response.body();
        if (responseStatus != StatusReader.ResponseStatus.GOOD) {
            if (responseBody != null) {
                record ResponseErrorMessage(String message) {}
                ResponseErrorMessage responseErrorMessage = new Gson().fromJson(responseBody, ResponseErrorMessage.class);
                throw new ResponseException(responseStatus, responseErrorMessage.message);
            }

            throw new ResponseException(responseStatus, "other failure: " + responseStatus);
        }

        if (responseClass != null) {
            return new Gson().fromJson(responseBody, responseClass);
        }

        return null;
    }

}
