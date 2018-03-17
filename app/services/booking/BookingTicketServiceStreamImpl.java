package services.booking;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Graph;
import akka.stream.Materializer;
import akka.stream.javadsl.Merge;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import com.fasterxml.jackson.databind.JsonNode;
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


    //private final ActorRef bookingActor;
    private Materializer mat;

    @Inject
    public BookingTicketServiceStreamImpl(ActorSystem system) {
        //this.bookingActor = system.actorOf(BookingActor.getProps());
        this.mat = ActorMaterializer.create(system);
    }

    @Override
    public CompletionStage<ObjectNode> bookingTicket() {
        return Source.combine(Source.fromPublisher(findTicketFlight()), Source.fromPublisher(findTicketHotel()),
                new ArrayList<>(), Merge::create).runWith(Sink.head(), mat);
    }

//    ObjectNode data = Json.newObject();
//                data.putPOJO("hotel", ticketHotel);
//                data.putPOJO("flight", ticketFlight);

    private Publisher<ObjectNode> findTicketHotel() {
        return (Subscriber<? super ObjectNode> s) -> {
            Ticket ticketHotel = TicketHotel.db().createQuery(TicketHotel.class).findOne();
            s.onNext((ObjectNode)Json.toJson(ticketHotel));
        };
    }

    private Publisher<ObjectNode> findTicketFlight() {
        return (Subscriber<? super ObjectNode> s) -> {
            Ticket ticketFlight = TicketFlight.db().createQuery(TicketFlight.class).findOne();
            s.onNext((ObjectNode) Json.toJson(ticketFlight));
        };
    }
}
