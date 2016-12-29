package com.pathfinder.position.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventShards;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTagger;
import com.lightbend.lagom.serialization.CompressedJsonable;
import com.lightbend.lagom.serialization.Jsonable;
import com.pathfinder.position.api.PositionDetail;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

import javax.annotation.concurrent.Immutable;

/**
 * Definitions of the events that can be emitted by our Position Entity.
 */
interface PositionEvent extends Jsonable, AggregateEvent<PositionEvent> {

    int NUM_SHARDS = 3;

    AggregateEventShards<PositionEvent> TAG =
            AggregateEventTag.sharded(PositionEvent.class, NUM_SHARDS);

    @Override
    default AggregateEventTagger<PositionEvent> aggregateTag() {
        //use this signature if you are not sharding..
        //return AggregateEventTag.of(PositionEvent.class);
        return TAG;
    }

    @Immutable
    @JsonDeserialize
    @Value
    @AllArgsConstructor(onConstructor = @__(@JsonCreator))
    final class PositionAdded implements PositionEvent, CompressedJsonable {
        @NonNull
        PositionDetail positionDetail;
    }

}
