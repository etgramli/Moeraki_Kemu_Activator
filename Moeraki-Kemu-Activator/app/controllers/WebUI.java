package controllers;

import javax.inject.Singleton;

import com.google.inject.Guice;
import com.google.inject.Injector;

import de.htwg.se.moerakikemu.controller.ControllerModuleWithController;
import de.htwg.se.moerakikemu.controller.IController;
import de.htwg.se.moerakikemu.controller.IControllerPlayer;
import de.htwg.se.moerakikemu.controller.controllerimpl.ControllerPlayer;
import de.htwg.se.moerakikemu.view.UserInterface;
import de.htwg.se.moerakikemu.view.viewimpl.TextUI;
import de.htwg.se.util.observer.IObserverSubject;
import de.htwg.se.util.observer.ObserverObserver;
import play.mvc.*;
import views.html.*;

@Singleton
public class WebUI extends Controller {
	
	private IController controller = null;
	
	public String newGame(final String player1Name, final String player2Name) {
		// Create new game
		startGame();
		return "";
	}

    public Result index() {
        return ok(index.render("Moeraki Kemu: Startseite"));
    }
    public Result getManual() {
        return ok(manual.render());
    }
    
	public Result drawBoard() {
		if (controller != null) {
			return ok(board.render("Moeraki Kemu", controller));
		} else {
			return ok("Game not started yet !!!"); // TODO: make it an error
		}
	}
	
	public Result startGame() {
		Injector injector = Guice.createInjector(new ControllerModuleWithController());
		
		IControllerPlayer playerController = new ControllerPlayer();
		controller = new de.htwg.se.moerakikemu.controller.controllerimpl.Controller(8, playerController);
	
		UserInterface tui = injector.getInstance(TextUI.class);

		((IObserverSubject) controller).attatch((ObserverObserver) tui);
		tui.drawCurrentState();

		return ok("Game created");
	}
}
