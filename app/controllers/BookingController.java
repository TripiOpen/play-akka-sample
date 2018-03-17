package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import play.mvc.Controller;
import play.mvc.Result;
import services.booking.BookingService;
import utils.ResponseFactory;

import javax.inject.Named;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public class BookingController extends Controller {

    /**
     * Recommend using final because you need remember do not recreate instance for services
     */
    private final BookingService<ObjectNode> bookingSync;
    private final BookingService<CompletionStage<ObjectNode>> bookingAsync;
    private final BookingService<CompletionStage<ObjectNode>> bookingAkka;
    private final BookingService<CompletionStage<List<ObjectNode>>> bookingStream;


    @Inject
    public BookingController(@Named("bookingAsync") BookingService bookingAsync,
                             @Named("bookingSync") BookingService bookingSync,
                             @Named("bookingAkka") BookingService bookingAkka,
                             @Named("bookingStream") BookingService bookingStream) {
        this.bookingSync = bookingSync;
        this.bookingAsync = bookingAsync;
        this.bookingAkka = bookingAkka;
        this.bookingStream = bookingStream;
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

    public CompletionStage<Result> bookingWithAkka() {
        return bookingAkka.bookingTicket().thenApplyAsync((ObjectNode jsonNodes) ->
            ok(ResponseFactory.createResponse(jsonNodes, true))
        );
    }

    public CompletionStage<Result> bookingStreamWithAkka() {
        return bookingStream.bookingTicket().thenApply((List<ObjectNode> jsonNodes) ->
            ok(ResponseFactory.createResponse(jsonNodes, true))
        );
    }

    public CompletionStage<Result> bookingConcurrentWithAkka() {

        return null;
    }
}
