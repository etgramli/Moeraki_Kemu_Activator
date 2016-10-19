package controllers;

import javax.inject.Singleton;

import com.google.inject.Guice;
import com.google.inject.Injector;

import de.htwg.se.moerakikemu.controller.ControllerModuleWithController;
import de.htwg.se.moerakikemu.controller.IController;
import de.htwg.se.moerakikemu.view.UserInterface;
import de.htwg.se.moerakikemu.view.viewimpl.TextUI;
import de.htwg.se.util.observer.IObserverSubject;
import de.htwg.se.util.observer.ObserverObserver;
import html.HtmlBoardBuilder;
import play.mvc.*;

@Singleton
public class WebUI extends Controller {
	
	IController controller = null;
	HtmlBoardBuilder htmlBuilder = null;
	
	public String newGame(final String player1Name, final String player2Name) {
		// Create new game
		startGame();
		return "";
	}
	
	public Result drawBoard() {
		if (controller != null) {
			return ok(htmlBuilder.getHtml());
		} else {
			return ok("Game not started yet !!!"); // TODO: make it an error
		}
	}
	
	private void startGame() {
		Injector injector = Guice.createInjector(new ControllerModuleWithController());
		
		controller = injector.getInstance(de.htwg.se.moerakikemu.controller.controllerimpl.Controller.class);
		htmlBuilder = new HtmlBoardBuilder(controller);
	
		UserInterface[] interfaces = new UserInterface[2];
		interfaces[0] = injector.getInstance(TextUI.class);
		//interfaces[1] = injector.getInstance(GUI.class);

		for (int i = 0; i < interfaces.length; i++) {
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
	}
}
