package actors;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.inject.Guice;
import com.google.inject.Injector;

import play.mvc.*;
import akka.actor.*;
import de.htwg.se.moerakikemu.controller.ControllerModuleWithController;
import de.htwg.se.moerakikemu.controller.IController;
import de.htwg.se.moerakikemu.controller.IControllerPlayer;
import de.htwg.se.moerakikemu.controller.controllerimpl.Controller;
import de.htwg.se.moerakikemu.controller.controllerimpl.ControllerPlayer;
import de.htwg.se.moerakikemu.view.UserInterface;
import de.htwg.se.moerakikemu.view.viewimpl.TextUI;
import de.htwg.se.moerakikemu.view.viewimpl.gui.GUI;
import de.htwg.se.util.observer.IObserverSubject;
import de.htwg.se.util.observer.ObserverObserver;

public class GameActor extends UntypedActor implements UserInterface, ObserverObserver {
	private static String SET_COMMAND = "setDot";
	private static int SET_COMMAND_LENGTH = SET_COMMAND.length();
	
	private IController controller = null;
	private IControllerPlayer playerController = null;
	
    
	public static Props props(ActorRef out) {
	    return Props.create(GameActor.class, out);
	}

	private final ActorRef out;
	
	public GameActor(ActorRef out) {
	    this.out = out;
		Injector injector = Guice.createInjector(new ControllerModuleWithController());
		
		playerController = new ControllerPlayer();
		controller = new de.htwg.se.moerakikemu.controller.controllerimpl.Controller(8, playerController);
		
		List<UserInterface> interfaces = new ArrayList<>(3);
		interfaces.add(injector.getInstance(TextUI.class));
		interfaces.add(new GUI(controller, playerController));
		interfaces.add(this);
		
		for (UserInterface iface : interfaces) {
			((IObserverSubject) controller).attatch((ObserverObserver) iface);
        }
	}
	
	@Override
	public void onReceive(Object msg) {
		if (msg instanceof String) {
			final String msgString = (String) msg;
			
			if (msgString.startsWith(SET_COMMAND) && msgString.length() > SET_COMMAND_LENGTH) {
				occupyAndGetBoard(msgString.substring(SET_COMMAND_LENGTH + 1, msgString.length()-1));
			}
		}
		out.tell(JsonRenderer.getBoardAsJSON(controller, playerController), self());
	}

	@Override
	public void addPoints(int arg0, int arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void drawCurrentState() {
		// TODO Auto-generated method stub
	}

	@Override
	public void printMessage(String arg0) {
		//
	}

	@Override
	public void queryPlayerName() {
		// TODO Auto-generated method stub
	}

	@Override
	public void quit() {
		// TODO Auto-generated method stub
	}

	@Override
	public void update() {
		out.tell(JsonRenderer.getBoardAsJSON(controller, playerController), self());
		
	}

    public String occupyAndGetBoard(final String coordinates) {
		int []ij = splitXY(coordinates);
    	controller.occupy(ij[0], ij[1]);
    	return JsonRenderer.getBoardAsJSON(controller, playerController);
    }

	/**
	 * Takes a parameter (from AJAX call) and extracts the x and y coordinate.
	 *
	 * @param param String that must match the pattern [0-9]+/[0-9]*
	 * @return An array of int with the length of 2.
	 */
	public static final int[] splitXY(final String param) {
	    final int idx = param.indexOf("-");
	    return new int[] {Integer.parseInt(param.substring(0, idx)), 
	    				  Integer.parseInt(param.substring(idx + 1))};
	}

}
