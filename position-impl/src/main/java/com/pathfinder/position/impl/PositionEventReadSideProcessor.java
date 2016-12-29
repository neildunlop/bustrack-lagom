package com.pathfinder.position.impl;

import akka.Done;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.google.inject.Inject;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.ReadSideProcessor;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import com.pathfinder.position.api.PositionDetail;
import org.pcollections.PSequence;

import java.util.List;
import java.util.concurrent.CompletionStage;

import static com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide.completedStatement;

/**
 * Consumes events produced by persistent entities and update one or more tables in
 * Cassandra that are optimized for queries
 */
public class PositionEventReadSideProcessor extends ReadSideProcessor<PositionEvent> {

    private final CassandraSession session;
    private final CassandraReadSide readSide;

    private PreparedStatement insertPositionStatement;

    @Inject
    public PositionEventReadSideProcessor(CassandraSession session, CassandraReadSide readSide) {
        this.session = session;
        this.readSide = readSide;
    }

    // Return a list of all the tags that our processor will handle - if you return more than one tag,
    // Lagom will shard these tags across your services cluster.
    @Override
    public PSequence<AggregateEventTag<PositionEvent>> aggregateTags() {
        return PositionEvent.TAG.allTags();
    }

    @Override
    public ReadSideHandler<PositionEvent> buildHandler() {

        //the parameter here is a unique name for this side processor and is used to create a cassandra event offset table
        //used just for this processor.
        return readSide.<PositionEvent>builder("PositionEventReadSideProcessor")
                //global prepare runs at least once across the whole cluster.  It runs to prepare things before
                //any readside processing happens.  Ensure its idempotent!
                .setGlobalPrepare(this::createTable)
                //executed once per shard before any read side processing happens.  Use it to prepare any statements
                //and optimise database operations from that shard.
                .setPrepare(tag -> prepareInsertPosition())

                //** This is where we define what events we will listen for and which method we will trigger when
                //** that event is detected.
                .setEventHandler(PositionEvent.PositionAdded.class,
                        event -> insertPosition(event.getPositionDetail()))
                .build();
    }

    private CompletionStage<Done> createTable() {
        return session.executeCreateTable(
                "CREATE TABLE IF NOT EXISTS position ("
                        + "chassisNumber text, latitude double, longitude double, speed int, headingDegrees int, timestamp bigint, "
                        + "PRIMARY KEY (chassisNumber, timestamp))");
    }

    private CompletionStage<Done> prepareInsertPosition() {
        return session.prepare("INSERT INTO position (chassisNumber, latitude, longitude, speed, headingDegrees, timestamp) "
                + "VALUES (?, ?, ?, ?, ?, ?)")
                .thenApply(s -> {
                    insertPositionStatement = s;
                    return Done.getInstance();
                });
    }

    private CompletionStage<List<BoundStatement>> insertPosition(PositionDetail position) {
        return completedStatement(
                insertPositionStatement.bind(
                        position.getChassisNumber(),
                        position.getLatitude(),
                        position.getLongitude(),
                        position.getSpeed(),
                        position.getHeadingDegrees(),
                        position.getTimestamp().toEpochMilli()
                )
        );
    }
}



