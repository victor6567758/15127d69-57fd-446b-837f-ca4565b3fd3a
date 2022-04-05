package com.alvaria.test.pqueue.util.pqueue;

import lombok.experimental.UtilityClass;

@UtilityClass
public class OrderPriorityQueueFactory {

  public static OrderPriorityQueue create() {
    return new OrderPriorityQueueImpl();
  }
}
