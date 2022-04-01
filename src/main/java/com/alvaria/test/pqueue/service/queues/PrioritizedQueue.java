package com.alvaria.test.pqueue.service.queues;

import com.alvaria.test.pqueue.model.QueueData;
import com.alvaria.test.pqueue.util.Util;
import com.alvaria.test.pqueue.util.pqueue.OrderPriorityQueue;

public class PrioritizedQueue extends OrderQueueBase {

  public PrioritizedQueue(OrderPriorityQueue orderPriorityQueue) {
    super(orderPriorityQueue);
  }

  @Override
  protected double calculatePriority(QueueData probeData) {
    long sec = Util.getCurrentSeconds() - probeData.getEnqueueEpochTimeSec();
    return sec > 0 ? Math.max(3.0, sec * Math.log(sec)) : 3.0;
  }


  @Override
  public boolean enqueueHelper(QueueData queueData) {
    if (queueData.getId() % 3 == 0) {
      orderPriorityQueue.enqueue(queueData);
      return true;
    }
    return false;
  }
}
