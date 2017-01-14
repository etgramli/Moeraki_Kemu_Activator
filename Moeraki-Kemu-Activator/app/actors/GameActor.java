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

public class GameActor {
	
	private IController controller = null;
	private IControllerPlayer playerController = null;
	private List<LegacyWebSocket<String>> websockets;
	
	
	public GameActor() {
		Injector injector = Guice.createInjector(new ControllerModuleWithController());
		
		playerController = new ControllerPlayer();
		controller = new de.htwg.se.moerakikemu.controller.controllerimpl.Controller(8, playerController);
		
		List<UserInterface> interfaces = new ArrayList<>(3);
		interfaces.add(injector.getInstance(TextUI.class));
		interfaces.add(new GUI(controller, playerController));
		
		websockets = new ArrayList<LegacyWebSocket<String>>(2);
	}
	
	public LegacyWebSocket<String> getWebSocket() {
	    final int size = websockets.size();
	    switch (size) {
	        case 0:
	        case 1:
	            websockets.add(WebSocket.withActor(WebsocketEndpointActor::props));
	            return websockets.get(size);
	        default:
	            return null;
	    }
	}
}
