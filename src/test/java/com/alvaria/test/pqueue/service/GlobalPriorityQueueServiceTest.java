package com.alvaria.test.pqueue.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.alvaria.test.pqueue.model.QueueData;
import com.alvaria.test.pqueue.model.QueueType;
import com.alvaria.test.pqueue.util.Util;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
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
  void enqueueDifferentCategoriesAndListTest() {
    int curEpochSec = Util.getCurrentSeconds();

    QueueData queueData1 = new QueueData(curEpochSec + 1, 3 * 5);
    Pair<QueueData, Double> pair1 = new ImmutablePair<>(queueData1,
        QueueData.getPriority(QueueType.MANAGEMENT, queueData1.getEnqueueTimeSec() - curEpochSec));
    assertThat(QueueData.getOrderType(queueData1)).isEqualTo(QueueType.MANAGEMENT);

    QueueData queueData2 = new QueueData(curEpochSec + 2, 3);
    Pair<QueueData, Double> pair2 = new ImmutablePair<>(queueData2,
        QueueData.getPriority(QueueType.PRIORITY, queueData2.getEnqueueTimeSec() - curEpochSec));
    assertThat(QueueData.getOrderType(queueData2)).isEqualTo(QueueType.PRIORITY);

    QueueData queueData3 = new QueueData(curEpochSec + 3, 5);
    Pair<QueueData, Double> pair3 = new ImmutablePair<>(queueData3,
        QueueData.getPriority(QueueType.VIP, queueData3.getEnqueueTimeSec() - curEpochSec));
    assertThat(QueueData.getOrderType(queueData3)).isEqualTo(QueueType.VIP);

    QueueData queueData4 = new QueueData(curEpochSec + 4, 22);
    Pair<QueueData, Double> pair4 = new ImmutablePair<>(queueData4,
        QueueData.getPriority(QueueType.NORMAL, queueData4.getEnqueueTimeSec() - curEpochSec));
    assertThat(QueueData.getOrderType(queueData4)).isEqualTo(QueueType.NORMAL);

    List<Pair<QueueData, Double>> pairs = Stream.of(pair1, pair2, pair3, pair4).collect(Collectors.toList());
    Collections.sort(pairs, (o1, o2) -> o2.getRight().compareTo(o1.getRight()));

    pairs.forEach( data -> globalPriorityQueue.enqueue(data.getLeft()));
    List<Long> expected = pairs.stream().map(elem -> elem.getLeft().getId()).collect(Collectors.toList());

    List<Long> result = globalPriorityQueue.getIdListSortedByPriority();
    assertThat(result).isEqualTo(expected);


  }


}