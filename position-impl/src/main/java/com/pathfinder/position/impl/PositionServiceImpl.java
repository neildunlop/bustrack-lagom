package com.pathfinder.position.impl;

import akka.Done;
import akka.stream.javadsl.Source;
import com.google.inject.Inject;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import com.pathfinder.position.api.HistoricalPositionRequest;
import com.pathfinder.position.api.LivePositionRequest;
import com.pathfinder.position.api.PositionDetail;
import com.pathfinder.position.api.PositionService;
import org.pcollections.PSequence;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service implementation for the position microservice. This service is essentially a wrapper for the
 * persistence entity API.
 */
public class PositionServiceImpl implements PositionService {


    //provides access to all persistent entities, regardless of where they are handled in the cluster.
    //(each persistent entity is an actor, so this gives is a way of locating the actor for each entity).
    //(suggests that maybe the position should not be service in its own right but a child of the vehicle?)
    private final PersistentEntityRegistry persistentEntities;

    //Live position updates are distributed to subscribers via the Topic - topic is Kafka?
    //private final PositionTopic topic;

    //gives us access to historical position information that is not held in memory, but persisted to cassandra
    //(how do things get flushed to DB and when do they come out of memory if we have an actor per position entity?)
    private final PositionRepository positions;

    @Inject
    public PositionServiceImpl(final PersistentEntityRegistry registry, final PositionRepository positions) {
        this.persistentEntities = registry;
        this.positions = positions;

        registry.register(PositionEntity.class);

    }

    @Override
    public ServiceCall<PositionDetail, Done> addPosition(String vehicleId) {
          return request -> {
              //This locates the akka actor responsible for this position entity and sends the command?
              PersistentEntityRef<PositionCommand> ref =
                    persistentEntities.refFor(PositionEntity.class, vehicleId);
            return ref.ask(new PositionCommand.AddPosition(request));
        };
    }

    @Override
    public ServiceCall<LivePositionRequest, Source<PositionDetail, ?>> getLivePosition() {
//        return req -> persistentEntities.getRecentPositions(req.getChassisNumbers()).thenApply(recentPositions -> {
//            List<Source<PositionDetail, ?>> sources = new ArrayList<>();
//            for (String chassisNumber : req.getChassisNumbers()) {
//                sources.add(topic.subscriber(chassisNumber));
//            }
//            HashSet<String> users = new HashSet<>(req.getChassisNumbers());
//            Source<PositionDetail, ?> publishedPositions = Source.from(sources).flatMapMerge(sources.size(), s -> s)
//                    .filter(c -> users.contains(c.getChassisNumber()));
//
//            // We currently ignore the fact that it is possible to get duplicate chirps
//            // from the recent and the topic. That can be solved with a de-duplication stage.
//            return Source.from(recentPositions).concat(publishedPositions);
//        });
        return null;
    }

    @Override
    public ServiceCall<HistoricalPositionRequest, Source<PositionDetail, ?>> getHistoricalPosition() {
//        return req -> {
//            PSequence<String> vehicleIds = req.getChassisNumbers();
//            long timestamp = req.getFromTime().toEpochMilli();
//            Source<PositionDetail, ?> result = persistentEntities.getHistoricalPositions(userIds, timestamp);
//            return CompletableFuture.completedFuture(result);
//        };
        return null;
    }
}
