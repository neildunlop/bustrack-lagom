package com.pathfinder.position.impl;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import com.lightbend.lagom.javadsl.testkit.PersistentEntityTestDriver;
import com.pathfinder.position.api.PositionDetail;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Instant;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class PositionEntityTest {

    private static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create("PositionEntityTest");
    }

    @AfterClass
    public static void teardown() {
        JavaTestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testAddPosition() {

        //ChirpTopicStub topic = new ChirpTopicStub();
        PersistentEntityTestDriver<PositionCommand, PositionEvent, NotUsed> driver =
                new PersistentEntityTestDriver<>(system, new PositionEntity(), "vehicle1");

        Instant timestamp = Instant.now();
        PositionDetail positionDetail = PositionDetail.builder()
                .chassisNumber("vehicle1")
                .latitude(22.3)
                .longitude(54.6)
                .speed(56)
                .headingDegrees(90)
                .timestamp(timestamp)
                .build();


        PersistentEntityTestDriver.Outcome<PositionEvent, NotUsed> outcome =
                driver.run(new PositionCommand.AddPosition(positionDetail));

        assertEquals(Done.getInstance(), outcome.getReplies().get(0));
        assertEquals(positionDetail, ((PositionEvent.PositionAdded) outcome.events().get(0)).getPositionDetail());
        //assertEquals(positionDetail, topic.chirps.get(0));
        assertEquals(Collections.emptyList(), driver.getAllIssues());
    }
}
