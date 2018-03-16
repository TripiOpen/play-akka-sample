package actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class BookingActor extends AbstractActor {

    public static Props getProps() {
        return Props.create(BookingActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ObjectNode.class, data ->
                    sender().tell(data, self())
                )
                .build();
    }
}
