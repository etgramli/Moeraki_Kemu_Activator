package controllers;

import actors.LobbyActor;
import actors.WebsocketActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import play.mvc.Controller;
import play.mvc.LegacyWebSocket;
import play.mvc.Result;
import play.mvc.WebSocket;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class WebsocketController extends Controller {

    public static ActorRef lobby;

    @Inject
    public WebsocketController(ActorSystem actorSystem) {
        lobby = actorSystem.actorOf(LobbyActor.props(actorSystem));
    }

    public Result strategoWui() {
        return ok(views.html.plainBoard.render(0));
    }

    public LegacyWebSocket<String> socket() {
        return WebSocket.withActor(WebsocketActor::props);
    }

}