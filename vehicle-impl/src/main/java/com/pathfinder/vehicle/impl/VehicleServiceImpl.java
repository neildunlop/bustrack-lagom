package com.pathfinder.vehicle.impl;

import akka.Done;
import akka.NotUsed;
import com.google.inject.Inject;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import com.pathfinder.vehicle.api.VehicleDetails;
import com.pathfinder.vehicle.api.VehicleService;
import com.pathfinder.vehicle.impl.VehicleCommand;
import com.pathfinder.vehicle.impl.VehicleEntity;

import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation for the vehicle (fleet?) microservice. This service is essentially a wrapper for the
 * persistence entity API.
 */
public class VehicleServiceImpl implements VehicleService {

    //injected by Guice
    private final PersistentEntityRegistry registry;

    @Inject
    public VehicleServiceImpl(final PersistentEntityRegistry registry) {
        this.registry = registry;
        registry.register(VehicleEntity.class);
    }

    @Override
    public ServiceCall<NotUsed, Optional<VehicleDetails>> getVehicle(String id) {
        return request -> registry.refFor(VehicleEntity.class, id)
                .ask(VehicleCommand.GetVehicle.INSTANCE);
    }

    @Override
    public ServiceCall<VehicleDetails, String> addVehicle() {
        return content -> registry.refFor(VehicleEntity.class, UUID.randomUUID().toString())
                .ask(new VehicleCommand.AddVehicle(content));
    }

    @Override
    public ServiceCall<VehicleDetails, Done> updateVehicle(String id) {
        return content -> registry.refFor(VehicleEntity.class, id)
                .ask(new VehicleCommand.UpdateVehicle(content));
    }
}
