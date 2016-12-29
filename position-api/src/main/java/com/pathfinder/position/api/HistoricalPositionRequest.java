package com.pathfinder.position.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Wither;
import org.pcollections.PSequence;

import javax.annotation.concurrent.Immutable;
import java.time.Instant;

/**
 * A request for historical position information for one or more vehicles.
 */
@Immutable
@JsonDeserialize
@Value
@Builder
@Wither
@AllArgsConstructor(onConstructor = @__(@JsonCreator))
public class HistoricalPositionRequest {

    //See: http://pcollections.org/
    @NonNull
    PSequence<String> chassisNumbers;

    //See: https://docs.oracle.com/javase/8/docs/api/java/time/Instant.html
    @NonNull
    Instant fromTime;
}


