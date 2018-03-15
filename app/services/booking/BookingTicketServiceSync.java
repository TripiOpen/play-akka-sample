package services.booking;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.concurrent.CompletionStage;

public class BookingTicketServiceSync implements BookingService<CompletionStage<ObjectNode>> {

    @Override
    public CompletionStage<ObjectNode> bookingTicketAsync() {
        return null;
    }
}
