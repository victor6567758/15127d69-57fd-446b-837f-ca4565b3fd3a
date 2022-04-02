package com.alvaria.test.pqueue.service;

import com.alvaria.test.pqueue.model.QueueData;
import java.util.List;

public interface GlobalPriorityQueueService {
  void enqueue(QueueData queueData);

  QueueData dequeue();

  List<Long> getIdListSortedByPriority();

  void remove(long id);

  int getPosition(long id);

  double getAverageWaitTime(long currentEpochTimeSec);

  void clear();


}
