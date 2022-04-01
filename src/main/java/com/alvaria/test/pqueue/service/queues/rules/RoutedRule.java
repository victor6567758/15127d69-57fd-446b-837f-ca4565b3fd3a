package com.alvaria.test.pqueue.service.queues.rules;

import com.alvaria.test.pqueue.model.QueueData;

@FunctionalInterface
public interface RoutedRule {

  boolean enqueueHelper(QueueData queueData);

  default RoutedRule appendNextRoute(RoutedRule nextRule) {
    return queueData -> {
      if (!enqueueHelper(queueData)) {
        return nextRule.enqueueHelper(queueData);
      }
      return false;
    };
  }

}
