package com.pathfinder.position.impl;

import akka.NotUsed;
import akka.stream.javadsl.Source;
import com.pathfinder.position.api.PositionDetail;

/**
 * Allows you to publish position details to subscribers (outside this service?).
 */
interface PositionTopic {
    /**
     * Publishes the provided position detail to subscribers based on the vehicle ID within the position detail.
     *
     * @param positionDetail the positionDetail to publish
     */
    void publish(PositionDetail positionDetail);

    /**
     * Returns a source of positions published by the provided vehicle ID.
     *
     * @param vehicleId the ID of the vehicle whose position updates the caller is subscribing to
     * @return a continuous source of positionDetails for the provided vehicle
     */
    Source<PositionDetail, NotUsed> subscriber(String vehicleId);
}
