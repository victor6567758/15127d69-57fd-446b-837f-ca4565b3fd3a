package com.alvaria.test.pqueue.service;

import com.alvaria.test.pqueue.model.QueueData;
import com.alvaria.test.pqueue.model.QueueType;
import com.alvaria.test.pqueue.util.MaxIterableAdapter;
import com.alvaria.test.pqueue.util.Util;
import com.alvaria.test.pqueue.util.pqueue.OrderPriorityQueue;
import com.alvaria.test.pqueue.util.pqueue.OrderPriorityQueueFactory;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GlobalPriorityQueueServiceImpl implements GlobalPriorityQueueService {

  private static final QueueType[] SECOND_PRIORITY_QUEUES = {QueueType.VIP, QueueType.PRIORITY, QueueType.NORMAL};
  private static final QueueType[] ALL_PRIORITY_QUEUES = {QueueType.MANAGEMENT, QueueType.VIP, QueueType.PRIORITY, QueueType.NORMAL};

  private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
  private final ReadLock readLock = lock.readLock();
  private final WriteLock writeLock = lock.writeLock();


  private final OrderPriorityQueue[] queues = new OrderPriorityQueue[]{
      OrderPriorityQueueFactory.create(),
      OrderPriorityQueueFactory.create(),
      OrderPriorityQueueFactory.create(),
      OrderPriorityQueueFactory.create()
  };

  private final int startSec = Util.getCurrentSeconds();


  @Override
  public void enqueue(QueueData queueData) {

    writeLock.lock();
    try {

      if (queueData.getId() < 1) {
        throw new IllegalArgumentException("ID must be greater than 0");
      }

      if (queueData.getEnqueueTimeSec() - startSec < 0) {
        throw new IllegalArgumentException("Cannot insert orders earlier than Queue exists");
      }

      QueueType queueType = QueueData.getOrderType(queueData);
      queues[queueType.getIdx()].enqueue(queueData);

      log.info("Enqueued {}", queueData);

    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public QueueData dequeue() {
    writeLock.lock();
    try {

      if (!queues[QueueType.MANAGEMENT.getIdx()].isEmpty()) {
        return queues[QueueType.MANAGEMENT.getIdx()].removeFirst();
      } else {

        double priority = -1.0;
        QueueType priorityType = QueueType.DUMMY;
        for (QueueType queueType : SECOND_PRIORITY_QUEUES) {

          QueueData peeked = queues[queueType.getIdx()].isEmpty() ? null : queues[queueType.getIdx()].peekFirst();
          if (peeked != null) {
            double newPriority = QueueData.getPriority(queueType, peeked.getEnqueueTimeSec() - startSec);
            if (priority > newPriority) {
              priorityType = queueType;
            }
          }
        }

        if (priorityType != QueueType.DUMMY) {
          return queues[priorityType.getIdx()].removeFirst();
        }

      }

      throw new IllegalArgumentException("Queue is empty");

    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public void remove(long id) {
    writeLock.lock();
    try {
      for (QueueType queueType : ALL_PRIORITY_QUEUES) {
        if (!queues[queueType.getIdx()].isEmpty() && queues[queueType.getIdx()].remove(id) != null) {
          return;
        }
      }
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

      return Arrays.stream(queues).flatMap(queue -> StreamSupport.stream(queue.spliterator(), false))
          .mapToDouble(value -> value.getEnqueueTimeSec() - startSec).average().orElse(-1.0);

    } finally {
      readLock.unlock();
    }
  }

  @Override
  public void clear() {
    writeLock.lock();
    try {
      Arrays.stream(queues).forEach(OrderPriorityQueue::clear);
    } finally {
      writeLock.unlock();
    }
  }


  private MaxIterableAdapter<QueueData> getIterableAdapter() {
    return new MaxIterableAdapter<>(
        Arrays.stream(ALL_PRIORITY_QUEUES)
            .map(elem -> (Function<QueueData, Double>) queueData -> QueueData.getPriority(elem, queueData.getEnqueueTimeSec() - startSec))
            .collect(Collectors.toList()),
        Arrays.stream(ALL_PRIORITY_QUEUES).map(elem -> queues[elem.getIdx()].iterator()).collect(Collectors.toList())
    );

  }


}
