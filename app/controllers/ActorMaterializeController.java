package controllers;

import actors.MaterializeActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import play.mvc.Controller;
import play.mvc.Result;
import scala.compat.java8.FutureConverters;
import utils.ResponseFactory;

import java.util.concurrent.CompletionStage;

import static akka.pattern.Patterns.ask;

@Singleton
public class ActorMaterializeController extends Controller {

    private final ActorRef actorRef;

    @Inject
    public ActorMaterializeController(ActorSystem system) {
        this.actorRef = system.actorOf(MaterializeActor.getProps());
    }

    public CompletionStage<Result> getHello() {
        return FutureConverters.toJava(ask(actorRef, hello(), 500))
                .thenApply((Object obj) -> ok(ResponseFactory.createResponse(obj, true))
        ).toCompletableFuture();
    }

    private String hello() {
        return "Deverlex Supervisor";
    }
}
