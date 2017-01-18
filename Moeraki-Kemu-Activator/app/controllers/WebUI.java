package controllers;

import javax.inject.Singleton;

import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.LegacyWebSocket;

import views.html.*;
import actors.LobbyActor;

@Singleton
public class WebUI extends Controller {
    
	
	public Result lobby() {
	    return ok(lobby.render(0));
	}
	
    public Result index() {
        return ok(index.render("Moeraki Kemu: Startseite"));
    }
    public Result getManual() {
        return ok(manual.render());
    }
    public Result plainBoard(final int gameIdx) {
        if (gameIdx >= 0) {
            return ok(plainBoard.render(gameIdx));
        } else if (true) {
            return ok(plainBoard.render(gameIdx));
        } else {
            return ok(lobby.render(0));
        }
    }
    
}
