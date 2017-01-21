package actors;

import de.htwg.se.moerakikemu.controller.IController;
import de.htwg.se.moerakikemu.controller.IControllerPlayer;
import de.htwg.se.moerakikemu.controller.controllerimpl.Controller;
import de.htwg.se.moerakikemu.controller.controllerimpl.ControllerPlayer;


public final class JsonRenderer {
    
    // Json Symbols
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
    
    
    private static String getPlayer1AsJson(final IController controller) {
    	return JsonEscapeValue(PLAYER1) + ":" + JsonEscapeValue(controller.getPlayer1Name());
    }

    private static String getPlayer2AsJson(final IController controller) {
    	return JsonEscapeValue(PLAYER2) + ":" + JsonEscapeValue(controller.getPlayer2Name());
    }
    
    private static String getPlayer1PointsAsJson(final IControllerPlayer playerController) {
    	return JsonEscapeValue(PLAYER1POINTS) + ":" + playerController.getPlayer1Points();
    }
    
    private static String getPlayer2PointsAsJson(final IControllerPlayer playerController) {
    	return JsonEscapeValue(PLAYER2POINTS) + ":" + playerController.getPlayer2Points();
    }
    
    public static String getBoardAsJSON(final IController controller, final IControllerPlayer playerController) {
        final int boardLength = controller.getEdgeLength();

        StringBuilder json = new StringBuilder(oOpening);
        json.append(getPlayer1AsJson(controller)).append(DELIMITER);
        json.append(getPlayer2AsJson(controller)).append(DELIMITER);
        json.append(getPlayer1PointsAsJson(playerController)).append(DELIMITER);
        json.append(getPlayer2PointsAsJson(playerController)).append(DELIMITER);
        
        json.append(JsonEscapeValue(LINES) + ":");

        json.append(aOPENING).append(NEWLINE);

        for (int i = 0; i < boardLength; i++) {
            json.append(getJSONLine(i, controller));
            json.append(getDelimiterOrEmpty(boardLength, i));
        }
        json.append(aCLOSING);

        return json.append(oCLOSING).toString();
    }

    private static String getJSONLine(final int lineNumber, final IController controller) {
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
