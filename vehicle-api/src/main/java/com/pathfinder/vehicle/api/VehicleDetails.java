package com.pathfinder.vehicle.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.experimental.Wither;
import lombok.Value;


import javax.annotation.concurrent.Immutable;

/**
 * A model for representing a blog post - this is just a data container, we aren't able to do anything to it.
 */
@Immutable
@JsonDeserialize
@Value
@Builder
@Wither
@AllArgsConstructor(onConstructor = @__(@JsonCreator))
public class VehicleDetails {

    @NonNull
    String chassisNumber;
    @NonNull
    String make;
    @NonNull
    String model;
    @NonNull
    String colour;
    @NonNull
    int capacity;
}
