package controllers;

import javax.inject.Singleton;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import play.mvc.LegacyWebSocket;

import views.html.*;
import actors.LobbyActor;
import actors.WebsocketActor;

@Singleton
public class WebUI extends Controller {
    
	public static ActorRef actorLobby;
    public static ActorRef lobby;
	private static WebsocketController wsController;
	
	public WebUI() {
		ActorSystem actorSystem = ActorSystem.create("MoerakiSystem");
		actorLobby = actorSystem.actorOf(LobbyActor.props(actorSystem));
        lobby = actorSystem.actorOf(LobbyActor.props(actorSystem));
		wsController = new WebsocketController(actorSystem);
	}
	
    public Result index() {
        return ok(index.render("Moeraki Kemu: Startseite"));
    }
    public Result getManual() {
        return ok(manual.render());
    }
    public Result plainBoard() {
        return ok(plainBoard.render());
    }
    public LegacyWebSocket<String> socket() {
		return WebSocket.withActor(WebsocketActor::props);
    }
}
