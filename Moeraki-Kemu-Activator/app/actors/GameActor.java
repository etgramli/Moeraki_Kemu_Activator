package actors;

import java.util.ArrayList;
import java.util.List;
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

public class GameActor extends UntypedActor implements UserInterface, ObserverObserver {
	
    private static final String aOPENING = "[";
    private static final String aCLOSING = "]";
    private static final String oOpening = "{";
    private static final String oCLOSING = "}";
    private static final String NEWLINE = "\n";
    private static final String DELIMITER = ", ";
    private static final String EMPTYSTRING = "";
    private static final String DQUOTES = "\"";
    private static final String LINES = "lines";
    private static final String CELLS = "cells";
    
    
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
		
		List<UserInterface> interfaces = new ArrayList<>(3);
		interfaces.add(injector.getInstance(TextUI.class));
		interfaces.add(new GUI(controller, playerController));
		interfaces.add(this);
		
		for (UserInterface iface : interfaces) {
			((IObserverSubject) controller).attatch((ObserverObserver) iface);
		}
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
	
	
	
	//////// Override methods from UserInterface and PbserverObserver ////////

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
		out.tell(webinterface.getBoardAsJSON(), self());
		
	}

	
	
	//////// Build the JSON of the board ////////
	
    public String occupyAndGetBoard(final String coordinates) {
		int []ij = splitXY(coordinates);
    	controller.occupy(ij[0], ij[1]);
    	return getBoardAsJSON();
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
	
    public String getBoardAsJSON() {
        final String linesObject = JsonEscapeValue(LINES) + ":";
        final int boardLength = controller.getEdgeLength();

        StringBuilder json = new StringBuilder(oOpening);
        json.append(linesObject);

        json.append(aOPENING).append(NEWLINE);

        for (int i = 0; i < boardLength; i++) {
            json.append(getJSONLine(i));
            json.append(getDelimiterOrEmpty(boardLength, i));
        }
        json.append(aCLOSING);

        return json.append(oCLOSING).toString();
    }

    private String getJSONLine(final int lineNumber) {
        final String cellsObject = JsonEscapeValue(CELLS) + ":";
    	final int boardLength = controller.getEdgeLength();
    	
    	StringBuilder line = new StringBuilder(oOpening);
    	line.append(cellsObject);
    	
    	line.append(aOPENING);
    	for (int j = 0; j < boardLength; j++) {
    		line.append(JsonEscapeValue(controller.getIsOccupiedByPlayer(lineNumber, j)));
    		line.append(getDelimiterOrEmpty(boardLength, j));
    	}
    	line.append(aCLOSING).append(NEWLINE);
    	
    	return line.append(oCLOSING).toString();
    }
    
    private static String getDelimiterOrEmpty(final int edgeLength, final int pos) {
        return pos < edgeLength - 1 ? DELIMITER : EMPTYSTRING;
    }

    private static String JsonEscapeValue(final String value) {
        return DQUOTES + value + DQUOTES;
    }

}
