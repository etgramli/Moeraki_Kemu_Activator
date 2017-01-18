package actors;

import akka.actor.*;

import java.util.List;
import java.util.ArrayList;

public class LobbyActor extends UntypedActor {

    public static Props props(ActorSystem actorSystem) {
        return Props.create(LobbyActor.class, actorSystem);
    }

    private ActorSystem actorSystem;

    private List<ActorRef> clients = new ArrayList<>();

    public LobbyActor(ActorSystem actorSystem) {
        this.actorSystem = actorSystem;
    }

    public void onReceive(Object message) throws Exception {
    	if (message instanceof GameCommands.JoinCommand) {
    		clients.add(sender());
    		if(clients.size() >= 2) {
    			ActorRef player1 = clients.get(0);
    			ActorRef player2 = clients.get(1);
    			ActorRef game = actorSystem.actorOf(GameActor.props(player1, player2));
    			GameCommands.StartGame newGame = new GameCommands.StartGame(game);
    			player1.tell(newGame, self());
    			player2.tell(newGame, self());
    		}
    	}
    }
}