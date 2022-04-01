package com.alvaria.test.pqueue.service.queues.rules;

import org.apache.commons.lang3.tuple.Pair;

@FunctionalInterface
public interface DequeuedRule {

  Pair<DequeuedRule, Double> dequeueHelper(double prevPriority, DequeuedRule prevQueue);

  default DequeuedRule appendNextTopQueue(DequeuedRule nextRule) {

    return (prevPriority, prevQueue) -> {
      Pair<DequeuedRule, Double> dequeueEstimation = dequeueHelper(prevPriority, prevQueue);
      return nextRule.dequeueHelper(dequeueEstimation.getRight(), dequeueEstimation.getLeft());
    };
  }

}
