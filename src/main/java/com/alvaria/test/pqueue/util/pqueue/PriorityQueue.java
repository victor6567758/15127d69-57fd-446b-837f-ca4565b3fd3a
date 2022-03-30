package com.alvaria.test.pqueue.util.pqueue;

import com.alvaria.test.pqueue.model.QueueData;
import java.util.List;

public interface PriorityQueue {

  void enqueue(QueueData queueData);

  QueueData dequeue();

  QueueData poll();

  List<QueueData> getList();

  int getPosition(long id);

  boolean isEmpty();

  double avrSeqWaitTime(long currentEpochTimeSec);

}
