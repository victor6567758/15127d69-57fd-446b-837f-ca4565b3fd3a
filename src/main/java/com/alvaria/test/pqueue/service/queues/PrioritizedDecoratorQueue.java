package com.alvaria.test.pqueue.service.queues;

import com.alvaria.test.pqueue.model.QueueData;
import com.alvaria.test.pqueue.util.Util;
import com.alvaria.test.pqueue.util.pqueue.OrderPriorityQueue;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PrioritizedDecoratorQueue extends DecoratorQueueBase {

  public PrioritizedDecoratorQueue(OrderPriorityQueue orderPriorityQueue) {
    super(orderPriorityQueue);
  }

  @Override
  public double calculatePriority(QueueData probeData) {
    int sec = Util.getCurrentSeconds() - probeData.getEnqueueEpochTimeSec();
    double priority = sec > 0 ? Math.max(3.0, sec * Math.log(sec)) : 3.0;
    log.debug("Returned priority {} as prioritized queue", priority);

    return priority;
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
