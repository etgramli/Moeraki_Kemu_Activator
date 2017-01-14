package controllers;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.CompletionStage;

import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Guice;
import com.google.inject.Injector;

import actors.GameActor;
import akka.stream.javadsl.Flow;

import com.fasterxml.jackson.databind.JsonNode;

import play.http.websocket.Message;
import play.libs.F.Either;
import play.*;
import play.mvc.*;
import play.mvc.Http.RequestHeader;

import views.html.*;
import actors.LobbyActor;

@Singleton
public class WebUI extends Controller {
    
    private LobbyActor lobbyActor = new LobbyActor();
	
	public Result lobby() {
	    return ok(lobby.render(lobbyActor.getNumberOfGames()));
	}
	
    public Result index() {
        return ok(index.render("Moeraki Kemu: Startseite"));
    }
    public Result getManual() {
        return ok(manual.render());
    }
    public Result plainBoard() {
        return ok(plainBoard.render("Moeraki Kemu"));
    }
    
	// Return Websocket for callbacks
	public LegacyWebSocket<String> socket() {
		return null;//WebSocket.withActor(GameActor::props);
	}
	
}
