package com.pathfinder.vehicle.impl;

import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;
import com.pathfinder.vehicle.api.VehicleService;

/**
 * Google Guice module.
 */
public class VehicleModule extends AbstractModule implements ServiceGuiceSupport {

    @Override
    protected void configure() {

        bindServices(serviceBinding(VehicleService.class, VehicleServiceImpl.class));
    }
}
