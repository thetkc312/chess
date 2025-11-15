package endpointresponses;

import model.GameData;
import java.util.ArrayList;

public record GameListResponse (ArrayList<GameData> games) {
}
