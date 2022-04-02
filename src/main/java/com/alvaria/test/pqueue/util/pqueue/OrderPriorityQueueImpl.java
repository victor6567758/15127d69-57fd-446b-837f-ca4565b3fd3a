package com.alvaria.test.pqueue.util.pqueue;

import com.alvaria.test.pqueue.model.QueueData;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

public class OrderPriorityQueueImpl implements OrderPriorityQueue {

  private final Map<Long, QueueData> idDataMap = new HashMap<>();

  private final TreeSet<QueueData> orderPriorityTree = new TreeSet<>(
      Comparator.comparing(QueueData::getEnqueueTimeSec)
          .reversed().thenComparingLong(QueueData::getId));

  OrderPriorityQueueImpl() {
  }


  @Override
  public void enqueue(QueueData queueData) {

    if (queueData == null || queueData.getEnqueueTimeSec() < 0 || queueData.getId() < 1) {
      throw new IllegalArgumentException("Invalid input data");
    }

    QueueData prevQueueData = idDataMap.putIfAbsent(queueData.getId(), queueData);
    if (prevQueueData != null) {
      throw new IllegalArgumentException("ID already exists: " + queueData.getId());
    }

    orderPriorityTree.add(queueData);
  }

  @Override
  public QueueData removeFirst() {
    if (orderPriorityTree.isEmpty()) {
      throw new IllegalArgumentException("Queue is empty");
    }

    QueueData top = orderPriorityTree.pollFirst();
    return idDataMap.remove(top.getId());
  }

  @Override
  public QueueData peekFirst() {
    if (!orderPriorityTree.isEmpty()) {
      return orderPriorityTree.first();
    }
    return null;
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

  @Override
  public void clear() {
    idDataMap.clear();
    orderPriorityTree.clear();
  }
}
