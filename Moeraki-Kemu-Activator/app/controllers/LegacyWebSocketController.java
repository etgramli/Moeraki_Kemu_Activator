package controllers;

import play.mvc.LegacyWebSocket;

public class LegacyWebSocketController {
	private StringWebSocket ws;
	
	public LegacyWebSocketController() {
		ws = new StringWebSocket();
	}
	
}
