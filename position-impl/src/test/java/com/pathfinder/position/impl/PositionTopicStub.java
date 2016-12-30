package com.pathfinder.position.impl;

import akka.NotUsed;
import akka.stream.javadsl.Source;
import com.pathfinder.position.api.PositionDetail;

import java.util.ArrayList;
import java.util.List;

class PositionTopicStub implements PositionTopic {

    final List<PositionDetail> positionDetails = new ArrayList<>();

    @Override
    public void publish(PositionDetail positionDetail) {
        positionDetails.add(positionDetail);
    }

    @Override
    public Source<PositionDetail, NotUsed> subscriber(String userId) {
        return Source.from(positionDetails);
    }
}
