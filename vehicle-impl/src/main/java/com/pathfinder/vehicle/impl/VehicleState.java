package com.pathfinder.vehicle.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.serialization.CompressedJsonable;
import com.pathfinder.vehicle.api.VehicleDetails;
import lombok.AllArgsConstructor;
import lombok.Value;

import javax.annotation.concurrent.Immutable;
import java.util.Optional;

@Immutable
@JsonDeserialize
@Value
@AllArgsConstructor(onConstructor = @__(@JsonCreator))
public class VehicleState implements CompressedJsonable {

    public static final VehicleState EMPTY = new VehicleState(Optional.empty());

    Optional<VehicleDetails> vehicleDetails;

}
