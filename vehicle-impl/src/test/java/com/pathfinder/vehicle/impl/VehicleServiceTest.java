package com.pathfinder.vehicle.impl;

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.lightbend.lagom.javadsl.testkit.ServiceTest.defaultSetup;
import static com.lightbend.lagom.javadsl.testkit.ServiceTest.withServer;
import static org.assertj.core.api.Assertions.assertThat;

import com.pathfinder.vehicle.api.VehicleDetails;
import com.pathfinder.vehicle.api.VehicleService;
import org.junit.Test;

/**
 * Integration tests for the vehicle microservice.
 *
 */
public class VehicleServiceTest {

    @Test
    public void testBlogServices() throws Exception {
        withServer(defaultSetup().withCassandra(true), server -> {
            final VehicleService service = server.client(VehicleService.class);

            unknownVehicleIdShouldBeEmpty(service);

            final String id = addVehicleAndGetId(service);
            verifyVehicleIdHasVehicleDetails(service, id, newVehicleDetails());

            updateVehicleShouldUpdateVehicleDetails(service, id, newVehicleDetails().withColour("Green"));
        });
    }

    private static void unknownVehicleIdShouldBeEmpty(final VehicleService service) throws Exception {
        // when we look up an arbitrary unknown vehicle
        final Optional<VehicleDetails> response =
                getOrTimeout(service.getVehicle("unknown entity id").invoke());

        // then the response should be empty
        assertThat(response).isNotPresent();
    }

    private static String addVehicleAndGetId(final VehicleService service) throws Exception {
        // when we add a vehicle
        final String id = getOrTimeout(service.addVehicle().invoke(newVehicleDetails()));

        // then the id should be a UUID
        assertThat(id).matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");

        return id;
    }

    private static void updateVehicleShouldUpdateVehicleDetails(final VehicleService service, final String id,
                                                      final VehicleDetails vehicleDetails) throws Exception {
        // when we update a vehicle
        getOrTimeout(service.updateVehicle(id).invoke(vehicleDetails));

        // then the contents should be updated
        verifyVehicleIdHasVehicleDetails(service, id, vehicleDetails);
    }

    private static void verifyVehicleIdHasVehicleDetails(final VehicleService service, final String id,
                                               final VehicleDetails vehicleDetails) throws Exception {
        // when we look up the vehicle ID
        final Optional<VehicleDetails> response = getOrTimeout(service.getVehicle(id).invoke());

        // then the vehicle details should match
        assertThat(response).hasValue(vehicleDetails);
    }

    private static VehicleDetails newVehicleDetails() {
        return VehicleDetails.builder().chassisNumber("xyz123")
                .make("Optare")
                .model("MetroCity")
                .capacity(55)
                .colour("Red")
                .build();
    }

    private static <T> T getOrTimeout(final CompletionStage<T> stage)
            throws InterruptedException, ExecutionException, TimeoutException {
        return stage.toCompletableFuture().get(5, TimeUnit.SECONDS);
    }
}
