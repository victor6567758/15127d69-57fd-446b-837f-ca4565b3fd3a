package com.alvaria.test.pqueue.service;

import com.alvaria.test.pqueue.model.QueueData;
import com.alvaria.test.pqueue.util.Util;
import com.alvaria.test.pqueue.util.pqueue.PriorityQueue;
import com.alvaria.test.pqueue.util.pqueue.PriorityQueueImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.springframework.stereotype.Service;

@Service
public class GlobalPriorityQueueImpl implements PriorityQueue {

  private final PriorityQueue normalQueue = new PriorityQueueImpl();

  private final Function<QueueData, Double> normalQueueRankFun = queueData -> {
    if (queueData == null) {
      return -1.0;
    }
    return (double) Util.getCurrentSeconds() - queueData.getEnqueueEpochTimeSec();
  };


  private final PriorityQueue priorityQueue = new PriorityQueueImpl();

  private final Function<QueueData, Double> priorityQueueRankFun = queueData -> {
    if (queueData == null) {
      return -1.0;
    }

    long sec = Util.getCurrentSeconds() - queueData.getEnqueueEpochTimeSec();
    return sec > 0 ? Math.max(3.0, sec * Math.log(sec)) : 3.0;
  };


  private final PriorityQueue vipQueue = new PriorityQueueImpl();

  private final Function<QueueData, Double> vipQueueRankFun = queueData -> {
    if (queueData == null) {
      return -1.0;
    }

    long sec = Util.getCurrentSeconds() - queueData.getEnqueueEpochTimeSec();
    return sec > 0 ? Math.max(4.0, 2 * sec * Math.log(sec)) : 4.0;
  };

  private final PriorityQueue managementQueue = new PriorityQueueImpl();

  @Override
  public void enqueue(QueueData queueData) {

    if (queueData.getId() < 1) {
      throw new IllegalArgumentException("ID must be more than 0");
    }

    long id = queueData.getId();

    boolean divisibleBy3 = (id % 3 == 0);
    boolean divisibleBy5 = (id % 5 == 0);

    if (divisibleBy3 && divisibleBy5) {
      managementQueue.enqueue(queueData);
    } else if (divisibleBy3) {
      priorityQueue.enqueue(queueData);
    } else if (divisibleBy5) {
      vipQueue.enqueue(queueData);
    } else {
      normalQueue.enqueue(queueData);
    }
  }

  @Override
  public QueueData dequeue() {
    return resolveTopPriorityQueue().dequeue();
  }

  @Override
  public QueueData poll() {
    throw new UnsupportedOperationException("Method is not implemented");
  }

  @Override
  public List<QueueData> getList() {

    List<QueueData> result = new ArrayList<>(managementQueue.getList());

    List<QueueData> normalList = normalQueue.getList();
    List<QueueData> priorityList = priorityQueue.getList();
    List<QueueData> vipList = vipQueue.getList();

    int idxNormal = 0;
    int idxPriority = 0;
    int idxVip = 0;

    while (idxNormal < normalList.size() || idxPriority < priorityList.size() || idxVip < vipList.size()) {

      QueueData normaData = idxNormal < normalList.size() ? normalList.get(idxNormal) : null;
      QueueData priorityData = idxPriority < priorityList.size() ? priorityList.get(idxPriority) : null;
      QueueData vipData = idxVip < vipList.size() ? vipList.get(idxVip) : null;

      double normaDataPriority = normalQueueRankFun.apply(normaData);
      double priorityDataPriority = priorityQueueRankFun.apply(priorityData);
      double vipDataPriority = vipQueueRankFun.apply(vipData);

      if (normaData != null && normaDataPriority > priorityDataPriority && normaDataPriority > vipDataPriority) {
        result.add(normaData);
        idxNormal++;
      } else if (priorityData != null && priorityDataPriority > normaDataPriority && priorityDataPriority > vipDataPriority) {
        result.add(priorityData);
        idxPriority++;
      } else if (vipData != null) {
        result.add(vipData);
        idxVip++;
      }

    }

    return result;
  }

  @Override
  public int getPosition(long id) {
    int position = -1;
    position = managementQueue.getPosition(id);
    if (position == -1) {
      position = normalQueue.getPosition(id);
      if (position == -1) {
        position = priorityQueue.getPosition(id);
        if (position == -1) {
          vipQueue.getPosition(id);
        }
      }
    }
    return -1;
  }

  @Override
  public boolean isEmpty() {
    throw new UnsupportedOperationException("Method is not implemented");
  }


  @Override
  public double avrSeqWaitTime(long currentEpochTimeSec) {
    return (managementQueue.avrSeqWaitTime(currentEpochTimeSec) + normalQueue.avrSeqWaitTime(currentEpochTimeSec) +
        priorityQueue.avrSeqWaitTime(currentEpochTimeSec) + vipQueue.avrSeqWaitTime(currentEpochTimeSec)) / 4.0;
  }

  private PriorityQueue resolveTopPriorityQueue() {
    if (!managementQueue.isEmpty()) {
      return managementQueue;
    } else {
      QueueData normaData = normalQueue.poll();
      QueueData priorityData = priorityQueue.poll();
      QueueData vipData = vipQueue.poll();

      double normaDataPriority = normalQueueRankFun.apply(normaData);
      double priorityDataPriority = priorityQueueRankFun.apply(priorityData);
      double vipDataPriority = vipQueueRankFun.apply(vipData);

      if (normaDataPriority > priorityDataPriority && normaDataPriority > vipDataPriority) {
        return normalQueue;
      } else if (priorityDataPriority > normaDataPriority && priorityDataPriority > vipDataPriority) {
        return priorityQueue;
      } else {
        return vipQueue;
      }

    }
  }


}
