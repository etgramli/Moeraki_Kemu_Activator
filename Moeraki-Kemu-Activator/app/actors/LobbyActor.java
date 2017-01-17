package actors;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;
import play.mvc.LegacyWebSocket;
import play.mvc.WebSocket;


@Singleton
public class LobbyActor {
    /* Change GameActor to contain to contain only controllers,
     * and return return 1 or 2 WebSocketActors on Request for each player.
     */
    private List<LegacyWebSocket<String>> games;
    
    public LobbyActor() {
        games = new ArrayList<LegacyWebSocket<String>>();
    }
    
    public LegacyWebSocket<String> getSocket(final int idx) {
        if (idx < 0 || idx >= games.size()) {
            return null;
        } else {
            return games.get(idx);
        }
    }
    
    public int startGame() {
        games.add(WebSocket.withActor(GameActor::props));
        return games.size() - 1;
    }
    
    public int getNumberOfGames() {
        return games.size();
    }
}