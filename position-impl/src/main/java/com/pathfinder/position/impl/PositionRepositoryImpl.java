package com.pathfinder.position.impl;

import akka.stream.javadsl.Source;
import com.google.inject.Inject;
import com.lightbend.lagom.javadsl.persistence.ReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import com.pathfinder.position.api.PositionDetail;
import org.pcollections.PSequence;

import java.util.concurrent.CompletionStage;

public class PositionRepositoryImpl implements PositionRepository {

    private final CassandraSession session;

    @Inject
    public PositionRepositoryImpl(CassandraSession session, ReadSide readSide) {
        this.session = session;
        readSide.register(PositionEventReadSideProcessor.class);
    }

    @Override
    public Source<PositionDetail, ?> getHistoricalPositions(PSequence<String> vehicleIds, long timestamp) {
        return null;
    }

    @Override
    public CompletionStage<PSequence<PositionDetail>> getRecentPositions(PSequence<String> vehicleIds) {
        return null;
    }
}
