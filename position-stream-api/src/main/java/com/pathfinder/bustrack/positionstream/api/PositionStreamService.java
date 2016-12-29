package com.pathfinder.bustrack.positionstream.api;


import akka.NotUsed;
import akka.stream.javadsl.Source;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import static com.lightbend.lagom.javadsl.api.Service.*;


//TODO: Might want to rename this to be the activity stream...
//it should maybe expose operations on vehicles as well as postion updates.


public interface PositionStreamService extends Service {

    ServiceCall<NotUsed, Source<Position, ?>> getLivePositionStream(String busId);
    ServiceCall<NotUsed, Source<Position, ?>> getHistoricalPositionStream(String busId);


    @Override
    default Descriptor descriptor() {
        return named("positionstreamservice").withCalls(
                pathCall("/api/position/:chassisId/live", this::getLivePositionStream),
                pathCall("/api/position/:chassisId/historical", this::getHistoricalPositionStream)
        ).withAutoAcl(true);
    }
}
