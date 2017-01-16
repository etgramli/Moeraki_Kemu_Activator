package controllers;

import javax.inject.Singleton;

import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.LegacyWebSocket;

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
    public Result plainBoard(final int gameIdx) {
        return ok(plainBoard.render(gameIdx));
    }
    
	public LegacyWebSocket<String> socket(final int gameIdx) {
		return lobbyActor.getSocket(gameIdx);
	}
	
}
