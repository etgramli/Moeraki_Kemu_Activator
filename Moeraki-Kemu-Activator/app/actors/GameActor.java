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

import actors.GameCommands.SetDotCommand;
import actors.GameCommands.UpdateCommand;

public class GameActor extends UntypedActor implements UserInterface, ObserverObserver {
	
	private IController controller = null;
	private IControllerPlayer playerController = null;
	

    public static Props props(ActorRef playerOne, ActorRef playerTwo) {
        return Props.create(GameActor.class, playerOne, playerTwo);
    }

	private final ActorRef player1;
	private final ActorRef player2;
	
	public GameActor(ActorRef player1, ActorRef player2) {
	    this.player1 = player1;
	    this.player2 = player2;
	    
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
		if (msg instanceof SetDotCommand) {
			final SetDotCommand commandMsg = (SetDotCommand) msg;
			occupyAndGetBoard(commandMsg.getCoors());
		}
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
		final UpdateCommand newBoard = new UpdateCommand(JsonRenderer.getBoardAsJSON(controller, playerController));
		player1.tell(newBoard, self());
		player2.tell(newBoard, self());
		
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
