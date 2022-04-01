package com.alvaria.test.pqueue.service.queues;

import com.alvaria.test.pqueue.model.QueueData;
import com.alvaria.test.pqueue.util.Util;
import com.alvaria.test.pqueue.util.pqueue.OrderPriorityQueue;

public class NormalQueue extends OrderQueueBase {


  public NormalQueue(OrderPriorityQueue orderPriorityQueue) {
    super(orderPriorityQueue);
  }

  @Override
  protected double calculatePriority(QueueData probeData) {
    return (double) Util.getCurrentSeconds() - probeData.getEnqueueEpochTimeSec();
  }


  @Override
  public boolean enqueueHelper(QueueData queueData) {
    orderPriorityQueue.enqueue(queueData);
    return true;
  }
}
