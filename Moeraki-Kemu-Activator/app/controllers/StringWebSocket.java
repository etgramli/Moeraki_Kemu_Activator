package controllers;

import play.mvc.LegacyWebSocket;
import play.mvc.WebSocket;
import play.mvc.WebSocket.In;
import play.mvc.WebSocket.Out;

public class StringWebSocket extends LegacyWebSocket<String> {

	private WebSocket.In<String> in;
	private WebSocket.Out<String> out;
	
	@Override
	public void onReady(In<String> arg0, Out<String> arg1) {
		// TODO Auto-generated method stub
		this.in = arg0;
		this.out = arg1;
	}
	
	public void send(final String msg) {
		out.write(msg);
	}

}
