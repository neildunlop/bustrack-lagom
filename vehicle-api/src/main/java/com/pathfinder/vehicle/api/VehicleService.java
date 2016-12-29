package com.pathfinder.vehicle.api;

import akka.Done;
import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.Method;

import java.util.Optional;

//this is important for the descriptor and it wont autoimport!
import static com.lightbend.lagom.javadsl.api.Service.*;


/**
 * Exposes the fleet (vehicle) microservice API.
 */
public interface VehicleService extends Service {

    ServiceCall<NotUsed, Optional<VehicleDetails>> getVehicle(String id);

    ServiceCall<VehicleDetails, String> addVehicle();

    ServiceCall<VehicleDetails, Done> updateVehicle(String id);

    @Override
    default Descriptor descriptor() {
        return named("fleet").withCalls(
                restCall(Method.GET, "/api/vehicle/:id", this::getVehicle),
                restCall(Method.POST, "/api/vehicle/", this::addVehicle),
                restCall(Method.PUT, "/api/vehicle/:id", this::updateVehicle)
        ).withAutoAcl(true);
    }
}
