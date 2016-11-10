package controllers;

import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;

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
import services.AjaxHelper;
import views.html.*;

@Singleton
public class WebUI extends Controller {
	
	private IController controller = null;
	
	
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
	
		UserInterface tui = injector.getInstance(TextUI.class);

		((IObserverSubject) controller).attatch((ObserverObserver) tui);
		tui.drawCurrentState();

		return redirect("/board");
	}
	
	public Result setDot(final int x, final int y) {
	    final int returnValue = controller.occupy(x, y);
	    // Build JSON
	    return ok(getBoardAsJSON()); // Return JSON
	}
	
	private static final String OPENING = "[";
	private static final String CLOSING = "]";
	private static final String NEWLINE = "\n";
	
	private String getBoardAsJSON() {
		final int boardLength = controller.getEdgeLength();
		
		StringBuilder json = new StringBuilder();
		json.append(OPENING).append(NEWLINE);
		
		for (int i = 0; i < boardLength; i++) {
			json.append(OPENING);
			for (int j = 0; j < boardLength; j++) {
				json.append(AjaxHelper.JsonEscapeValue(controller.getIsOccupiedByPlayer(i, j)));
				json.append(delimiter(boardLength, j));
			}
			json.append(CLOSING).append(NEWLINE);
			json.append(delimiter(boardLength, i));
		}
		json.append(CLOSING);
		
		return json.toString();
	}
	
	private String delimiter(final int edgeLength, final int pos) {
		return pos < edgeLength - 1 ? AjaxHelper.JSONARRAYDELIMITER : StringUtils.EMPTY;
	}
	
}
