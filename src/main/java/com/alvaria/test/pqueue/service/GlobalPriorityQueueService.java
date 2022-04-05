package com.alvaria.test.pqueue.service;

import com.alvaria.test.pqueue.model.QueueData;
import java.util.List;

public interface GlobalPriorityQueueService {
  void enqueue(QueueData queueData);

  QueueData dequeue(int curTimeEpochSec);

  List<Long> getIdListSortedByPriority(int curTimeEpochSec);

  void remove(long id);

  int getPosition(long id, int curTimeEpochSec);

  double getAverageWaitTime(int curTimeEpochSec);

  void clear();


}
