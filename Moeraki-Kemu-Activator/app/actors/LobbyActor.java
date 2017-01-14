package actors;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;
import play.mvc.LegacyWebSocket;


@Singleton
public class LobbyActor {
    /* Change GameActor to contain to contain only controllers,
     * and return return 1 or 2 WebSocketActors on Request for each player.
     */
    private List<GameActor> games;
    
    public LobbyActor() {
        games = new ArrayList<GameActor>();
    }
    
    public LegacyWebSocket<String> getSocket(final int idx) {
        // if (idx < 0 || idx >= games.length())
        // return games.get(idx). getSocket();
        return null;
    }
    
    public int startGame() {
        //games.append(new GameActor());
        //return games.size() - 1;
        return 0;
    }
    
    public int getNumberOfGames() {
        return games.size();
    }
}