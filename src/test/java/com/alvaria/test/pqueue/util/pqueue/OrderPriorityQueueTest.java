package com.alvaria.test.pqueue.util.pqueue;

import static org.assertj.core.api.Assertions.assertThat;

import com.alvaria.test.pqueue.model.QueueData;
import com.alvaria.test.pqueue.util.Util;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class OrderPriorityQueueTest {


  @Test
  void enqueueVanillaTest() {
    OrderPriorityQueue orderPriorityQueue = OrderPriorityQueueFactory.create();
    int curEpochSec = Util.getCurrentSeconds();

    orderPriorityQueue.enqueue(new QueueData(curEpochSec + 1, 1L));
    orderPriorityQueue.enqueue(new QueueData(curEpochSec + 3, 2L));
    orderPriorityQueue.enqueue(new QueueData(curEpochSec - 1, 3L));

    assertThat(StreamSupport.stream(orderPriorityQueue.spliterator(), false)
        .map(QueueData::getEnqueueEpochTimeSec).collect(Collectors.toList()))
        .containsExactly(curEpochSec + 3, curEpochSec + 1, curEpochSec - 1);

  }

  @Test
  void enqueueInvalidInputTest() {
    OrderPriorityQueue orderPriorityQueue = OrderPriorityQueueFactory.create();
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      orderPriorityQueue.enqueue(null);
    });
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      orderPriorityQueue.enqueue(new QueueData(-1, 1L));
    });
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      orderPriorityQueue.enqueue(new QueueData(Util.getCurrentSeconds(), -1L));
    });
  }

  @Test
  void enqueueDuplicateIdTest() {
    OrderPriorityQueue orderPriorityQueue = OrderPriorityQueueFactory.create();
    int curEpochSec = Util.getCurrentSeconds();

    QueueData queueData1 = new QueueData(curEpochSec + 1, 1L);
    QueueData queueData2 = new QueueData(curEpochSec + 3, 1L);

    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      orderPriorityQueue.enqueue(queueData1);
      orderPriorityQueue.enqueue(queueData2);
    });

  }

  @Test
  void enqueueDuplicatePriorityTest() {
    OrderPriorityQueue orderPriorityQueue = OrderPriorityQueueFactory.create();
    int curEpochSec = Util.getCurrentSeconds();

    orderPriorityQueue.enqueue(new QueueData(curEpochSec, 1L));
    orderPriorityQueue.enqueue(new QueueData(curEpochSec, 2L));
    orderPriorityQueue.enqueue(new QueueData(curEpochSec, 3L));

    assertThat(StreamSupport.stream(orderPriorityQueue.spliterator(), false)
        .map(QueueData::getId).collect(Collectors.toList()))
        .containsExactlyInAnyOrder(1L, 2L, 3L);

  }


  @Test
  void removeFirstVanillaTest() {
    OrderPriorityQueue orderPriorityQueue = OrderPriorityQueueFactory.create();
    int curEpochSec = Util.getCurrentSeconds();

    orderPriorityQueue.enqueue(new QueueData(curEpochSec + 1, 1L));
    orderPriorityQueue.enqueue(new QueueData(curEpochSec + 3, 2L));
    orderPriorityQueue.enqueue(new QueueData(curEpochSec - 1, 3L));

    assertThat(StreamSupport.stream(orderPriorityQueue.spliterator(), false)
        .map(QueueData::getEnqueueEpochTimeSec).collect(Collectors.toList()))
        .hasSize(3);

    QueueData removedData = orderPriorityQueue.removeFirst();
    assertThat(removedData.getId()).isEqualTo(2L);
    assertThat(removedData.getEnqueueEpochTimeSec()).isEqualTo(curEpochSec + 3);

    assertThat(StreamSupport.stream(orderPriorityQueue.spliterator(), false)
        .map(QueueData::getEnqueueEpochTimeSec).collect(Collectors.toList()))
        .containsExactly(curEpochSec + 1, curEpochSec - 1);
  }

  @Test
  void peekFirstVanillaTest() {
    OrderPriorityQueue orderPriorityQueue = OrderPriorityQueueFactory.create();
    int curEpochSec = Util.getCurrentSeconds();

    orderPriorityQueue.enqueue(new QueueData(curEpochSec + 1, 1L));
    orderPriorityQueue.enqueue(new QueueData(curEpochSec + 3, 2L));
    orderPriorityQueue.enqueue(new QueueData(curEpochSec - 1, 3L));

    QueueData peekData = orderPriorityQueue.peekFirst();
    assertThat(peekData.getId()).isEqualTo(2L);
    assertThat(peekData.getEnqueueEpochTimeSec()).isEqualTo(curEpochSec + 3);

    assertThat(StreamSupport.stream(orderPriorityQueue.spliterator(), false)
        .map(QueueData::getEnqueueEpochTimeSec).collect(Collectors.toList()))
        .containsExactly(curEpochSec + 3, curEpochSec + 1, curEpochSec - 1);
  }

  @Test
  void peekFirstEmptyQueueTest() {
    OrderPriorityQueue orderPriorityQueue = OrderPriorityQueueFactory.create();
    QueueData queueData = orderPriorityQueue.peekFirst();
    assertThat(queueData).isNull();
  }

  @Test
  void removeFirstEmptyQueueTest() {
    OrderPriorityQueue orderPriorityQueue = OrderPriorityQueueFactory.create();

    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      orderPriorityQueue.removeFirst();
    });
  }

  @Test
  void isEmptyVanillaTest() {
    OrderPriorityQueue orderPriorityQueue = OrderPriorityQueueFactory.create();
    orderPriorityQueue.enqueue(new QueueData(Util.getCurrentSeconds(), 1L));

    assertThat(StreamSupport.stream(orderPriorityQueue.spliterator(), false)
        .map(QueueData::getEnqueueEpochTimeSec).collect(Collectors.toList()))
        .hasSize(1);

    orderPriorityQueue.removeFirst();

    assertThat(StreamSupport.stream(orderPriorityQueue.spliterator(), false)
        .map(QueueData::getEnqueueEpochTimeSec).collect(Collectors.toList()))
        .isEmpty();
  }

  @Test
  void sizeVanillaTest() {
    OrderPriorityQueue orderPriorityQueue = OrderPriorityQueueFactory.create();
    assertThat(orderPriorityQueue.size()).isEqualTo(0);

    orderPriorityQueue.enqueue(new QueueData(Util.getCurrentSeconds(), 1L));
    assertThat(orderPriorityQueue.size()).isEqualTo(1);

  }

  @Test
  void removeVanillaTest() {
    OrderPriorityQueue orderPriorityQueue = OrderPriorityQueueFactory.create();
    int curEpochSec = Util.getCurrentSeconds();

    orderPriorityQueue.enqueue(new QueueData(curEpochSec + 1, 1L));
    orderPriorityQueue.enqueue(new QueueData(curEpochSec + 3, 2L));
    orderPriorityQueue.enqueue(new QueueData(curEpochSec - 1, 3L));

    orderPriorityQueue.remove(3L);
    assertThat(StreamSupport.stream(orderPriorityQueue.spliterator(), false)
        .map(QueueData::getId).collect(Collectors.toList()))
        .containsExactlyInAnyOrder(1L, 2L);

  }

  @Test
  void clearVanillaTest() {
    OrderPriorityQueue orderPriorityQueue = OrderPriorityQueueFactory.create();
    orderPriorityQueue.enqueue(new QueueData(Util.getCurrentSeconds(), 1L));

    assertThat(orderPriorityQueue.isEmpty()).isFalse();
    orderPriorityQueue.clear();

    assertThat(orderPriorityQueue.isEmpty()).isTrue();
  }
}