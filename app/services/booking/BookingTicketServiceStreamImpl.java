package services.booking;

import akka.NotUsed;
import akka.stream.javadsl.Flow;
import akka.stream.scaladsl.Source;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;


public class BookingTicketServiceStreamImpl implements BookingService<Publisher<ObjectNode>> {

    @Override
    public Publisher<ObjectNode> bookingTicket() {
        final Flow<String, String, NotUsed> authors = Flow.of(String.class)
                .filter(t -> t.contains("A"))
                .map(t -> t);
        Source.fromPublisher(new Publisher<Object>() {
            @Override
            public void subscribe(Subscriber<? super Object> s) {

            }
        });

        return null;
    }


}
