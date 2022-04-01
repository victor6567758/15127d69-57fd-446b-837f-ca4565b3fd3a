package com.alvaria.test.pqueue.service.queues.rules;

import org.apache.commons.lang3.tuple.Pair;

@FunctionalInterface
public interface DequeueRule {

  Pair<DequeueRule, Double> dequeueHelper(double prevPriority, DequeueRule prevQueue);

  default DequeueRule appendNextDequeueRule(DequeueRule nextRule) {

    return (prevPriority, prevQueue) -> {
      Pair<DequeueRule, Double> dequeueEstimation = dequeueHelper(prevPriority, prevQueue);
      return nextRule.dequeueHelper(dequeueEstimation.getRight(), dequeueEstimation.getLeft());
    };
  }

}
