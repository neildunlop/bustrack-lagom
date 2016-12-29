package com.pathfinder.bustrack.positionstream.impl;

import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;
import com.pathfinder.bustrack.positionstream.api.PositionStreamService;
import com.pathfinder.position.api.PositionService;

public class PositionStreamModule extends AbstractModule implements ServiceGuiceSupport {

    @Override
    protected void configure() {
        bindServices(serviceBinding(PositionStreamService.class, PositionStreamServiceImpl.class));
        //bindClient(PositionService.class);
    }
}
