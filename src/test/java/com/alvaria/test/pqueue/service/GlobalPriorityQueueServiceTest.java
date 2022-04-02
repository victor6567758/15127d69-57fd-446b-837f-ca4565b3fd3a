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
    globalPriorityQueue.clear();
  }

  @Test
  void enqueueVanillaTest() {
    int curEpochSec = Util.getCurrentSeconds();

    globalPriorityQueue.enqueue(new QueueData(curEpochSec + 1, 1L));
    globalPriorityQueue.enqueue(new QueueData(curEpochSec + 3, 12L));

    assertThat(globalPriorityQueue.getIdListSortedByPriority()).isNotEmpty();
  }

  @Test
  void createNewOrderInvalidIdTest() {

    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      globalPriorityQueue.enqueue(new QueueData(Util.getCurrentSeconds(), 0L));
    });
  }

  @Test
  void enqueueDifferentCategoriesTest() {
    int curEpochSec = Util.getCurrentSeconds();
    long id_3_5 = 3 * 5; // management
    long id_3 = 3; // priority
    long id_5 = 5; // vip
    long id_21 = 22; // normal

    QueueData queueData1 = new QueueData(curEpochSec + 1, id_3_5);
    QueueData queueData2 = new QueueData(curEpochSec + 2, id_3);
    QueueData queueData3 = new QueueData(curEpochSec + 3, id_5);
    QueueData queueData4 = new QueueData(curEpochSec + 4, id_21);

    globalPriorityQueue.enqueue(queueData1);
    globalPriorityQueue.enqueue(queueData2);
    globalPriorityQueue.enqueue(queueData3);
    globalPriorityQueue.enqueue(queueData4);

    List<Long> result = globalPriorityQueue.getIdListSortedByPriority();

    //assertThat(result).containsExactly(id_3_5, id_21, id_5, id_3);

  }


}