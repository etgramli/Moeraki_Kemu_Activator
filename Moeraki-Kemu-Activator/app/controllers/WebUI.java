package controllers;

import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Guice;
import com.google.inject.Injector;

import com.fasterxml.jackson.databind.JsonNode;

import de.htwg.se.moerakikemu.controller.ControllerModuleWithController;
import de.htwg.se.moerakikemu.controller.IController;
import de.htwg.se.moerakikemu.controller.IControllerPlayer;
import de.htwg.se.moerakikemu.controller.controllerimpl.ControllerPlayer;
import de.htwg.se.moerakikemu.view.UserInterface;
import de.htwg.se.moerakikemu.view.viewimpl.TextUI;
import de.htwg.se.moerakikemu.view.viewimpl.WebInterface;
import de.htwg.se.util.observer.IObserverSubject;
import de.htwg.se.util.observer.ObserverObserver;
import play.mvc.*;
import services.AjaxHelper;
import views.html.*;

@Singleton
public class WebUI extends Controller {
	
	private IController controller = null;
	private WebInterface webInterface = null;
	
	
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
			return notFound("Game not started yet !!!"); // TODO: make it an error
		}
	}
	
	public Result startGame() {
		Injector injector = Guice.createInjector(new ControllerModuleWithController());
		
		IControllerPlayer playerController = new ControllerPlayer();
		controller = new de.htwg.se.moerakikemu.controller.controllerimpl.Controller(8, playerController);
		webInterface = new WebInterface(controller);
	
		UserInterface tui = injector.getInstance(TextUI.class);

		((IObserverSubject) controller).attatch((ObserverObserver) tui);
		tui.drawCurrentState();

		return redirect("/board");
	}

	@BodyParser.Of(BodyParser.Json.class)
	public Result setDot() {
		JsonNode json = request().body().asJson();
		
		System.out.println("Text got from POST: " + json.asText());
		
		int []ij = AjaxHelper.splitXY(json.asText());
		
		System.out.println("Coordinates: (" + ij[0] + "/" + ij[1] + ")");
		
		
		return occupyAndGetBoard(ij[0], ij[1]);
	}
	
	private Result occupyAndGetBoard(final int x, final int y) {
		final int retVal = controller.occupy(x, y);
		final String board = webInterface.getBoardAsJSON();
		return ok(board);
	}

}
