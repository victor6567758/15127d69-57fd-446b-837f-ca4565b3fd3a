package com.alvaria.test.pqueue.service.queues;

import com.alvaria.test.pqueue.model.QueueData;
import com.alvaria.test.pqueue.service.queues.rules.DequeuedRule;
import com.alvaria.test.pqueue.util.pqueue.OrderPriorityQueue;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class ManagementQueue extends OrderQueueBase {

  public ManagementQueue(OrderPriorityQueue orderPriorityQueue) {
    super(orderPriorityQueue);
  }

  @Override
  public Pair<DequeuedRule, Double> dequeueHelper(double prevPriority, DequeuedRule prevQueue) {
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
