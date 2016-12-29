package com.pathfinder.vehicle.impl;

import akka.Done;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import java.util.Optional;

/**
 * Defines the behaviors for a vehicle.
 */
public class VehicleEntity extends PersistentEntity<VehicleCommand, VehicleEvent, VehicleState> {


    @Override
    public Behavior initialBehavior(Optional<VehicleState> snapshotState) {

        final BehaviorBuilder builder = newBehaviorBuilder(snapshotState.orElse(VehicleState.EMPTY));
        addBehaviorForGetVehicle(builder);
        addBehaviorForAddVehicle(builder);
        addBehaviorForUpdateVehicle(builder);
        return builder.build();
    }

    //a simple read only handler that replies with the current state of the entity
    private void addBehaviorForGetVehicle(BehaviorBuilder builder) {

        //state is a magic variable that is inherited from the PersistentEntity
        //not sure where cmd/ctx come from?
        builder.setReadOnlyCommandHandler(VehicleCommand.GetVehicle.class,
                (cmd,ctx) -> ctx.reply(state().getVehicleDetails()));

    }

    private void addBehaviorForUpdateVehicle(BehaviorBuilder builder) {

        //handle the 'UpdateVehicle' command by persisting a new VehicleUpdated VehicleEvent to the event log,
        // (the id of which comes from?? and the content of which comes from the incoming command.
        //A 'done' is returned to the caller - akka equivilent of HTTP 200
        builder.setCommandHandler(VehicleCommand.UpdateVehicle.class,
                (cmd,ctx) -> ctx.thenPersist(
                        new VehicleEvent.VehicleUpdated(entityId(), cmd.getContent()),
                        evt -> ctx.reply(Done.getInstance())
                )
        );

        //Add an event handler to update the current state of the object when a vehicle updated event is received.
        builder.setEventHandler(VehicleEvent.VehicleUpdated.class,
                evt -> new VehicleState(Optional.of(evt.getVehicleDetails()))
        );
    }

    private void addBehaviorForAddVehicle(BehaviorBuilder builder) {

        //handle the 'AddVehicle' command by persisting a new VehicleAdded VehicleEvent to the event log,
        // (the id of which comes from?? and the content of which comes from the incoming command.
        //The entity id is returned to the caller.
        builder.setCommandHandler(VehicleCommand.AddVehicle.class,
                (cmd,ctx) -> ctx.thenPersist(
                        new VehicleEvent.VehicleAdded(entityId(), cmd.getVehicleDetails()),
                        evt -> ctx.reply(entityId())
                )
        );

        //Add an event handler to update the current state of the object when a post added event is received.
        builder.setEventHandler(VehicleEvent.VehicleAdded.class,
                evt -> new VehicleState(Optional.of(evt.getVehicleDetails()))
        );
    }
}

