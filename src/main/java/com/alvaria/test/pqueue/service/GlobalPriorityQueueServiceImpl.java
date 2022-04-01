package com.alvaria.test.pqueue.service;

import com.alvaria.test.pqueue.model.QueueData;
import com.alvaria.test.pqueue.service.queues.ManagementQueue;
import com.alvaria.test.pqueue.service.queues.NormalQueue;
import com.alvaria.test.pqueue.service.queues.OrderQueueBase;
import com.alvaria.test.pqueue.service.queues.PrioritizedQueue;
import com.alvaria.test.pqueue.service.queues.VipQueue;
import com.alvaria.test.pqueue.service.queues.rules.DequeueRule;
import com.alvaria.test.pqueue.service.queues.rules.RemoveRule;
import com.alvaria.test.pqueue.service.queues.rules.EnqueueRule;
import com.alvaria.test.pqueue.util.MaxIterableAdapter;
import com.alvaria.test.pqueue.util.pqueue.OrderPriorityQueueFactory;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.stereotype.Service;

@Service
public class GlobalPriorityQueueServiceImpl implements GlobalPriorityQueueService {

  private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
  private final ReadLock readLock = lock.readLock();
  private final WriteLock writeLock = lock.writeLock();

  private final ManagementQueue managementQueue = new ManagementQueue(OrderPriorityQueueFactory.create());
  private final PrioritizedQueue prioritizedQueue = new PrioritizedQueue(OrderPriorityQueueFactory.create());
  private final VipQueue vipQueue = new VipQueue(OrderPriorityQueueFactory.create());
  private final NormalQueue normalQueue = new NormalQueue(OrderPriorityQueueFactory.create());

  private final EnqueueRule routedRule = managementQueue
      .appendNextEnqueueRule(prioritizedQueue)
      .appendNextEnqueueRule(vipQueue)
      .appendNextEnqueueRule(normalQueue);

  private final DequeueRule dequeuedRule = managementQueue
      .appendNextDequeueRule(prioritizedQueue)
      .appendNextDequeueRule(vipQueue)
      .appendNextDequeueRule(normalQueue);

  private final RemoveRule removeRule = managementQueue
      .appendNextRemoveRule(prioritizedQueue)
      .appendNextRemoveRule(vipQueue)
      .appendNextRemoveRule(normalQueue);


  @Override
  public void enqueue(QueueData queueData) {

    writeLock.lock();
    try {
      if (queueData.getId() < 1) {
        throw new IllegalArgumentException("ID must be more than 0");
      }

      routedRule.enqueueHelper(queueData);

    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public QueueData dequeue() {
    writeLock.lock();
    try {
      DequeueRule topRule = dequeuedRule.dequeueHelper(-1, null).getLeft();
      if (topRule == null) {
        throw new IllegalArgumentException("No data in the queue");
      }

      return ((OrderQueueBase) topRule).getOrderPriorityQueue().removeFirst();

    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public void remove(long id) {
    writeLock.lock();
    try {
      removeRule.removeHelper(id);
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public List<Long> getIdListSortedByPriority() {
    readLock.lock();
    try {

      return StreamSupport.stream(getIterableAdapter().spliterator(), false)
          .map(QueueData::getId)
          .collect(Collectors.toList());

    } finally {
      readLock.unlock();
    }

  }

  @Override
  public int getPosition(long id) {
    readLock.lock();
    try {

      int[] sequenceNum = new int[1];
      StreamSupport.stream(getIterableAdapter().spliterator(), false)
          .takeWhile(queueData -> queueData.getId() != id).forEach(elem -> sequenceNum[0]++);

      return sequenceNum[0];
    } finally {
      readLock.unlock();
    }
  }


  @Override
  public double getAverageWaitTime(long currentEpochTimeSec) {
    readLock.lock();
    try {
      return (getQueueAvr(managementQueue, currentEpochTimeSec) +
          getQueueAvr(prioritizedQueue, currentEpochTimeSec) +
          getQueueAvr(normalQueue, currentEpochTimeSec) +
          getQueueAvr(vipQueue, currentEpochTimeSec)) / 4.0;

    } finally {
      readLock.unlock();
    }
  }

  private double getQueueAvr(OrderQueueBase queue, long currentEpochTimeSec) {
    return  StreamSupport.stream(queue.getOrderPriorityQueue().spliterator(), false)
        .mapToDouble(value -> currentEpochTimeSec - value.getEnqueueEpochTimeSec()).average().orElse(0.0);
  }

  private MaxIterableAdapter<QueueData> getIterableAdapter() {
    return new MaxIterableAdapter<>(
        (o1, o2) -> o2.getEnqueueEpochTimeSec() - o1.getEnqueueEpochTimeSec(),
        Arrays.asList(
            managementQueue.getOrderPriorityQueue().iterator(),
            prioritizedQueue.getOrderPriorityQueue().iterator(),
            vipQueue.getOrderPriorityQueue().iterator(),
            normalQueue.getOrderPriorityQueue().iterator()
        ));
  }

}
