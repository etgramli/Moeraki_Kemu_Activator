package actors;

import java.util.Random;

import com.google.inject.Guice;
import com.google.inject.Injector;

import akka.actor.*;
import de.htwg.se.moerakikemu.controller.ControllerModuleWithController;
import de.htwg.se.moerakikemu.controller.IController;
import de.htwg.se.moerakikemu.controller.IControllerPlayer;
import de.htwg.se.moerakikemu.controller.controllerimpl.Controller;
import de.htwg.se.moerakikemu.controller.controllerimpl.ControllerPlayer;
import de.htwg.se.moerakikemu.view.UserInterface;
import de.htwg.se.moerakikemu.view.viewimpl.TextUI;
import de.htwg.se.moerakikemu.view.viewimpl.gui.GUI;
import de.htwg.se.moerakikemu.view.viewimpl.WebInterface;
import de.htwg.se.util.observer.IObserverSubject;
import de.htwg.se.util.observer.ObserverObserver;

public class GameActor extends UntypedActor {
	
	private static String SET_COMMAND = "setDot";
	private static int SET_COMMAND_LENGTH = SET_COMMAND.length();
	
	private IController controller = null;
	private IControllerPlayer playerController = null;
	private WebInterface webinterface = null;
	
	public static Props props(ActorRef out) {
	    return Props.create(GameActor.class, out);
	}

	private final ActorRef out;

	public GameActor(ActorRef out) {
	    this.out = out;
		Injector injector = Guice.createInjector(new ControllerModuleWithController());
		
		playerController = new ControllerPlayer();
		controller = new de.htwg.se.moerakikemu.controller.controllerimpl.Controller(8, playerController);
		webinterface = new WebInterface(controller);
		
		UserInterface tui = injector.getInstance(TextUI.class);
		UserInterface gui = new GUI(controller, playerController);

		((IObserverSubject) controller).attatch((ObserverObserver) tui);
		((IObserverSubject) controller).attatch((ObserverObserver) gui);
		tui.drawCurrentState();
	}
	
	@Override
	public void onReceive(Object msg) throws Throwable {
		if (msg instanceof String) {
			final String msgString = (String) msg;
			System.out.println("Actor got message over Websocket: " + msgString);
			if (msgString.startsWith(SET_COMMAND) && msgString.length() > SET_COMMAND_LENGTH) {
				webinterface.occupyAndGetBoard(msgString.substring(SET_COMMAND_LENGTH + 1, msgString.length()-1));
			}
		}
		if (controller.testIfWinnerExists()) {
			out.tell("winner(" + controller.getWinner() + ")", self());
		}
		out.tell(webinterface.getBoardAsJSON(), self());
	}

	void endGame() {
		if (new Random().nextBoolean()) {
			System.out.println("Ostereier suchen...");
		}
		self().tell(PoisonPill.getInstance(), self());
	}
	
}
