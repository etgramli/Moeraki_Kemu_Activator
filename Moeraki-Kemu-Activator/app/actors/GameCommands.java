package actors;

import akka.actor.ActorRef;


public final class GameCommands {
	
	public static final class StartGame {
		private final ActorRef game;
		public StartGame(final ActorRef game){
			this.game = game;
		}
		public ActorRef getGame() {
			return this.game;
		}
	}
	public static final class JoinCommand {}
	
	public static final class SetDotCommand {
		private final String coords;
		
		public SetDotCommand(final String coords) {
			this.coords = coords;
		}
		public String getCoors() {
			return coords;
		}
	}
	
	public static final class UpdateCommand {
		private final String json;
		
		public UpdateCommand(final String json) {
			this.json = json;
		}
		public String getJson() {
			return this.json;
		}
	}
	
}
