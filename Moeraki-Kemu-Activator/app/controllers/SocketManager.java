package controllers;

import java.util.*;
import play.mvc.*;
import play.libs.*;
import play.libs.F.*;

public class SocketManager {

	private static List<WebSocket.In<String>> inSockets = new ArrayList<>();
	private static List<WebSocket.Out<String>> outSockets = new ArrayList<>();
	
	public static void start(WebSocket.In<String> in, WebSocket.Out<String> out) {
		inSockets.add(in);
		outSockets.add(out);
		
		in.onMessage(event -> System.out.println("Got: " + event));
		in.onClose(() -> System.out.println("Connection closed"));
	}
}
