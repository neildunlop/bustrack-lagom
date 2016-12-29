package com.pathfinder.position.impl;

import akka.stream.javadsl.Source;
import com.pathfinder.position.api.PositionDetail;
import org.pcollections.PSequence;

import java.util.concurrent.CompletionStage;

/**
 * Created by IWC-NeilDunlop on 29/12/2016.
 */
public class PositionRepositoryImpl implements PositionRepository {

    @Override
    public Source<PositionDetail, ?> getHistoricalPositions(PSequence<String> vehicleIds, long timestamp) {
        return null;
    }

    @Override
    public CompletionStage<PSequence<PositionDetail>> getRecentPositions(PSequence<String> vehicleIds) {
        return null;
    }
}
