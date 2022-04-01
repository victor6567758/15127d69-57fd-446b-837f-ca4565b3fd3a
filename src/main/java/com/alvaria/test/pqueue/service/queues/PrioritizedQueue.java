package com.alvaria.test.pqueue.service.queues;

import com.alvaria.test.pqueue.model.QueueData;
import com.alvaria.test.pqueue.service.queues.rules.DequeuedRule;
import com.alvaria.test.pqueue.util.Util;
import com.alvaria.test.pqueue.util.pqueue.OrderPriorityQueue;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class PrioritizedQueue extends OrderQueueBase {

  public PrioritizedQueue(OrderPriorityQueue orderPriorityQueue) {
    super(orderPriorityQueue);
  }

  @Override
  protected double calculatePriority(QueueData probeData) {
    long sec = Util.getCurrentSeconds() - probeData.getEnqueueEpochTimeSec();
    return sec > 0 ? Math.max(3.0, sec * Math.log(sec)) : 3.0;
  }

  public Pair<DequeuedRule, Double> dequeueHelper(double prevPriority, DequeuedRule prevQueue) {
    if (prevQueue == null) {
      return new ImmutablePair<>(this, prevPriority);
    }

    QueueData probeData = orderPriorityQueue.poll();
    if (probeData != null) {
      long sec = Util.getCurrentSeconds() - probeData.getEnqueueEpochTimeSec();
      double curPriority = sec > 0 ? Math.max(3.0, sec * Math.log(sec)) : 3.0;

      if (curPriority > prevPriority) {
        return new ImmutablePair<>(this, curPriority);
      }
    }

    return new ImmutablePair<>(prevQueue, prevPriority);
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
