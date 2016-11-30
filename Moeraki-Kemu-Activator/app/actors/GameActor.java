package actors;

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
import de.htwg.se.moerakikemu.view.viewimpl.WebInterface;
import de.htwg.se.util.observer.IObserverSubject;
import de.htwg.se.util.observer.ObserverObserver;

public class GameActor extends UntypedActor {
	
	private IController controller;
	
	public static Props props(ActorRef out) {
	    return Props.create(GameActor.class, out);
	}

	private final ActorRef out;

	public GameActor(ActorRef out) {
	    this.out = out;
		Injector injector = Guice.createInjector(new ControllerModuleWithController());
		
		IControllerPlayer playerController = new ControllerPlayer();
		controller = new de.htwg.se.moerakikemu.controller.controllerimpl.Controller(8, playerController);
	
		UserInterface tui = injector.getInstance(TextUI.class);

		((IObserverSubject) controller).attatch((ObserverObserver) tui);
		tui.drawCurrentState();
	}
	
	@Override
	public void onReceive(Object arg0) throws Throwable {
		// TODO Auto-generated method stub
	}

}
