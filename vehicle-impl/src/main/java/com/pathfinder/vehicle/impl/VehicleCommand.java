package com.pathfinder.vehicle.impl;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.serialization.CompressedJsonable;
import com.lightbend.lagom.serialization.Jsonable;
import com.pathfinder.vehicle.api.VehicleDetails;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import javax.annotation.concurrent.Immutable;
import java.util.Optional;
import akka.Done;
import com.pathfinder.vehicle.api.VehicleDetails;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

/**
 * Commands for manipulating Vehicle entities.
 */
public interface VehicleCommand extends Jsonable {

    enum GetVehicle implements VehicleCommand, PersistentEntity.ReplyType<Optional<VehicleDetails>> {
        INSTANCE
    }

    @Immutable
    @JsonDeserialize
    @Value
    @AllArgsConstructor(onConstructor = @__(@JsonCreator))
    final class AddVehicle implements VehicleCommand, CompressedJsonable, PersistentEntity.ReplyType<String> {
        @NonNull
        VehicleDetails vehicleDetails;
    }

    @Immutable
    @JsonDeserialize
    @Value
    @AllArgsConstructor(onConstructor = @__(@JsonCreator))
    final class UpdateVehicle implements VehicleCommand, CompressedJsonable, PersistentEntity.ReplyType<Done> {
        @NonNull
        VehicleDetails content;
    }

    //TODO: No delete?
}

