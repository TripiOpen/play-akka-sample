package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import org.reactivestreams.Publisher;
import play.mvc.Controller;
import play.mvc.Result;
import scala.compat.java8.FutureConverters;
import services.booking.BookingService;
import utils.ResponseFactory;

import javax.inject.Named;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class BookingController extends Controller {

    /**
     * Recommend using final because you need remember do not recreate instance for services
     */
    private final BookingService<ObjectNode> bookingSync;
    private final BookingService<CompletionStage<ObjectNode>> bookingAsync;

    @Inject
    public BookingController(@Named("bookingAsync") BookingService bookingAsync,
                             @Named("bookingSync") BookingService bookingSync) {
        this.bookingSync = bookingSync;
        this.bookingAsync = bookingAsync;
    }

    public Result bookingSync() {
        return ok(ResponseFactory.createResponse(bookingSync.bookingTicket(), true));
    }

    public CompletionStage<Result> bookingAsync() {
        CompletableFuture<Result> future = new CompletableFuture<>();
        bookingAsync.bookingTicket().whenComplete((ObjectNode jsonData, Throwable throwable) -> {
            if (throwable == null) {
                future.complete(ok(ResponseFactory.createResponse(jsonData, true)));
            } else {
                future.complete(ok(ResponseFactory.createResponse(false)));
            }
        });
        return future;
    }

    public CompletionStage<Result> bookingReactiveWithAkka() {

        return null;
    }

    public CompletionStage<Result> bookingConcurrentWithAkka() {

        return null;
    }
}
