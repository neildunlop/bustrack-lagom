package com.pathfinder.position.impl;

import akka.Done;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.Jsonable;
import com.pathfinder.position.api.PositionDetail;
import jdk.nashorn.internal.ir.annotations.Immutable;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

/**
 * Commands for manipulating Position Entities.
 */
public interface PositionCommand extends Jsonable {

    //There is no way to update a position.. we only want to be able to add positions.
    //There is no way to delete a position.
    //There is no way to 'get' a position (I dont think)...

    @Immutable
    @JsonDeserialize
    @Value
    @AllArgsConstructor(onConstructor = @__(@JsonCreator))
    final class AddPosition implements PositionCommand, PersistentEntity.ReplyType<Done> {
        @NonNull
        PositionDetail positionDetail;
    }
}
