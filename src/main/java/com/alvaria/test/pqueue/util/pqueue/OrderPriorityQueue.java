package com.alvaria.test.pqueue.util.pqueue;

import com.alvaria.test.pqueue.model.QueueData;

public interface OrderPriorityQueue extends Iterable<QueueData> {

  void enqueue(QueueData queueData);

  QueueData remove(long id);

  QueueData removeFirst();

  QueueData peekFirst();

  boolean isEmpty();

  int size();

  void clear();

}
