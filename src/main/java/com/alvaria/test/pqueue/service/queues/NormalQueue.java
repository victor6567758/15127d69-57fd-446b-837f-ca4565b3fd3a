package com.alvaria.test.pqueue.service.queues;

import com.alvaria.test.pqueue.model.QueueData;
import com.alvaria.test.pqueue.service.queues.rules.DequeuedRule;
import com.alvaria.test.pqueue.util.Util;
import com.alvaria.test.pqueue.util.pqueue.OrderPriorityQueue;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class NormalQueue extends OrderQueueBase {


  public NormalQueue(OrderPriorityQueue orderPriorityQueue) {
    super(orderPriorityQueue);
  }

  @Override
  protected double calculatePriority(QueueData probeData) {
    return (double) Util.getCurrentSeconds() - probeData.getEnqueueEpochTimeSec();
  }

  public Pair<DequeuedRule, Double> dequeueHelper(double prevPriority, DequeuedRule prevQueue) {
    if (prevQueue == null) {
      return new ImmutablePair<>(this, prevPriority);
    }
    QueueData probeData = orderPriorityQueue.poll();
    if (probeData != null) {
      double curPriority = (double) Util.getCurrentSeconds() - probeData.getEnqueueEpochTimeSec();
      if (curPriority > prevPriority) {
        return new ImmutablePair<>(this, curPriority);
      }
    }

    return new ImmutablePair<>(prevQueue, prevPriority);
  }


  @Override
  public boolean enqueueHelper(QueueData queueData) {
    orderPriorityQueue.enqueue(queueData);
    return true;
  }
}
