package com.alvaria.test.pqueue.service.queues;

import com.alvaria.test.pqueue.model.QueueData;
import com.alvaria.test.pqueue.util.Util;
import com.alvaria.test.pqueue.util.pqueue.OrderPriorityQueue;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NormalDecoratorQueue extends DecoratorQueueBase {


  public NormalDecoratorQueue(OrderPriorityQueue orderPriorityQueue) {
    super(orderPriorityQueue);
  }

  @Override
  public double calculatePriority(QueueData probeData) {
    double priority = (double) Util.getCurrentSeconds() - probeData.getEnqueueEpochTimeSec();
    log.debug("Returned priority {} as normal queue", priority);

    return priority;
  }


  @Override
  public boolean enqueueHelper(QueueData queueData) {
    orderPriorityQueue.enqueue(queueData);
    return true;
  }
}
