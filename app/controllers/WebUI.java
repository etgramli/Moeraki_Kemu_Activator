package controllers;

import javax.inject.Singleton;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import play.mvc.LegacyWebSocket;
import play.data.*;
import static play.data.Form.*;
import play.mvc.Security;
import play.mvc.Http.Context;
import play.libs.F;
import play.libs.Json;
import play.libs.openid.*;
import java.util.HashMap;
import java.util.Map;

import views.html.*;
import actors.LobbyActor;
import actors.WebsocketActor;

import de.htwg.se.moerakikemu.controller.IController;
import de.htwg.se.moerakikemu.MoerakiKemu;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Singleton
public class WebUI extends Controller {
    
	public static ActorRef actorLobby;
    public static ActorRef lobby;
	private static WebsocketController wsController;
	
	private static Map<String, IController> controllers = new HashMap<>();
	
	public WebUI() {
		ActorSystem actorSystem = ActorSystem.create("MoerakiSystem");
		actorLobby = actorSystem.actorOf(LobbyActor.props(actorSystem));
        lobby = actorSystem.actorOf(LobbyActor.props(actorSystem));
		wsController = new WebsocketController(actorSystem);
	}
	
	@play.mvc.Security.Authenticated(Secured.class)
    public Result index() {
        String email = session("email");
        IController controller = controllers.get(email);
        if(null == controller) {
	        session().clear();
	        return this.login();
	    }
        
        return ok(index.render("Moeraki Kemu: Startseite"));
    }
    public Result getManual() {
        return ok(manual.render());
    }
    public Result plainBoard() {
        return ok(plainBoard.render());
    }
    public LegacyWebSocket<String> socket() {
		return WebSocket.withActor(WebsocketActor::props);
    }
    
    public Result login() {
        return ok( views.html.login.render(Form.form(User.class)));
    }
    
    public Result logout() {
    	session().clear();
    	return redirect(routes.WebUI.index());
    }
    
    public  Result authenticate() {
        Form<User> loginform = DynamicForm.form(User.class).bindFromRequest();

        User user = User.authenticate(loginform.get());

        if (loginform.hasErrors() || user == null) {
            ObjectNode response = Json.newObject();
            response.put("success", false);
            response.put("errors", loginform.errorsAsJson());
            if (user == null) {
                flash("errors", "Wrong username or password");
            }

            return badRequest(views.html.login.render(loginform));
        } else {
            session().clear();
            session("email", user.email);
            String email = session("email");
            IController controller = controllers.get(email);
            controllers.put(email,controller );
            return redirect(routes.WebUI.index());
        }
    }
    
    public static class User {
        private final static String defaultUser = "test@123.de";
        private final static String defaultPasswort = "nsa";

        public String email;
        public String password;
        
        public User() {}
  
        private User(final String email, final String password) {
            this.email = email;
            this.password = password;
        }
        
        public static User authenticate(User user){
            if (user.email.equals(defaultUser) && user.password.equals(defaultPasswort)) {
                return new User(user.email, user.password);
   	        }
 
    	    return null;
    	}
    }
    
    
    public static class Secured extends Security.Authenticator {
        @Override
        public String getUsername(Context ctx) {
            return ctx.session().get("email");
        }

        @Override
        public Result onUnauthorized(Context ctx) {
            return redirect(routes.WebUI.login());
        }
    }
}
