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
    private static final String PLAYER1 = "player1";
    private static final String PLAYER2 = "player2";
    private static final String PLAYER1POINTS = "player1Points";
    private static final String PLAYER2POINTS = "player2Points";
    private static final String LINES = "lines";
    private static final String CELLS = "cells";
    
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
		out.tell(getBoardAsJSON(), self());
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
		out.tell(getBoardAsJSON(), self());
		
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
	
    private String getPlayer1AsJson() {
    	return JsonEscapeValue(PLAYER1) + ":" + JsonEscapeValue(controller.getPlayer1Name());
    }

    private String getPlayer2AsJson() {
    	return JsonEscapeValue(PLAYER2) + ":" + JsonEscapeValue(controller.getPlayer2Name());
    }
    
    private String getPlayer1PointsAsJson() {
    	return JsonEscapeValue(PLAYER1POINTS) + ":" + playerController.getPlayer1Points();
    }
    
    private String getPlayer2PointsAsJson() {
    	return JsonEscapeValue(PLAYER2POINTS) + ":" + playerController.getPlayer2Points();
    }
    
    public String getBoardAsJSON() {
        final int boardLength = controller.getEdgeLength();

        StringBuilder json = new StringBuilder(oOpening);
        json.append(getPlayer1AsJson()).append(DELIMITER);
        json.append(getPlayer2AsJson()).append(DELIMITER);
        json.append(getPlayer1PointsAsJson()).append(DELIMITER);
        json.append(getPlayer2PointsAsJson()).append(DELIMITER);
        
        json.append(JsonEscapeValue(LINES) + ":");

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
