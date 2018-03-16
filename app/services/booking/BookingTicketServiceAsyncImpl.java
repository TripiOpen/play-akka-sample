package services.booking;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import models.ticket.Ticket;
import models.ticket.TicketFlight;
import models.ticket.TicketHotel;
import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import scala.concurrent.ExecutionContext;
import services.log.LogService;

import javax.inject.Named;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Singleton
public class BookingTicketServiceAsyncImpl implements BookingService<CompletionStage<ObjectNode>> {

    private final LogService logService;
    private final ExecutionContext exec;
    private final HttpExecutionContext httpExec;

    @Inject
    public BookingTicketServiceAsyncImpl(@Named("bookingLog") LogService logService,
                                         ExecutionContext exec, HttpExecutionContext httpExec) {
        this.logService = logService;
        this.exec = exec;
        this.httpExec = httpExec;
    }

    @Override
    public CompletionStage<ObjectNode> bookingTicket() {
        CompletableFuture<ObjectNode> future = new CompletableFuture<>();

        httpExec.current().execute(() -> {
            ObjectNode data = Json.newObject();

            Ticket ticketFlight = TicketFlight.db().createQuery(TicketFlight.class).findOne();
            Ticket ticketHotel = TicketHotel.db().createQuery(TicketHotel.class).findOne();

            data.putPOJO("flight", ticketFlight);
            data.putPOJO("hotel", ticketHotel);

            future.complete(data);
        });

        exec.execute(() -> {
            logService.writeLogs("Booking an ticket async!");
        });

        return future;
    }
}
