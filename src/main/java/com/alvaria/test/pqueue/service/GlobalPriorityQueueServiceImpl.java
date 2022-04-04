package com.alvaria.test.pqueue.service;

import com.alvaria.test.pqueue.model.QueueCategory;
import com.alvaria.test.pqueue.model.QueueData;
import com.alvaria.test.pqueue.util.MaxIterableAdapter;
import com.alvaria.test.pqueue.util.Util;
import com.alvaria.test.pqueue.util.pqueue.OrderPriorityQueue;
import com.alvaria.test.pqueue.util.pqueue.OrderPriorityQueueFactory;
import java.util.ArrayList;
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

  private static final QueueCategory[] SECOND_PRIORITY_QUEUES = {QueueCategory.VIP, QueueCategory.PRIORITY, QueueCategory.NORMAL};
  private static final QueueCategory[] ALL_PRIORITY_QUEUES = {QueueCategory.MANAGEMENT, QueueCategory.VIP, QueueCategory.PRIORITY, QueueCategory.NORMAL};

  private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
  private final ReadLock readLock = lock.readLock();
  private final WriteLock writeLock = lock.writeLock();


  private final OrderPriorityQueue[] queues = new OrderPriorityQueue[]{
      OrderPriorityQueueFactory.create(),
      OrderPriorityQueueFactory.create(),
      OrderPriorityQueueFactory.create(),
      OrderPriorityQueueFactory.create()
  };

  private final int startSec = Util.getNowCurrentEpochSeconds();

  @Override
  public void enqueue(QueueData queueData) {

    checkRequestTime(queueData.getEnqueueTimeSec());

    if (queueData.getId() < 1) {
      throw new IllegalArgumentException("ID must be greater than 0");
    }

    QueueCategory queueType = QueueData.getOrderCategory(queueData);

    writeLock.lock();
    try {
      queues[queueType.getIdx()].enqueue(queueData);
    } finally {
      writeLock.unlock();
    }

    log.debug("Enqueued: {}", queueData);
  }

  @Override
  public QueueData dequeue(int curTimeEpochSec) {
    checkRequestTime(curTimeEpochSec);

    QueueData dequeuedData = null;
    writeLock.lock();
    try {

      if (!queues[QueueCategory.MANAGEMENT.getIdx()].isEmpty()) {
        dequeuedData = queues[QueueCategory.MANAGEMENT.getIdx()].removeFirst();
      } else {
        QueueCategory priorityType = getQueueCategoryWithMaxPriority(curTimeEpochSec);
        if (priorityType != QueueCategory.DUMMY) {
          dequeuedData = queues[priorityType.getIdx()].removeFirst();
        }
      }
    } finally {
      writeLock.unlock();
    }

    if (dequeuedData == null) {
      throw new IllegalArgumentException("Queue is empty");
    }

    if (log.isDebugEnabled()) {
      QueueCategory queueCategory = QueueData.getOrderCategory(dequeuedData);
      double priorityDequeued =
          QueueData.getPriority(queueCategory, curTimeEpochSec - dequeuedData.getEnqueueTimeSec());

      log.debug("Dequeued: {}, priority: {}, category: {}", dequeuedData,
          priorityDequeued, queueCategory);
    }
    return dequeuedData;
  }

  @Override
  public void remove(long id) {
    writeLock.lock();
    try {
      for (QueueCategory queueType : ALL_PRIORITY_QUEUES) {
        if (!queues[queueType.getIdx()].isEmpty() && queues[queueType.getIdx()].remove(id) != null) {
          return;
        }
      }
    } finally {
      writeLock.unlock();
    }
  }

  @Override
  public List<Long> getIdListSortedByPriority(int curTimeEpochSec) {
    checkRequestTime(curTimeEpochSec);
    OrderPriorityQueue managementQueue = queues[QueueCategory.MANAGEMENT.getIdx()];
    List<Long> result = new ArrayList<>();


    readLock.lock();
    try {

      if (!managementQueue.isEmpty()) {
        StreamSupport
            .stream(managementQueue.spliterator(), false)
            .map(QueueData::getId)
            .forEach(result::add);
      }

      StreamSupport.stream(getNonManIterableAdapter(curTimeEpochSec).spliterator(), false)
          .map(QueueData::getId)
          .forEach(result::add);


    } finally {
      readLock.unlock();
    }

    return result;
  }

  @Override
  public int getPosition(long id, int curTimeEpochSec) {
    checkRequestTime(curTimeEpochSec);
    OrderPriorityQueue managementQueue = queues[QueueCategory.MANAGEMENT.getIdx()];

    int otherRank = -1;
    int managementRank = -1;

    readLock.lock();
    try {

      if (!managementQueue.isEmpty()) {
        managementRank = managementQueue.getRankPosition(id);
        if (managementRank >= 0) {
          return managementRank;
        }
      }
      otherRank = getRankPositionInQueue(getNonManIterableAdapter(curTimeEpochSec), id);

    } finally {
      readLock.unlock();
    }

    if (otherRank < 0) {
      throw new IllegalArgumentException("Cannot find a position for Order ID: " + id);
    }

    int result = managementQueue.size() + otherRank;
    log.debug("Found position for data with {}: {}", id, result);

    return result;
  }


  @Override
  public double getAverageWaitTime(int curTimeEpochSec) {

    checkRequestTime(curTimeEpochSec);

    readLock.lock();
    try {

      return Arrays.stream(queues).flatMap(queue -> StreamSupport.stream(queue.spliterator(), false))
          .mapToDouble(value -> curTimeEpochSec - value.getEnqueueTimeSec())
          .average().orElse(-1.0);

    } finally {
      readLock.unlock();
    }
  }

  @Override
  public void clear() {
    writeLock.lock();
    try {
      Arrays.stream(queues).forEach(
          queue -> {
            if (!queue.isEmpty()) {
              queue.clear();
            }
          }
      );
    } finally {
      writeLock.unlock();
    }
  }

  private QueueCategory getQueueCategoryWithMaxPriority(int curTimeEpochSec) {
    double priority = -1.0;
    QueueCategory priorityCategory = QueueCategory.DUMMY;
    for (QueueCategory queueType : SECOND_PRIORITY_QUEUES) {

      QueueData peeked = queues[queueType.getIdx()].isEmpty() ? null : queues[queueType.getIdx()].peekFirst();
      if (peeked != null) {
        double newPriority = QueueData.getPriority(queueType, curTimeEpochSec - peeked.getEnqueueTimeSec());
        if (newPriority > priority) {
          priorityCategory = queueType;
          priority = newPriority;
        }
      }
    }

    return priorityCategory;
  }


  private MaxIterableAdapter<QueueData> getNonManIterableAdapter(int curTimeEpochSec) {
    return new MaxIterableAdapter<>(
        Arrays.stream(SECOND_PRIORITY_QUEUES)
            .map(elem -> (Function<QueueData, Double>) queueData ->
                QueueData.getPriority(elem, curTimeEpochSec - queueData.getEnqueueTimeSec()))
            .collect(Collectors.toList()),
        Arrays.stream(SECOND_PRIORITY_QUEUES).map(elem -> queues[elem.getIdx()].iterator()).collect(Collectors.toList())
    );
  }

  private int getRankPositionInQueue(Iterable<QueueData> iterable, long id) {
    int idx = 0;
    for (QueueData queueData : iterable) {
      if (queueData.getId() == id) {
        return idx;
      }
      idx++;
    }
    return -1;
  }

  private void checkRequestTime(int curTimeEpochSec) {
    if (curTimeEpochSec < startSec) {
      throw new IllegalArgumentException(String.format("Request time: %s must not exceed start time: %s",
          Util.epochSecondToString(curTimeEpochSec), Util.epochSecondToString(startSec)));
    }
  }


}
