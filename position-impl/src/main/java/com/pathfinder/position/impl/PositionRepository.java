package com.pathfinder.position.impl;

import akka.stream.javadsl.Source;
import com.pathfinder.position.api.PositionDetail;
import org.pcollections.PSequence;

import java.util.concurrent.CompletionStage;

/**
 * Provides access to past positions. See {@link PositionTopic} for real-time access to new positions.
 */
interface PositionRepository {

    //provides access to historical position information for given vehicle ids after a given time.
    Source<PositionDetail, ?> getHistoricalPositions(PSequence<String> vehicleIds, long timestamp);

    //provides access to live position information for given vehicle ids.
    CompletionStage<PSequence<PositionDetail>> getRecentPositions(PSequence<String> vehicleIds);
}
