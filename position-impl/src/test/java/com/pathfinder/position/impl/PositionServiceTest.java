package com.pathfinder.position.impl;

import akka.Done;
import akka.stream.javadsl.Source;
import akka.stream.testkit.TestSubscriber.Probe;
import akka.stream.testkit.javadsl.TestSink;
import com.pathfinder.position.api.HistoricalPositionRequest;
import com.pathfinder.position.api.LivePositionRequest;
import com.pathfinder.position.api.PositionDetail;
import com.pathfinder.position.api.PositionService;
import org.joda.time.DateTime;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pcollections.PSequence;
import org.pcollections.TreePVector;
import scala.concurrent.duration.FiniteDuration;

import java.time.Instant;

import static com.lightbend.lagom.javadsl.testkit.ServiceTest.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;

//This is an integration test... fires up a lot of the system..
public class PositionServiceTest {

    private static TestServer server;

    @BeforeClass
    public static void setUp() {
        server = startServer(defaultSetup().withCassandra(true));
    }

    @AfterClass
    public static void tearDown() {
        server.stop();
        server = null;
    }

//    @Test
//    public void shouldAddPosition() throws Exception {
//        withServer(defaultSetup().withCassandra(true), server ->{
//            PositionService service = server.client(PositionService.class);
//
//            PositionDetail positionDetail1 = PositionDetail.builder()
//                    .chassisNumber("123")
//                    .headingDegrees(90)
//                    .latitude(22.5)
//                    .longitude(50.2)
//                    .speed(22)
//                    .timestamp(Instant.now())
//                    .build();
//
//            service.addPosition("123").invoke(positionDetail1).toCompletableFuture().get(5, SECONDS);
//
//        });
//    }

    @Test
    public void shouldAddPosition() throws Exception {
        PositionService service = server.client(PositionService.class);

        PositionDetail positionDetail1 = PositionDetail.builder()
                .chassisNumber("123")
                .headingDegrees(90)
                .latitude(22.5)
                .longitude(50.2)
                .speed(22)
                .timestamp(Instant.now())
                .build();

        Done response = service.addPosition("123").invoke(positionDetail1).toCompletableFuture().get(5, SECONDS);
        assertEquals(Done.getInstance(), response);
    }


    //The below tests are streaming service responses.. see how to test them here:
    //http://www.lagomframework.com/documentation/1.2.x/java/Test.html

//    @Test
//    public void shouldPublishPositionDetailsToSubscribers() throws Exception {
//
//        PositionService positionService = server.client(PositionService.class);
//        PSequence<String> vehicleIds = TreePVector.empty();
//        vehicleIds = vehicleIds.plus("123");
//        LivePositionRequest request = new LivePositionRequest(vehicleIds);
//
//        //What is this?  Its not where we add positions?  Is it just a way of setting up the test probe?
//        Source<PositionDetail, ?> positionDetailSource1 = positionService.getLivePosition().invoke(request).toCompletableFuture().get(3, SECONDS);
//
//        //A test subscriber...
//        Probe<PositionDetail> probe1 = positionDetailSource1.runWith(TestSink.probe(server.system()), server.materializer());
//        //tell the test subscriber to listen for 10 seconds at most...
//        probe1.request(10);
//
//        //What is this?  Its not where we add positions?  Is it just a way of setting up the test probe?
//        Source<PositionDetail, ?> positionDetailSource2 = positionService.getLivePosition().invoke(request).toCompletableFuture().get(3, SECONDS);
//        Probe<PositionDetail> probe2 = positionDetailSource2.runWith(TestSink.probe(server.system()), server.materializer());
//        probe2.request(10);
//
//
//        //send a test position update...
//        PositionDetail positionDetail1 = PositionDetail.builder()
//                .chassisNumber("123")
//                .headingDegrees(90)
//                .latitude(22.5)
//                .longitude(50.2)
//                .speed(22)
//                .timestamp(Instant.now())
//                .build();
//
//        positionService.addPosition("123").invoke(positionDetail1).toCompletableFuture().get(3, SECONDS);
//        probe1.expectNext(positionDetail1);
//        probe2.expectNext(positionDetail1);
//
//        //shutdown after our test
//        probe1.cancel();
//        probe2.cancel();
//
//
//    }
//
//    @Test
//    public void shouldRetrieveOldPositions() throws Exception {
//        PositionService positionService = server.client(PositionService.class);
//
//        Instant timestamp1 = Instant.now();
//        Instant timestamp2 = Instant.now().plusSeconds(2);
//
//        PositionDetail positionDetail1 = PositionDetail.builder()
//                .chassisNumber("vehicle1")
//                .latitude(22.3)
//                .longitude(35.7)
//                .headingDegrees(90)
//                .speed(55)
//                .timestamp(timestamp1)
//                .build();
//
//        PositionDetail positionDetail2 = PositionDetail.builder()
//                .chassisNumber("vehicle1")
//                .latitude(42.3)
//                .longitude(65.7)
//                .headingDegrees(97)
//                .speed(64)
//                .timestamp(timestamp2)
//                .build();
//
//        positionService.addPosition("vehicle1").invoke(positionDetail1).toCompletableFuture().get(3, SECONDS);
//        positionService.addPosition("vehicle1").invoke(positionDetail1).toCompletableFuture().get(3, SECONDS);
//
//        HistoricalPositionRequest request = new HistoricalPositionRequest(TreePVector.<String>empty().plus("vehicle1"), Instant.now().minusSeconds(20));
//
//        eventually(FiniteDuration.create(10, SECONDS), () -> {
//            Source<PositionDetail, ?> positionDetails = positionService.getHistoricalPosition().invoke(request).toCompletableFuture().get(3, SECONDS);
//            Probe<PositionDetail> probe = positionDetails.runWith(TestSink.probe(server.system()), server.materializer());
//            probe.request(10);
//            probe.expectNextUnordered(positionDetail1, positionDetail2);
//            probe.expectComplete();
//        });
//    }
}
