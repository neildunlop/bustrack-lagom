package com.pathfinder.bustrack.positionstream.impl;

import akka.NotUsed;
import akka.stream.javadsl.Source;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.pathfinder.bustrack.positionstream.api.Position;
import com.pathfinder.bustrack.positionstream.api.PositionStreamService;

import javax.inject.Inject;

public class PositionStreamServiceImpl implements PositionStreamService {

//    private final FriendService friendService;
//    private final ChirpService chirpService;
//
//    @Inject
//    public ActivityStreamServiceImpl(FriendService friendService, ChirpService chirpService) {
//        this.friendService = friendService;
//        this.chirpService = chirpService;
//    }

    @Override
    public ServiceCall<NotUsed, Source<Position, ?>> getLivePositionStream(String busId) {
        return null;
    }

    @Override
    public ServiceCall<NotUsed, Source<Position, ?>> getHistoricalPositionStream(String busId) {
        return null;
    }
}
