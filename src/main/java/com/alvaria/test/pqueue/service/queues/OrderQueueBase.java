package com.alvaria.test.pqueue.service.queues;

import com.alvaria.test.pqueue.model.QueueData;
import com.alvaria.test.pqueue.service.queues.rules.DequeuedRule;
import com.alvaria.test.pqueue.service.queues.rules.RemoveRule;
import com.alvaria.test.pqueue.service.queues.rules.RoutedRule;
import com.alvaria.test.pqueue.util.pqueue.OrderPriorityQueue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

@RequiredArgsConstructor
@Getter
public abstract class OrderQueueBase implements DequeuedRule, RoutedRule, RemoveRule {

  protected final OrderPriorityQueue orderPriorityQueue;

  protected double calculatePriority(QueueData probeData) {
    return -1;
  }

  @Override
  public abstract boolean enqueueHelper(QueueData queueData);

  @Override
  public boolean removeHelper(long id) {
    return orderPriorityQueue.remove(id) != null;
  }

  @Override
  public Pair<DequeuedRule, Double> dequeueHelper(double prevPriority, DequeuedRule prevQueue) {
    if (prevQueue == null) {
      return new ImmutablePair<>(this, prevPriority);
    }
    QueueData probeData = orderPriorityQueue.poll();
    if (probeData != null) {
      double curPriority = calculatePriority(probeData);
      if (curPriority > prevPriority) {
        return new ImmutablePair<>(this, curPriority);
      }
    }

    return new ImmutablePair<>(prevQueue, prevPriority);
  }
}
