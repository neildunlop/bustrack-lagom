package com.pathfinder.position.impl;

import akka.NotUsed;
import akka.stream.javadsl.Source;
import com.datastax.driver.core.Row;
import com.google.inject.Inject;
import com.lightbend.lagom.javadsl.persistence.ReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import com.pathfinder.position.api.PositionDetail;
import org.pcollections.PSequence;
import org.pcollections.TreePVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.Position;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

public class PositionRepositoryImpl implements PositionRepository {

    private final Logger log = LoggerFactory.getLogger(PositionServiceImpl.class);

    private static final int NUM_RECENT_POSITIONS = 3;

    private static final String SELECT_HISTORICAL_POSITIONS =
            "SELECT * FROM position WHERE chassisNumber = ? AND timestamp >= ? ORDER BY timestamp ASC;";

    private static final String SELECT_HISTORICAL_POSITIONS_FOR_VEHICLE =
            "SELECT * FROM position WHERE chassisNumber = ? ORDER BY timestamp ASC;";

    private static final String SELECT_ALL_HISTORICAL_POSITIONS =
            "SELECT * FROM position;";

    private static final String SELECT_RECENT_POSITIONS =
            "SELECT * FROM position WHERE chassisNumber = ? ORDER BY timestamp DESC LIMIT ?";

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
            log.warn("here");
            sources.add(getHistoricalPositionsForVehicleFromTimestamp(vehicleId, timestamp));
            //sources.add(getHistoricalPositionsForVehicle(vehicleId));
        }
        // Positions from one vehicle are ordered by timestamp, but chirps from different
        // users are not ordered. That can be improved by implementing a smarter
        // merge that takes the timestamps into account.
        return Source.from(sources).flatMapMerge(sources.size(), s -> s);
    }

    private Source<PositionDetail, NotUsed> getHistoricalPositionsForVehicleFromTimestamp(String vehicleId, long timestamp) {
        log.warn("Getting all historical positions from " + timestamp + " for vehicle: "+ vehicleId.toString() + " with timestamp" + timestamp);
        return session.select(SELECT_HISTORICAL_POSITIONS, vehicleId, timestamp)
                .map(this::mapPositionDetail);
    }

    private Source<PositionDetail, NotUsed> getHistoricalPositionsForVehicle(String vehicleId) {
        log.warn("Getting all historical positions for vehicle: "+ vehicleId.toString());
        return session.select(SELECT_HISTORICAL_POSITIONS_FOR_VEHICLE, vehicleId)
                .map(this::mapPositionDetail);
    }

    private Source<PositionDetail, NotUsed> getAllHistoricalPositions() {
        log.warn("Getting all historical positions.");
        return session.select(SELECT_ALL_HISTORICAL_POSITIONS)
                .map(this::mapPositionDetail);
    }

    public CompletionStage<PSequence<PositionDetail>> getRecentPositions(PSequence<String> vehicleIds) {
        CompletionStage<PSequence<PositionDetail>> results = CompletableFuture.completedFuture(TreePVector.empty());
        for (String vehicleId : vehicleIds) {
            results = results.thenCombine(getRecentPositions(vehicleId), PSequence::plusAll);
        }
        return results.thenApply(this::limitRecentPositions);
    }

    //is this really needed if the sql query does the limit - maybe due to the fact that the incoming rows may be a
    //stream?
    private PSequence<PositionDetail> limitRecentPositions(PSequence<PositionDetail> all) {
        List<PositionDetail> limited = all.stream()
                .sorted(comparing((PositionDetail positionDetail) -> positionDetail.getTimestamp()).reversed())
                .limit(NUM_RECENT_POSITIONS)
                .collect(Collectors.toCollection(ArrayList::new));

        Collections.reverse(limited);
        return TreePVector.from(limited);
    }

    private CompletionStage<PSequence<PositionDetail>> getRecentPositions(String vehicleId) {
        return session.selectAll(SELECT_RECENT_POSITIONS, vehicleId, NUM_RECENT_POSITIONS)
                .thenApply(this::mapPositionDetails);
    }

    private TreePVector<PositionDetail> mapPositionDetails(List<Row> positionDetails) {
        return positionDetails.stream()
                .map(this::mapPositionDetail)
                .collect(pSequenceCollector);
    }

    private PositionDetail mapPositionDetail(Row row) {
        return new PositionDetail(
                row.getString("chassisNumber"),
                row.getDouble("latitude"),
                row.getDouble("longitude"),
                row.getInt("speed"),
                row.getInt("headingDegrees"),
                Instant.ofEpochMilli(row.getLong("timestamp"))
        );
    }

    //TODO: Lookup what this does.. suspect a helper method to convert a stream to a list
    private static final Collector<PositionDetail, ?, TreePVector<PositionDetail>> pSequenceCollector =
            Collectors.collectingAndThen(Collectors.toList(), TreePVector::from);
}
