package actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import jdk.nashorn.internal.ir.ObjectNode;

public class MaterializeActor extends AbstractActor {

    ActorMaterializer mat = ActorMaterializer.create(context());

    public static Props getProps() {
        return Props.create(MaterializeActor.class);
    }

    @Override
    public void preStart() throws Exception {
        Source.repeat("hello")
                .runWith(Sink.onComplete(tryDone -> {
                    System.out.println("Terminated stream: " + tryDone);
                }), mat);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(String.class, data -> {
            //sender().tell(data, self());
            context().stop(self());
        }).build();
    }
}
