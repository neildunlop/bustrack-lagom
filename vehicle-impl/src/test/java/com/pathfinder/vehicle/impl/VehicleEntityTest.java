package com.pathfinder.vehicle.impl;


import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import com.lightbend.lagom.javadsl.testkit.PersistentEntityTestDriver;
import com.pathfinder.vehicle.api.VehicleDetails;
import org.junit.*;
import org.junit.rules.TestName;
import com.lightbend.lagom.javadsl.testkit.PersistentEntityTestDriver.Outcome;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test to confirm the VehicleEntity behaves as expected.
 */
public class VehicleEntityTest {

    //entities are actor based.. but how exactly?
    private static ActorSystem system;

    //setup an in memory persistent entity system (wires up commands, events and state for our target system).
    private PersistentEntityTestDriver<VehicleCommand, VehicleEvent, VehicleState> driver;

    //An easy way of dynamically getting the name of the test method being run.
    @Rule
    public TestName testName = new TestName();

    @BeforeClass
    public static void beforeClass() {
        system = ActorSystem.create("VehicleEntityTest");
    }

    @AfterClass
    public static void afterClass() {
        JavaTestKit.shutdownActorSystem(system);
        system = null;
    }

    @Before
    public void setUp() throws Exception {
        // given a default VehicleEntity
        driver = new PersistentEntityTestDriver<>(
                system, new VehicleEntity(), testName.getMethodName());
    }

    @Test
    public void initialStateShouldBeEmpty() {

        //when we send a GetVehicle command
        final Outcome<VehicleEvent, VehicleState> getPostOutcome = driver.run(VehicleCommand.GetVehicle.INSTANCE);

        //then no events should have been created
        assertThat(getPostOutcome.events()).isEmpty();

        //and the state should still be empty
        assertThat(getPostOutcome.state().getVehicleDetails()).isEmpty();

        // and we should get back an empty Optional to indicate that
        // no vehicle was found
        final Optional<VehicleDetails> actual = getFirstReply(getPostOutcome);
        assertThat(actual).isNotPresent();
    }

    @Test
    public void shouldBeAbleToAddAVehicle() {

        // given entity ID of test name
        final String expectedEntityId = testName.getMethodName();

        //when we send an AddVehicle command
        final Outcome<VehicleEvent, VehicleState> addVehicleOutcome = driver.run(new VehicleCommand.AddVehicle(newVehicleDetails()));     ;

        //then a VehicleAdded event should have been created.
        assertThat(addVehicleOutcome.events()).containsExactly(new VehicleEvent.VehicleAdded(expectedEntityId, newVehicleDetails()));

        // and that the vehicleDetails of the VehicleState is the newly created vehicle.
        assertThat(addVehicleOutcome.state().getVehicleDetails()).hasValue(newVehicleDetails());

        //and that the reply gives is the new entity id (and a populated optional containing the vehicle details).
        final String newEntityId = getFirstReply(addVehicleOutcome);
        assertThat(newEntityId).isEqualTo(expectedEntityId);

        //and when we send a GetVehicle command then the reply should be the vehicle we just created.
        //(how do we specify that we want a specific vehicle?  - how do we pass the id?
        final Outcome<VehicleEvent, VehicleState> getVehicleOutcome = driver.run(VehicleCommand.GetVehicle.INSTANCE);

        final Optional<VehicleDetails> retrievedContent = getFirstReply(getVehicleOutcome);
        assertThat(retrievedContent).hasValue(newVehicleDetails());

    }

    private VehicleDetails newVehicleDetails() {
        return VehicleDetails.builder().chassisNumber("xyz123")
                .make("Optare")
                .model("MetroCity")
                .capacity(55)
                .colour("Red")
                .build();
    }

    @SuppressWarnings("unchecked")
    private static <T> T getFirstReply(final Outcome<?, ?> outcome) {
        return (T) outcome.getReplies().get(0);
    }
}

