package com.alvaria.test.pqueue.util.pqueue;

import com.alvaria.test.pqueue.model.QueueData;

public interface OrderPriorityQueue extends Iterable<QueueData> {

  void enqueue(QueueData queueData);

  QueueData dequeue();

  QueueData poll();

  int getPosition(long id);

  QueueData remove(long id);

  boolean isEmpty();

  int size();

}
