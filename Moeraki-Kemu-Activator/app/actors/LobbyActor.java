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
    }
}