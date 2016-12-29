package com.pathfinder.position.impl;

import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;
import com.pathfinder.position.api.PositionService;

/**
 * Google Guice module.
 */
public class PositionModule extends AbstractModule implements ServiceGuiceSupport {
    @Override
    protected void configure() {
        bindServices(serviceBinding(PositionService.class, PositionServiceImpl.class));
        bind(PositionRepository.class).to(PositionRepositoryImpl.class);
    }
}
