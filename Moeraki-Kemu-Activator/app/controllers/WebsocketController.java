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


    @Inject
    public WebsocketController(ActorSystem actorSystem) {
    }

}