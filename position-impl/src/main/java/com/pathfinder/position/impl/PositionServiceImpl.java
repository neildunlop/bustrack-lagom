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
import com.pathfinder.vehicle.api.VehicleDetails;
import com.pathfinder.vehicle.api.VehicleService;
import org.pcollections.PSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

/**
 * Service implementation for the position microservice. This service is essentially a wrapper for the
 * persistence entity API and also a wrapper for topic management.  This is the meat of the service.
 */
public class PositionServiceImpl implements PositionService {

    private final Logger log = LoggerFactory.getLogger(PositionServiceImpl.class);

    //provides access to all persistent entities, regardless of where they are handled in the cluster.
    //(each persistent entity is an actor, so this gives is a way of locating the actor for each entity).
    //(suggests that maybe the position should not be service in its own right but a child of the vehicle?)
    private final PersistentEntityRegistry persistentEntities;

    private final VehicleService vehicleService;

    //We have an instance of the position topic here so that we can get old/recent positions from the DB
    //and publish them to the topic so subscribers can get them.  We dont want to pass large amount of
    //data back in a reply. We want to subscribe to a stream that is used to push data to us asynchronously.
    private final PositionTopic topic;


    //gives us access to historical position information that is not held in memory, but persisted to cassandra
    //(how do things get flushed to DB and when do they come out of memory if we have an actor per position entity?)
    private final PositionRepository positions;

    @Inject
    public PositionServiceImpl(final PersistentEntityRegistry registry, final PositionRepository positions, PositionTopic topic, VehicleService vehicleService) {
        this.persistentEntities = registry;
        this.vehicleService = vehicleService;
        this.positions = positions;
        this.topic = topic;

        registry.register(PositionEntity.class);
    }

    @Override
    public ServiceCall<PositionDetail, Done> addPosition(String vehicleId) {

        return request -> {

            //hoping this just pukes if it can't find the vehicle
            CompletionStage<Optional<VehicleDetails>> response = vehicleService.getVehicle(vehicleId).invoke();

            try {
                if (response.toCompletableFuture().get().isPresent()) {
                    //This locates the akka actor responsible for this position entity and sends the command
                    PersistentEntityRef<PositionCommand> ref =
                            persistentEntities.refFor(PositionEntity.class, vehicleId);
                    log.warn("Adding position...");
                    return ref.ask(new PositionCommand.AddPosition(request));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            log.warn("The vehicle does not exist. Not adding position...");
            return CompletableFuture.completedFuture(Done.getInstance());
        };
    }

    @Override
    public ServiceCall<LivePositionRequest, Source<PositionDetail, ?>> getLivePosition() {
        log.warn("Getting live positions");
        return req -> positions.getRecentPositions(req.getChassisNumbers()).thenApply(recentPositions -> {
            List<Source<PositionDetail, ?>> sources = new ArrayList<>();
            for (String chassisNumber : req.getChassisNumbers()) {
                sources.add(topic.subscriber(chassisNumber));
            }
            HashSet<String> vehcileIds = new HashSet<>(req.getChassisNumbers());
            Source<PositionDetail, ?> publishedPositions = Source.from(sources).flatMapMerge(sources.size(), s -> s)
                    .filter(c -> vehcileIds.contains(c.getChassisNumber()));

            // We currently ignore the fact that it is possible to get duplicate positions
            // from the recent positions query and the 'live' topic. That can be solved with a de-duplication stage.
            return Source.from(recentPositions).concat(publishedPositions);
        });
    }

    @Override
    public ServiceCall<HistoricalPositionRequest, Source<PositionDetail, ?>> getHistoricalPosition() {
        return req -> {
            PSequence<String> vehicleIds = req.getChassisNumbers();
            long timestamp = req.getFromTime().toEpochMilli();
            log.warn("Getting historical position with timestamp: " + Long.toString(timestamp) + " from original instant of: " + req.getFromTime().toString());
            Source<PositionDetail, ?> result = positions.getHistoricalPositions(vehicleIds, timestamp);
            return CompletableFuture.completedFuture(result);
        };
    }
}
