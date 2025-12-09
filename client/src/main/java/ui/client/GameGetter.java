package ui.client;

import endpointresponses.GameListResponse;
import model.GameData;
import server.ResponseException;
import server.ServerFacade;
import server.StatusReader;

public class GameGetter {

    public static GameData getGameData(int activeGameID, ServerFacade serverFacade) throws ResponseException {
        GameListResponse gameListResponse = serverFacade.listGames(serverFacade.getAuthData().authToken());
        GameData gameData = gameListResponse.findGameData(activeGameID);
        if (gameData == null) {
            throw new ResponseException(StatusReader.ResponseStatus.BAD_REQUEST, "No game with the provided gameID could be located.");
        }
        return gameData;
    }

    public static String formatGameData(GameData gameData) {
        String uiGameData = "";
        uiGameData += gameData.gameName();
        uiGameData += ": White Team - ";
        uiGameData += representPlayerName(gameData.whiteUsername());
        uiGameData += " | Black Team - ";
        uiGameData += representPlayerName(gameData.blackUsername());
        return uiGameData;
    }

    private static String representPlayerName(String playerName) {
        if (playerName == null) {
            return "_____";
        }
        return playerName;
    }
}
