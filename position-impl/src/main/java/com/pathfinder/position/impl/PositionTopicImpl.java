package com.pathfinder.position.impl;

import akka.NotUsed;
import akka.stream.javadsl.Source;
import com.lightbend.lagom.javadsl.pubsub.PubSubRef;
import com.lightbend.lagom.javadsl.pubsub.PubSubRegistry;
import com.lightbend.lagom.javadsl.pubsub.TopicId;
import com.pathfinder.position.api.PositionDetail;

import javax.inject.Inject;

/**
 * Wrapper around the pub/sub mechanism that allows us to control sending of position details onto the Kafka topic.
 * We create a topic that is unique to each vehicle and ensure that each is given a unique id based on the class of
 * message and the vehicle id).
 *
 * Implementation of the PositionTopic (appears to use pub/sub internally? - not sure of the distinction between
 * pub/sub and topics.  Two entries in the docs:
 * PubSub - http://www.lagomframework.com/documentation/1.2.x/java/PubSub.html
 * MessageBrokers - http://www.lagomframework.com/documentation/1.2.x/java/MessageBrokerApi.html
 */
public class PositionTopicImpl implements PositionTopic {

    private static final int MAX_TOPICS = 1024;

    //allows us to get hold of the actor ref for a given topic.
    private final PubSubRegistry pubSub;

    @Inject
    public PositionTopicImpl(PubSubRegistry pubSub) {
        this.pubSub = pubSub;
    }

    //publishes a positionDetail to the actor that handles the topic for the vehicle the position relates to.
    @Override
    public void publish(PositionDetail positionDetail) {
        refFor(positionDetail.getChassisNumber()).publish(positionDetail);
    }

    //TODO: Not sure....
    //?? provides access to the subscriber? for the given vehicle position update topic??
    @Override
    public Source<PositionDetail, NotUsed> subscriber(String vehicleId) {
        return refFor(vehicleId).subscriber();
    }

    //gets a reference to the actor that handles the position topic for this vehicle.
    private PubSubRef<PositionDetail> refFor(String vehicleId) {
        return pubSub.refFor(TopicId.of(PositionDetail.class, topicQualifier(vehicleId)));
    }

    //works out the unique id for this vehicles topic based off the vehicleId.
    private String topicQualifier(String vehicleId) {
        return String.valueOf(Math.abs(vehicleId.hashCode()) % MAX_TOPICS);
    }
}
