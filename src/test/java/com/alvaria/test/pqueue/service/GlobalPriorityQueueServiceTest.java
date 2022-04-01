package com.alvaria.test.pqueue.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.alvaria.test.pqueue.model.QueueData;
import com.alvaria.test.pqueue.util.Util;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class GlobalPriorityQueueServiceTest {

  @Autowired
  private GlobalPriorityQueueService globalPriorityQueue;

  @BeforeEach
  public void setUp() {
    while(!globalPriorityQueue.getIdListSortedByPriority().isEmpty()) {
      globalPriorityQueue.dequeue();
    }

  }

  @Test
  void createNewOrderVanillaTest() {
    int curEpochSec = Util.getCurrentSeconds();
    QueueData queueData1 = new QueueData(curEpochSec + 1, 1L);
    QueueData queueData2 = new QueueData(curEpochSec + 3, 12L);

    globalPriorityQueue.enqueue(queueData1);
    globalPriorityQueue.enqueue(queueData2);

    assertThat(globalPriorityQueue.getIdListSortedByPriority()).containsExactlyInAnyOrder(1L, 12L);
  }

  @Test
  void createNewOrderInvalidIdTest() {

    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      globalPriorityQueue.enqueue(new QueueData(Util.getCurrentSeconds(), 0L));
    });
  }

  @Test
  void createNewOrderDifferentRulesTest() {
    int curEpochSec = Util.getCurrentSeconds();
    long id_3_5 = 3 * 5;
    long id_3 = 3;
    long id_5 = 5;
    long id_21 = 21;

    QueueData queueData1 = new QueueData(curEpochSec + 1, id_3_5);
    QueueData queueData2 = new QueueData(curEpochSec + 2, id_3);
    QueueData queueData3 = new QueueData(curEpochSec + 3, id_5);
    QueueData queueData4 = new QueueData(curEpochSec + 4, id_21);

    globalPriorityQueue.enqueue(queueData1);
    globalPriorityQueue.enqueue(queueData2);
    globalPriorityQueue.enqueue(queueData3);
    globalPriorityQueue.enqueue(queueData4);

    List<Long> result = globalPriorityQueue.getIdListSortedByPriority();

    assertThat(result).containsExactly(id_21, id_5, id_3, id_3_5);

  }

  @Test
  void pollOrderTest() {
  }

  @Test
  void listOrdersTest() {
  }

  @Test
  void removeOrderTest() {
  }

  @Test
  void getOrderPositionTest() {
  }

  @Test
  void getAverageWaitTimeTest() {
  }
}