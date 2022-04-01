package com.alvaria.test.pqueue.service;

import com.alvaria.test.pqueue.model.QueueData;
import com.alvaria.test.pqueue.service.queues.ManagementQueue;
import com.alvaria.test.pqueue.service.queues.NormalQueue;
import com.alvaria.test.pqueue.service.queues.OrderQueueBase;
import com.alvaria.test.pqueue.service.queues.PrioritizedQueue;
import com.alvaria.test.pqueue.service.queues.VipQueue;
import com.alvaria.test.pqueue.service.queues.rules.DequeuedRule;
import com.alvaria.test.pqueue.service.queues.rules.RemoveRule;
import com.alvaria.test.pqueue.service.queues.rules.RoutedRule;
import com.alvaria.test.pqueue.util.OrderMaxRankIterableAdapter;
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
public class GlobalPriorityQueueImpl implements GlobalPriorityQueue {

  private static final int ID_MANAGEMENT_QUEUE = 0;
  private static final int ID_NORMAL_QUEUE = 1;
  private static final int ID_PRIORITY_QUEUE = 2;
  private static final int ID_VIP_QUEUE = 3;

  private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
  private final ReadLock readLock = lock.readLock();
  private final WriteLock writeLock = lock.writeLock();

  private final ManagementQueue managementQueue = new ManagementQueue(OrderPriorityQueueFactory.create());
  private final PrioritizedQueue prioritizedQueue = new PrioritizedQueue(OrderPriorityQueueFactory.create());
  private final VipQueue vipQueue = new VipQueue(OrderPriorityQueueFactory.create());
  private final NormalQueue normalQueue = new NormalQueue(OrderPriorityQueueFactory.create());

  private final RoutedRule routedRule = managementQueue
      .appendNextRoute(prioritizedQueue)
      .appendNextRoute(vipQueue)
      .appendNextRoute(normalQueue);

  private final DequeuedRule dequeuedRule = managementQueue
      .appendNextTopQueue(prioritizedQueue)
      .appendNextTopQueue(vipQueue)
      .appendNextTopQueue(normalQueue);

  private final RemoveRule removeRule = managementQueue
      .appendNextRemove(prioritizedQueue)
      .appendNextRemove(vipQueue)
      .appendNextRemove(normalQueue);

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
      DequeuedRule topQueue = dequeuedRule.dequeueHelper(-1, null).getLeft();
      if (topQueue == null) {
        throw new IllegalArgumentException("No data in the queue");
      }

      return ((OrderQueueBase) topQueue).getOrderPriorityQueue().dequeue();

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
  public List<QueueData> getList() {
    readLock.lock();
    try {
      OrderMaxRankIterableAdapter<QueueData> adapter = new OrderMaxRankIterableAdapter<>(
          (o1, o2) -> o2.getEnqueueEpochTimeSec() - o1.getEnqueueEpochTimeSec(),
          Arrays.asList(
              managementQueue.getOrderPriorityQueue().iterator(),
              prioritizedQueue.getOrderPriorityQueue().iterator(),
              vipQueue.getOrderPriorityQueue().iterator(),
              normalQueue.getOrderPriorityQueue().iterator()
          ));

      return StreamSupport.stream(adapter.spliterator(), false).collect(Collectors.toList());

    } finally {
      readLock.unlock();
    }

  }

  @Override
  public int getPosition(long id) {
    readLock.lock();
    try {

    } finally {
      readLock.unlock();
    }
    return -1;
  }


  @Override
  public double avrSeqWaitTime(long currentEpochTimeSec) {
    readLock.lock();
    try {
      OrderMaxRankIterableAdapter<QueueData> adapter = new OrderMaxRankIterableAdapter<>(
          (o1, o2) -> o2.getEnqueueEpochTimeSec() - o1.getEnqueueEpochTimeSec(),
          Arrays.asList(
              managementQueue.getOrderPriorityQueue().iterator(),
              prioritizedQueue.getOrderPriorityQueue().iterator(),
              vipQueue.getOrderPriorityQueue().iterator(),
              normalQueue.getOrderPriorityQueue().iterator()
          ));

      return StreamSupport.stream(adapter.spliterator(), false)
          .mapToDouble(value -> currentEpochTimeSec - value.getEnqueueEpochTimeSec()).average().orElse(0.0);

    } finally {
      readLock.unlock();
    }
  }


}
