package controllers;

import akka.actor.ActorSystem;
import javax.inject.*;

import play.libs.concurrent.HttpExecutionContext;
import play.mvc.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import scala.concurrent.ExecutionContextExecutor;

/**
 * This controller contains an action that demonstrates how to write
 * simple asynchronous code in a controller. It uses a timer to
 * asynchronously delay sending a response for 1 second.
 */
@Singleton
public class AsyncController extends Controller {

    private final ActorSystem actorSystem;
    private final ExecutionContextExecutor exec;
    private final HttpExecutionContext httpExec;

    @Inject
    public AsyncController(ActorSystem actorSystem, ExecutionContextExecutor exec, HttpExecutionContext httpExec) {
      this.actorSystem = actorSystem;
      this.exec = exec;
      this.httpExec = httpExec;
    }

    public CompletionStage<Result> usingHttpContext() {
        // Use a different task with explicit EC
        return calculateResponse().thenApplyAsync((String answer) -> {
                ctx().flash().put("info", "Response updated!");
                return ok("answer was " + answer);
        }, httpExec.current());
    }

    private static CompletionStage<String> calculateResponse() {
        return CompletableFuture.completedFuture("42");
    }

    public CompletionStage<Result> message() {
        return getFutureMessage(1, TimeUnit.SECONDS).thenApplyAsync(Results::ok, exec);
    }

    private CompletionStage<String> getFutureMessage(long time, TimeUnit timeUnit) {
        CompletableFuture<String> future = new CompletableFuture<>();
        actorSystem.scheduler().scheduleOnce(
            Duration.create(time, timeUnit),
            () -> future.complete("Hi!"),
            exec
        );
        return future;
    }

}
