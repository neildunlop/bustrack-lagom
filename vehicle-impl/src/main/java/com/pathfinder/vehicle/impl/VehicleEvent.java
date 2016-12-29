package com.pathfinder.vehicle.impl;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTagger;
import com.lightbend.lagom.serialization.CompressedJsonable;
import com.lightbend.lagom.serialization.Jsonable;
import com.pathfinder.vehicle.api.VehicleDetails;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

import javax.annotation.concurrent.Immutable;

/**
 * Events related to vehicle entities.  Extending AggregateEvent allows us to shard it across databases.
 */
public interface VehicleEvent extends Jsonable, AggregateEvent<VehicleEvent> {

    @Override
    default AggregateEventTagger<VehicleEvent> aggregateTag() {
        //use the name of the class as the aggregation key.. not great but ok for this demo..
        return AggregateEventTag.of(VehicleEvent.class);
    }

    @Immutable
    @JsonDeserialize
    @Value
    @AllArgsConstructor(onConstructor = @__(@JsonCreator))
    final class VehicleAdded implements VehicleEvent, CompressedJsonable {
        @NonNull
        String id;
        @NonNull
        VehicleDetails vehicleDetails;
    }

    @Immutable
    @JsonDeserialize
    @Value
    @AllArgsConstructor(onConstructor = @__(@JsonCreator))
    final class VehicleUpdated implements VehicleEvent, CompressedJsonable {
        @NonNull
        String id;
        @NonNull
        VehicleDetails vehicleDetails;
    }

}

