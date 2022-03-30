package com.alvaria.test.pqueue.util.pqueue;

import com.alvaria.test.pqueue.model.QueueData;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class PriorityQueueImpl implements PriorityQueue {

  private final Map<Long, QueueData> idDataMap = new HashMap<>();
  private final TreeMap<Long, TreeSet<QueueData>> priorityMap = new TreeMap<>();

  @Override
  public void enqueue(QueueData queueData) {


    QueueData prevQueueData = idDataMap.putIfAbsent(queueData.getId(), queueData);
    if (prevQueueData != null) {
      throw new IllegalArgumentException("ID already exists: " + queueData.getId());
    }

    QueueData lastStoredData = getLastStoredData();
    int lastSeq = -1;
    if (lastStoredData != null) {
      lastSeq = lastStoredData.getSeqNum();
    }

    TreeSet<QueueData> dataUnderSamePriority =
        priorityMap.computeIfAbsent(queueData.getEnqueueEpochTimeSec(), key ->
            new TreeSet<>((o1, o2) -> Long.compare(o2.getEnqueueEpochTimeSec(), o1.getEnqueueEpochTimeSec())));
    queueData.setSeqNum(++lastSeq);
    dataUnderSamePriority.add(queueData);

    for (QueueData data : dataUnderSamePriority.tailSet(queueData, false)) {
      data.setSeqNum(++lastSeq);
    }

    // next priorities
    for (Map.Entry<Long, TreeSet<QueueData>> dataSet :
        priorityMap.tailMap(queueData.getEnqueueEpochTimeSec(), false).entrySet()) {
      for (QueueData data : dataSet.getValue()) {
        data.setSeqNum(++lastSeq);
      }
    }
  }

  @Override
  public QueueData dequeue() {
    Map.Entry<Long, TreeSet<QueueData>> topEntry = priorityMap.lastEntry();
    if (topEntry == null) {
      throw new IllegalArgumentException("Queue is empty");
    }

    Set<QueueData> topDataSet = topEntry.getValue();
    QueueData toRemove = topDataSet.iterator().next();

    idDataMap.remove(toRemove.getId());
    topDataSet.remove(toRemove);

    if (topDataSet.isEmpty()) {
      priorityMap.remove(topEntry.getKey());
    }

    return toRemove;
  }

  @Override
  public QueueData poll() {
    return getLastStoredData();
  }


  @Override
  public List<QueueData> getList() {
    return priorityMap.values()
        .stream()
        .flatMap(Collection::stream).collect(Collectors.toList());
  }

  @Override
  public int getPosition(long id) {
    QueueData queueData = idDataMap.get(id);
    if (queueData == null) {
      return -1;
    }

    return queueData.getSeqNum();
  }

  @Override
  public boolean isEmpty() {
    return priorityMap.isEmpty();
  }

  @Override
  public double avrSeqWaitTime(long currentEpochTimeSec) {
    return priorityMap.values()
        .stream()
        .flatMap(Collection::stream)
        .map(entry -> currentEpochTimeSec - entry.getEnqueueEpochTimeSec())
        .mapToLong(value -> value).average().orElse(0.0);
  }

  private QueueData getLastStoredData() {
    if (priorityMap.isEmpty()) {
      return null;
    }
    Map.Entry<Long, TreeSet<QueueData>> topEntry = priorityMap.lastEntry();
    TreeSet<QueueData> topDataSet = topEntry.getValue();
    return topDataSet.last();
  }

}
