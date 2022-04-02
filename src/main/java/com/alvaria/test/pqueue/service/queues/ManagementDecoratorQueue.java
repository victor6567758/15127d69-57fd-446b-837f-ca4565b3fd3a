package com.alvaria.test.pqueue.service.queues;

import com.alvaria.test.pqueue.model.QueueData;
import com.alvaria.test.pqueue.service.queues.rules.DequeueRule;
import com.alvaria.test.pqueue.util.pqueue.OrderPriorityQueue;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

@Slf4j
public class ManagementDecoratorQueue extends DecoratorQueueBase {

  public ManagementDecoratorQueue(OrderPriorityQueue orderPriorityQueue) {
    super(orderPriorityQueue);
  }

  @Override
  public double calculatePriority(QueueData probeData) {
    log.debug("Returned max priority as management queue");
    return Double.MAX_VALUE;
  }

  @Override
  public Pair<DequeueRule, Double> dequeueHelper(double prevPriority, DequeueRule prevQueue) {
    return new ImmutablePair<>(this, Double.MAX_VALUE);
  }


  @Override
  public boolean enqueueHelper(QueueData queueData) {
    if (queueData.getId() % 3 == 0 && queueData.getId() % 5 == 0) {
      orderPriorityQueue.enqueue(queueData);
      return true;
    }
    return false;
  }
}
