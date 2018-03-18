package services.booking;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import models.ticket.Ticket;
import models.ticket.TicketFlight;
import models.ticket.TicketHotel;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import play.libs.Json;

import java.util.ArrayList;
import java.util.concurrent.CompletionStage;

public class BookingTicketServiceStreamImpl implements BookingService<CompletionStage<ObjectNode>> {

    private final Materializer mat;

    @Inject
    public BookingTicketServiceStreamImpl(ActorSystem system) {
        this.mat = ActorMaterializer.create(system);
    }

    @Override
    public CompletionStage<ObjectNode> bookingTicket() {
        return Source.combine(
                Source.fromPublisher(findTicketFlight()),
                Source.fromPublisher(findTicketHotel()),
                new ArrayList<>(), Merge::create)
                .runWith(Sink.reduce((ObjectNode ticketFlight, ObjectNode ticketHotel) -> {
                    ObjectNode data = Json.newObject();
                    data.putPOJO("flight", ticketFlight);
                    data.putPOJO("hotel", ticketHotel);
                    return data;
                }), mat).toCompletableFuture();
    }

    private Publisher<ObjectNode> findTicketHotel() {
        return (Subscriber<? super ObjectNode> s) -> {
            Ticket ticketHotel = TicketHotel.db().createQuery(TicketHotel.class).findList().get(0);
            s.onNext((ObjectNode)Json.toJson(ticketHotel));
            s.onComplete();
        };
    }

    private Publisher<ObjectNode> findTicketFlight() {
        return (Subscriber<? super ObjectNode> s) -> {
            Ticket ticketFlight = TicketFlight.db().createQuery(TicketFlight.class).findList().get(0);
            s.onNext((ObjectNode) Json.toJson(ticketFlight));
            s.onComplete();
        };
    }
}
