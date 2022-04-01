package com.alvaria.test.pqueue.service.queues.rules;

import com.alvaria.test.pqueue.model.QueueData;

@FunctionalInterface
public interface EnqueueRule {

  boolean enqueueHelper(QueueData queueData);

  default EnqueueRule appendNextEnqueueRule(EnqueueRule nextRule) {
    return queueData -> {
      if (!enqueueHelper(queueData)) {
        return nextRule.enqueueHelper(queueData);
      }
      return true;
    };
  }

}
