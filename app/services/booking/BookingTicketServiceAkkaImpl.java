package services.booking;

import actors.BookingActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import models.ticket.Ticket;
import models.ticket.TicketFlight;
import models.ticket.TicketHotel;
import play.libs.Json;
import scala.compat.java8.FutureConverters;

import java.util.concurrent.CompletionStage;

import static akka.pattern.Patterns.ask;

@Singleton
public class BookingTicketServiceAkkaImpl implements BookingService<CompletionStage<Object>> {

    private final ActorRef bookingActor;

    @Inject
    public BookingTicketServiceAkkaImpl(ActorSystem system) {
        this.bookingActor = system.actorOf(BookingActor.getProps());
    }

    @Override
    public CompletionStage<Object> bookingTicket() {
        return FutureConverters.toJava(ask(bookingActor, findTicket(), 500));
    }

    public ObjectNode findTicket() {
        ObjectNode data = Json.newObject();

        Ticket ticketFlight = TicketFlight.db().createQuery(TicketFlight.class).findOne();
        Ticket ticketHotel = TicketHotel.db().createQuery(TicketHotel.class).findOne();

        data.putPOJO("flight", ticketFlight);
        data.putPOJO("hotel", ticketHotel);
        return data;
    }
}
