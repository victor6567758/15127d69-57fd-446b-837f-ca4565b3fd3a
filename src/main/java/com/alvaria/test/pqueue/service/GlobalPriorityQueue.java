package com.alvaria.test.pqueue.service;

import com.alvaria.test.pqueue.model.QueueData;
import java.util.List;

public interface GlobalPriorityQueue {
  void enqueue(QueueData queueData);

  QueueData dequeue();

  List<QueueData> getList();

  void remove(long id);

  int getPosition(long id);

  double avrSeqWaitTime(long currentEpochTimeSec);

}
