package endpointresponses;

import model.GameData;
import java.util.ArrayList;

public record GameListResponse (ArrayList<GameData> games) {

    public GameData findGameData(int gameID) {
        for (GameData gameData : games) {
            if (gameData.gameID() == gameID) {
                return gameData;
            }
        }
        return null;
    }
}
