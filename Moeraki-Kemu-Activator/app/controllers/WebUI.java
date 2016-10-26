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
	
	IController controller = null;
	
	public String newGame(final String player1Name, final String player2Name) {
		// Create new game
		startGame();
		return "";
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
	
		UserInterface[] interfaces = new UserInterface[2];
		interfaces[0] = injector.getInstance(TextUI.class);
		//interfaces[1] = injector.getInstance(GUI.class);

		for (int i = 0; i < 1/*interfaces.length*/; i++) {
			((IObserverSubject) controller).attatch((ObserverObserver) interfaces[i]);
			interfaces[i].drawCurrentState();
		}

		// Used to query Player names
		//((ObserverObserver) interfaces[1]).update();
		
		boolean finished = false;
		while (!finished) {
			finished = controller.testIfEnd();
			interfaces[0].update();
			//interfaces[1].update();
		}
		
		for (UserInterface ui : interfaces) {
			ui.quit();
		}
		
		return ok("Game created");
	}
}
