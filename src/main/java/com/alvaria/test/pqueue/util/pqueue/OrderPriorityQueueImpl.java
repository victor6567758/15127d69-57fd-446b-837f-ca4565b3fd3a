package com.alvaria.test.pqueue.util.pqueue;

import com.alvaria.test.pqueue.model.QueueData;
import com.alvaria.test.pqueue.util.augmenttree.AugmentedTreeList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class OrderPriorityQueueImpl implements OrderPriorityQueue {

  private final Map<Long, QueueData> idDataMap = new HashMap<>();

  private final AugmentedTreeList<QueueData> orderPriorityTree = new AugmentedTreeList<>(
      Comparator.comparing(QueueData::getEnqueueEpochTimeSec)
          .reversed().thenComparingLong(QueueData::getId));

  OrderPriorityQueueImpl() {
  }


  @Override
  public void enqueue(QueueData queueData) {
    QueueData prevQueueData = idDataMap.putIfAbsent(queueData.getId(), queueData);
    if (prevQueueData != null) {
      throw new IllegalArgumentException("ID already exists: " + queueData.getId());
    }

    orderPriorityTree.enqueue(queueData);
  }

  @Override
  public QueueData dequeue() {

    QueueData removed = orderPriorityTree.dequeue();
    idDataMap.remove(removed.getId());

    return removed;
  }

  @Override
  public QueueData poll() {
    return orderPriorityTree.poll();
  }

  @Override
  public QueueData remove(long id) {
    QueueData queueData = idDataMap.get(id);
    if (queueData == null) {
      return null;
    }

    orderPriorityTree.remove(queueData);
    return queueData;
  }


  @Override
  public int getPosition(long id) {
    QueueData queueData = idDataMap.get(id);
    if (queueData == null) {
      return -1;
    }

    return orderPriorityTree.getItemRank(queueData);
  }

  @Override
  public boolean isEmpty() {
    return idDataMap.isEmpty();
  }

  @Override
  public int size() {
    return orderPriorityTree.size();
  }


  @Override
  public Iterator<QueueData> iterator() {
    return orderPriorityTree.iterator();
  }
}
