package actors;

import akka.actor.*;
import controllers.WebUI;
import controllers.WebsocketController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class WebsocketActor extends UntypedActor {
	private static String SET_COMMAND = "setDot";
	private static int SET_COMMAND_LENGTH = SET_COMMAND.length();
	
	public static Props props(ActorRef out) {
	    return Props.create(WebsocketActor.class, out);
	}


    private final ActorRef out;     // Websocket connection
    private final ActorRef lobby;   // The Lobby to join a game
    private ActorRef game;          // The container for the Controller

	
	public WebsocketActor(ActorRef out) {
	    this.out = out;
        this.lobby = WebUI.lobby;
        lobby.tell(new GameCommands.JoinCommand(), self());
	}
	
	@Override
	public void onReceive(Object msg) {
		if (msg instanceof String) {
			final String msgString = (String) msg;
			
			if (msgString.startsWith(SET_COMMAND) && msgString.length() > SET_COMMAND_LENGTH) {
				final String coords = msgString.substring(SET_COMMAND_LENGTH + 1, msgString.length()-1);
				game.tell(new GameCommands.SetDotCommand(coords), self());
			}
		} else if (msg instanceof GameCommands.UpdateCommand) {
			GameCommands.UpdateCommand updateCommand = (GameCommands.UpdateCommand) msg;
			out.tell(updateCommand.getJson(), self());
		} else if (msg instanceof GameCommands.StartGame) {
			GameCommands.StartGame game = (GameCommands.StartGame) msg;
			this.game = game.getGame();
		} else {
			// Unrecognized message
		}
	}

}