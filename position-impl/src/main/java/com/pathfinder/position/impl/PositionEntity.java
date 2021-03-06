package com.pathfinder.position.impl;

import akka.Done;
import akka.NotUsed;
import com.google.inject.Inject;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import java.util.Optional;

/**
 * Behaviours for the Position.  This is the logic of the entity.
 */

//TODO: Note that there is no PositionState for this entity... check out ChirpTimeline for a similar implementation.
//suspect this is because it doesn't make sense for a position to have state.. its not an aggregate root?  The positions
//belong to vehicles.
public class PositionEntity extends PersistentEntity<PositionCommand, PositionEvent, NotUsed> {

    //allows us to broadcast position detail messages to other services via Kafka.
    private final PositionTopic topic;

    @Inject
    public PositionEntity(PositionTopic topic) {
        this.topic = topic;
    }

    //Wire the command and event handlers into behaviours.
    //There is no state for 'Position' so we pass in an optional not used.  We need to
    //honour the parameter but we have an explicit 'NotUsed' type we can use.
    @Override
    public Behavior initialBehavior(Optional<NotUsed> snapshotState) {

        final BehaviorBuilder builder = newBehaviorBuilder(NotUsed.getInstance());
        addBehaviorForAddPosition(builder);
        return builder.build();
    }

    private void addBehaviorForAddPosition(BehaviorBuilder builder) {

        //handle the 'AddPosition' command by persisting a new PositionAdded PositionEvent to the event store,
        // (the content of which comes from the incoming command).
        //A 'done' is returned to the caller - akka equivilent of HTTP 200
        //and the position detail is published to the kafka topics for other services to use.
        builder.setCommandHandler(PositionCommand.AddPosition.class,
                //validation could be inserted here before persisting the command and emitting events.
                (cmd,ctx) -> ctx.thenPersist(
                        new PositionEvent.PositionAdded(cmd.getPositionDetail()),
                        evt -> {
                            ctx.reply(Done.getInstance());
                            topic.publish(cmd.getPositionDetail());
                        }
                )
        );

        //Add an event handler to update the current state of the object when a position added event is received.
        //TODO: What does 'state()' do? - gives you the current state of the entity I believe...
        builder.setEventHandler(PositionEvent.PositionAdded.class, evt -> state());
    }

    //Alternative Approach to wiring in command and event handlers.
    //Can't decide which I like..

//    @Override
//    public Behavior initialBehavior(Optional<NotUsed> snapshotState) {
//        BehaviorBuilder b = newBehaviorBuilder(NotUsed.getInstance());
//        b.setCommandHandler(PositionCommand.AddPosition.class, this::addPosition);
//        b.setEventHandler(PositionEvent.PositionAdded.class, evt -> state());
//        return b.build();
//    }
//
//    private Persist addPosition(PositionCommand.AddPosition cmd, CommandContext<Done> ctx) {
//        return ctx.thenPersist(new PositionEvent.PositionAdded(cmd.getPositionDetail()), evt -> {
//            ctx.reply(Done.getInstance());
//        });
//    }
}
