package com.pathfinder.position.api;

import akka.Done;
import akka.stream.javadsl.Source;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;

//this is important for the descriptor and it wont autoimport!
import static com.lightbend.lagom.javadsl.api.Service.*;


/**
 * Exposes the vehicle position microservice API.
 */
public interface PositionService extends Service {

    ServiceCall<PositionDetail, Done> addPosition(String vehicleId);

    //The responses here are an asynchronous streamed response - anything with 'Source' is a streamed response.
    //The first type for the Source generic is the type of the actual response, the second type argument
    //is the materialiser that will be used to actually execute the stream.. we use a wildcard so that Lagom can use
    // whatever materializer it deems appropriate.  In short, Lagom sorts out the materializer...(this is a bit wooly).
    ServiceCall<LivePositionRequest, Source<PositionDetail, ?>> getLivePosition();
    ServiceCall<HistoricalPositionRequest, Source<PositionDetail, ?>> getHistoricalPosition();


    @Override
    default Descriptor descriptor() {
        return named("positionservice").withCalls(
                pathCall("/api/position/live/:vehicleId", this::addPosition),
                namedCall("/api/position/live", this::getLivePosition),
                //TODO: How do the parameters get passed to the method? How is the HistoricalPositionRequest generated.
                namedCall("/api/position/history", this::getHistoricalPosition)
        ).withAutoAcl(true);
    }
}

//TODO: What is the difference between a pathCall, a namedCall and a restCall?
//Read this: http://www.lagomframework.com/documentation/1.2.x/java/ServiceDescriptors.html
