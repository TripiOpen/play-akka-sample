package services.booking;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import models.ticket.Ticket;
import models.ticket.TicketFlight;
import models.ticket.TicketHotel;
import play.libs.Json;
import services.log.LogService;

import javax.inject.Named;
import java.util.concurrent.CompletionStage;

@Singleton
public class BookingTicketServiceSyncImpl implements BookingService<ObjectNode> {

    private final LogService logService;

    @Inject
    public BookingTicketServiceSyncImpl(@Named("bookingLog") LogService logService) {
        this.logService = logService;
    }

    @Override
    public ObjectNode bookingTicket() {
        logService.writeLogs("Booking an ticket sync!");

        ObjectNode data = Json.newObject();

        Ticket ticketFlight = TicketFlight.db().createQuery(TicketFlight.class).findList().get(0);
        Ticket ticketHotel = TicketHotel.db().createQuery(TicketHotel.class).findList().get(0);

        data.putPOJO("flight", ticketFlight);
        data.putPOJO("hotel", ticketHotel);

        return data;
    }
}
