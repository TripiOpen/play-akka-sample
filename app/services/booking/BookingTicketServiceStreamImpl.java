package services.booking;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.japi.function.Function;
import akka.stream.*;
import akka.stream.javadsl.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import models.ticket.Ticket;
import models.ticket.TicketFlight;
import models.ticket.TicketHotel;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import play.libs.Json;

import java.util.concurrent.CompletionStage;

public class BookingTicketServiceStreamImpl implements BookingService<CompletionStage<ObjectNode>> {

    private final Materializer mat;

    @Inject
    public BookingTicketServiceStreamImpl(ActorSystem system) {
        this.mat = ActorMaterializer.create(system);
    }

    @Override
    public CompletionStage<ObjectNode> bookingTicket() {

        Flow<ObjectNode, ObjectNode, NotUsed> f1 = Flow.of(ObjectNode.class).map(elem -> elem).async();
        Flow<ObjectNode, ObjectNode, NotUsed> f2 = Flow.of(ObjectNode.class).map(elem -> elem);
        Flow<ObjectNode, ObjectNode, NotUsed> f3 = Flow.of(ObjectNode.class).map(elem -> elem);
        Flow<ObjectNode, ObjectNode, NotUsed> f4 = Flow.of(ObjectNode.class).map(elem -> elem).async();
        Flow<ObjectNode, ObjectNode, NotUsed> f5 = Flow.of(ObjectNode.class).map(elem -> elem);

        Source<ObjectNode, NotUsed> inFlight = Source.fromPublisher(findTicketFlight());
        Source<ObjectNode, NotUsed> inHotel = Source.fromPublisher(findTicketHotel());

        Sink<ObjectNode, CompletionStage<ObjectNode>> sink = Sink.head();

        RunnableGraph<CompletionStage<ObjectNode>> graph =
                RunnableGraph.fromGraph(
                        GraphDSL.create(sink, (builder, out) -> {

                            final UniformFanOutShape<ObjectNode, ObjectNode> bcastFlight = builder.add(Broadcast.create(1));
                            final UniformFanOutShape<ObjectNode, ObjectNode> bcastHotel = builder.add(Broadcast.create(1));

                            final UniformFanInShape<ObjectNode, ObjectNode> merge = builder.add(Merge.create(2));

                            final Outlet<ObjectNode> sourceFlight = builder.add(inFlight).out();
                            final Outlet<ObjectNode> sourceHotel = builder.add(inHotel).out();

                            builder.from(sourceFlight).via(builder.add(f1)).
                                    viaFanOut(bcastFlight)
                                    .via(builder.add(f2)).viaFanIn(merge).via(builder.add(f3)).to(out);

                            builder.from(sourceHotel).via(builder.add(f4))
                                    .viaFanOut(bcastHotel)
                                    .via(builder.add(f5)).viaFanIn(merge);

                            return ClosedShape.getInstance();
                        }));
        return graph.run(mat);
    }

//        return Source.combine(
//                Source.fromPublisher(findTicketFlight()),
//                Source.fromPublisher(findTicketHotel()),
//                new ArrayList<>(), Merge::create)
//                .runWith(Sink.reduce((ObjectNode ticketFlight, ObjectNode ticketHotel) -> {
//                    ObjectNode data = Json.newObject();
//                    data.putPOJO("flight", ticketFlight);
//                    data.putPOJO("hotel", ticketHotel);
//                    return data;
//                }).async(), mat).toCompletableFuture();

    private Publisher<ObjectNode> findTicketHotel() {
        return (Subscriber<? super ObjectNode> s) -> {
            System.out.println("+++++++++++++++ findTicketHotel");
            Ticket ticketHotel = TicketHotel.db().createQuery(TicketHotel.class).findList().get(0);

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            s.onNext((ObjectNode)Json.toJson(ticketHotel));
            s.onComplete();
        };
    }

    private Publisher<ObjectNode> findTicketFlight() {
        return (Subscriber<? super ObjectNode> s) -> {
            System.out.println("+++++++++++++++ findTicketFlight");
            Ticket ticketFlight = TicketFlight.db().createQuery(TicketFlight.class).findList().get(0);

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            s.onNext((ObjectNode) Json.toJson(ticketFlight));
            s.onComplete();
        };
    }
}
