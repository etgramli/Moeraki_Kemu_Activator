package html;

import de.htwg.se.moerakikemu.controller.IController;

public class HtmlBoardBuilder implements HtmlAble {

    /**
     * Controller to query cell state from.
     */
    private final IController controller;

    public HtmlBoardBuilder(IController controller) {
        this.controller = controller;
    }
    
    public String getHtml() {
        return getHtmlBody();
    }

    private String getHtmlBody() {
    	final int length = controller.getEdgeLength();
        StringBuilder htmlCode = new StringBuilder("<body>");
        htmlCode.append("<table style=\"width:100%\">");
        // Render Html
        for (int i = 0; i < length; i++) {
            htmlCode.append("<tr>\n");
            for (int j = 0; j < length; j++) {
                htmlCode.append(getCellHtml(i, j));
            }
            htmlCode.append("</tr>\n");
        }
        return htmlCode.append("</table>").append("<body>").toString();
    }
    
    private String getCellHtml(final int i, final int y) {
    	final String cellBegin = "<th";
    	final String cellEnd = " />\n";
    	final String black = " style=\"background:#000000";
    	final String red   = " style=\"background:#00FF11";
    	final String green = " style=\"background:#FF3300";
        // query state and leave MT or fill with color
    	final String playerName = controller.getIsOccupiedByPlayer(i, y);
    	
    	final String color;
    	if ("StartDot".equals(playerName)) {
    		color = black;
    	} else if (controller.getPlayer1Name().equals(playerName)) {
    		color = red;
    	} else if (controller.getPlayer2Name().equals(playerName)) {
    		color = green;
    	} else if ("".equals(playerName)) {
    		// Not occupied
    		color = "";
    	} else {
    		// Should not happen
    		color = "";
    	}
    	
    	return cellBegin + color + cellEnd;
    }

}
