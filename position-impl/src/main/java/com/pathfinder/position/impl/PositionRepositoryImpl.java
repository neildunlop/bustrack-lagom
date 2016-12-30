package com.pathfinder.position.impl;

import akka.NotUsed;
import akka.stream.javadsl.Source;
import com.datastax.driver.core.DataType;
import com.datastax.driver.core.Row;
import com.google.inject.Inject;
import com.lightbend.lagom.javadsl.persistence.ReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import com.pathfinder.position.api.PositionDetail;
import lombok.NonNull;
import org.pcollections.PSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;

public class PositionRepositoryImpl implements PositionRepository {

    private final Logger log = LoggerFactory.getLogger(PositionServiceImpl.class);

    private static final String SELECT_HISTORICAL_POSITIONS =
            "SELECT * FROM position WHERE chassisNumber = ? AND timestamp >= ? ORDER BY timestamp ASC";

    private static final String SELECT_ALL_HISTORICAL_POSITIONS =
            "SELECT * FROM position";

    private final CassandraSession session;

    @Inject
    public PositionRepositoryImpl(CassandraSession session, ReadSide readSide) {
        this.session = session;
        readSide.register(PositionEventReadSideProcessor.class);
    }

    @Override
    public Source<PositionDetail, ?> getHistoricalPositions(PSequence<String> vehicleIds, long timestamp) {
        List<Source<PositionDetail, ?>> sources = new ArrayList<>();
        for (String vehicleId : vehicleIds) {
            sources.add(getHistoricalPositions(vehicleId, timestamp));
        }
        // Positions from one vehicle are ordered by timestamp, but chirps from different
        // users are not ordered. That can be improved by implementing a smarter
        // merge that takes the timestamps into account.
        return Source.from(sources).flatMapMerge(sources.size(), s -> s);
    }

//    private Source<PositionDetail, NotUsed> getHistoricalPositions(String vehicleId, long timestamp) {
//        return session.select(SELECT_HISTORICAL_POSITIONS, vehicleId, timestamp)
//                .map(this::mapPositionDetail);
//    }

    private Source<PositionDetail, NotUsed> getHistoricalPositions(String vehicleId, long timestamp) {
        return session.select(SELECT_ALL_HISTORICAL_POSITIONS)
                .map(this::mapPositionDetail);
    }

//    private PositionDetail mapPositionDetail(Row row) {
//        return new PositionDetail(
//                row.getString("chassisNumber"),
//                row.getDouble("latitude"),
//                row.getDouble("longitude"),
//                row.getInt("speed"),
//                row.getInt("headingDegrees"),
//                Instant.ofEpochMilli(row.getLong("timestamp"))
//        );
//    }

    private PositionDetail mapPositionDetail(Row row) {
        PositionDetail detail =  new PositionDetail(
                row.getString("chassisNumber"),
                row.getDouble("latitude"),
                row.getDouble("longitude"),
                row.getInt("speed"),
                row.getInt("headingDegrees"),
                Instant.ofEpochMilli(row.getLong("timestamp"))
        );
        log.debug("Converted: "+detail.toString());
        return detail;
    }

    @Override
    public CompletionStage<PSequence<PositionDetail>> getRecentPositions(PSequence<String> vehicleIds) {
        return null;
    }
}
