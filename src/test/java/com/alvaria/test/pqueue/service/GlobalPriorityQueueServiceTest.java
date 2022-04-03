package com.alvaria.test.pqueue.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.alvaria.test.pqueue.model.QueueData;
import com.alvaria.test.pqueue.model.QueueType;
import com.alvaria.test.pqueue.util.TestConst;
import com.alvaria.test.pqueue.util.Util;
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
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest
@ActiveProfiles("test")
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
  void clearVanillaTest() {
    int curEpochSec = Util.getCurrentSeconds();

    globalPriorityQueue.enqueue(new QueueData(curEpochSec + 1, 1L));
    globalPriorityQueue.enqueue(new QueueData(curEpochSec + 3, 12L));

    assertThat(globalPriorityQueue.getIdListSortedByPriority()).isNotEmpty();

    globalPriorityQueue.clear();
    assertThat(globalPriorityQueue.getIdListSortedByPriority()).isEmpty();

  }

  @Test
  void enqueueInvalidIdTest() {

    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      globalPriorityQueue.enqueue(new QueueData(Util.getCurrentSeconds(), 0L));
    });
  }

  @Test
  void enqueueInvalidTimeTest() {

    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      globalPriorityQueue.enqueue(new QueueData(globalPriorityQueue.getStartEpochSec() - 1, 0L));
    });
  }

  @Test
  void enqueueRepeatedIdTest() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      globalPriorityQueue.enqueue(new QueueData(Util.getCurrentSeconds() + 1, 1L));
      globalPriorityQueue.enqueue(new QueueData(Util.getCurrentSeconds() + 2, 1L));
    });
  }

  @Test
  void enqueueSameNonManagerCategoryAndListTest() {
    int curEpochSec = Util.getCurrentSeconds();

    QueueData queueData1 = new QueueData(curEpochSec + 10, 3);
    Pair<QueueData, Double> pair1 = new ImmutablePair<>(queueData1,
        QueueData.getPriority(QueueType.PRIORITY, queueData1.getEnqueueTimeSec() - curEpochSec));
    assertThat(QueueData.getOrderType(queueData1)).isEqualTo(QueueType.PRIORITY);

    QueueData queueData2 = new QueueData(curEpochSec + 20, 9);
    Pair<QueueData, Double> pair2 = new ImmutablePair<>(queueData2,
        QueueData.getPriority(QueueType.PRIORITY, queueData2.getEnqueueTimeSec() - curEpochSec));
    assertThat(QueueData.getOrderType(queueData2)).isEqualTo(QueueType.PRIORITY);

    QueueData queueData3 = new QueueData(curEpochSec + 30, 12);
    Pair<QueueData, Double> pair3 = new ImmutablePair<>(queueData3,
        QueueData.getPriority(QueueType.PRIORITY, queueData3.getEnqueueTimeSec() - curEpochSec));
    assertThat(QueueData.getOrderType(queueData3)).isEqualTo(QueueType.PRIORITY);

    List<Pair<QueueData, Double>> pairs = Stream.of(pair1, pair2, pair3)
        .sorted((o1, o2) -> o2.getRight().compareTo(o1.getRight())).collect(Collectors.toList());

    pairs.forEach(data -> globalPriorityQueue.enqueue(data.getLeft()));
    List<Long> expected = pairs.stream().map(elem -> elem.getLeft().getId()).collect(Collectors.toList());

    List<Long> result = globalPriorityQueue.getIdListSortedByPriority();
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void enqueueSamePrioritiesAndListTest() {
    int curEpochSec = Util.getCurrentSeconds();
    QueueData queueData1 = new QueueData(curEpochSec, 3 * 5);
    assertThat(QueueData.getOrderType(queueData1)).isEqualTo(QueueType.MANAGEMENT);
    globalPriorityQueue.enqueue(queueData1);

    QueueData queueData2 = new QueueData(curEpochSec, 3 * 5 * 3);
    assertThat(QueueData.getOrderType(queueData2)).isEqualTo(QueueType.MANAGEMENT);
    globalPriorityQueue.enqueue(queueData2);

    QueueData queueData3 = new QueueData(curEpochSec, 22);
    assertThat(QueueData.getOrderType(queueData3)).isEqualTo(QueueType.NORMAL);
    globalPriorityQueue.enqueue(queueData3);

    QueueData queueData4 = new QueueData(curEpochSec, 23);
    assertThat(QueueData.getOrderType(queueData4)).isEqualTo(QueueType.NORMAL);
    globalPriorityQueue.enqueue(queueData4);

    List<Long> result = globalPriorityQueue.getIdListSortedByPriority();
    assertThat(result.subList(0, 2)).containsExactlyInAnyOrder(3 * 5L, 3 * 5 * 3L);
    assertThat(result.subList(2, 4)).containsExactlyInAnyOrder(22L, 23L);

  }

  @Test
  void enqueueDifferentCategoriesAndListTest() {
    int curEpochSec = Util.getCurrentSeconds();

    QueueData queueData1 = new QueueData(curEpochSec + 10, 3 * 5);
    Pair<QueueData, Double> pair1 = new ImmutablePair<>(queueData1,
        QueueData.getPriority(QueueType.MANAGEMENT, queueData1.getEnqueueTimeSec() - curEpochSec));
    assertThat(QueueData.getOrderType(queueData1)).isEqualTo(QueueType.MANAGEMENT);

    QueueData queueData2 = new QueueData(curEpochSec + 20, 3);
    Pair<QueueData, Double> pair2 = new ImmutablePair<>(queueData2,
        QueueData.getPriority(QueueType.PRIORITY, queueData2.getEnqueueTimeSec() - curEpochSec));
    assertThat(QueueData.getOrderType(queueData2)).isEqualTo(QueueType.PRIORITY);

    QueueData queueData3 = new QueueData(curEpochSec + 30, 5);
    Pair<QueueData, Double> pair3 = new ImmutablePair<>(queueData3,
        QueueData.getPriority(QueueType.VIP, queueData3.getEnqueueTimeSec() - curEpochSec));
    assertThat(QueueData.getOrderType(queueData3)).isEqualTo(QueueType.VIP);

    QueueData queueData4 = new QueueData(curEpochSec + 40, 22);
    Pair<QueueData, Double> pair4 = new ImmutablePair<>(queueData4,
        QueueData.getPriority(QueueType.NORMAL, queueData4.getEnqueueTimeSec() - curEpochSec));
    assertThat(QueueData.getOrderType(queueData4)).isEqualTo(QueueType.NORMAL);

    List<Pair<QueueData, Double>> pairs = Stream.of(pair1, pair2, pair3, pair4)
        .sorted((o1, o2) -> o2.getRight().compareTo(o1.getRight())).collect(Collectors.toList());

    pairs.forEach(data -> globalPriorityQueue.enqueue(data.getLeft()));
    List<Long> expected = pairs.stream().map(elem -> elem.getLeft().getId()).collect(Collectors.toList());

    List<Long> result = globalPriorityQueue.getIdListSortedByPriority();
    assertThat(result).isEqualTo(expected);

  }

  @Test
  void findPositionIdManagementCategoryDifferentPrioritiesTest() {
    int curEpochSec = Util.getCurrentSeconds();

    QueueData queueData1 = new QueueData(curEpochSec + 10, 3 * 5);
    assertThat(QueueData.getOrderType(queueData1)).isEqualTo(QueueType.MANAGEMENT);
    globalPriorityQueue.enqueue(queueData1);

    QueueData queueData2 = new QueueData(curEpochSec + 20, 3 * 5 * 3);
    assertThat(QueueData.getOrderType(queueData2)).isEqualTo(QueueType.MANAGEMENT);
    globalPriorityQueue.enqueue(queueData2);

    QueueData queueData3 = new QueueData(curEpochSec + 30, 3 * 5 * 3 * 5);
    assertThat(QueueData.getOrderType(queueData3)).isEqualTo(QueueType.MANAGEMENT);
    globalPriorityQueue.enqueue(queueData3);

    List<Long> resultList = globalPriorityQueue.getIdListSortedByPriority();

    assertThat(globalPriorityQueue.getPosition(queueData3.getId()))
        .isEqualTo(0);
    assertThat(globalPriorityQueue.getPosition(queueData2.getId()))
        .isEqualTo(1);
    assertThat(globalPriorityQueue.getPosition(queueData1.getId()))
        .isEqualTo(2);

    assertThat(resultList.get(0))
        .isEqualTo(resultList.get(globalPriorityQueue.getPosition(queueData3.getId())));
    assertThat(resultList.get(1))
        .isEqualTo(resultList.get(globalPriorityQueue.getPosition(queueData2.getId())));
    assertThat(resultList.get(2))
        .isEqualTo(resultList.get(globalPriorityQueue.getPosition(queueData1.getId())));

  }

  @Test
  void findPositionIdNonManagementCategoriesDifferentPrioritiesTest() {
    int curEpochSec = Util.getCurrentSeconds();

    QueueData queueData1 = new QueueData(curEpochSec + 10, 5);
    assertThat(QueueData.getOrderType(queueData1)).isEqualTo(QueueType.VIP);
    Pair<QueueData, Double> pair1 = new ImmutablePair<>(queueData1,
        QueueData.getPriority(QueueType.VIP, queueData1.getEnqueueTimeSec() - curEpochSec));

    QueueData queueData2 = new QueueData(curEpochSec + 20, 22);
    assertThat(QueueData.getOrderType(queueData2)).isEqualTo(QueueType.NORMAL);
    Pair<QueueData, Double> pair2 = new ImmutablePair<>(queueData2,
        QueueData.getPriority(QueueType.NORMAL, queueData2.getEnqueueTimeSec() - curEpochSec));

    QueueData queueData3 = new QueueData(curEpochSec + 30, 23);
    assertThat(QueueData.getOrderType(queueData3)).isEqualTo(QueueType.NORMAL);
    Pair<QueueData, Double> pair3 = new ImmutablePair<>(queueData3,
        QueueData.getPriority(QueueType.NORMAL, queueData3.getEnqueueTimeSec() - curEpochSec));

    QueueData queueData4 = new QueueData(curEpochSec + 40, 3);
    assertThat(QueueData.getOrderType(queueData4)).isEqualTo(QueueType.PRIORITY);
    Pair<QueueData, Double> pair4 = new ImmutablePair<>(queueData4,
        QueueData.getPriority(QueueType.PRIORITY, queueData4.getEnqueueTimeSec() - curEpochSec));

    List<Pair<QueueData, Double>> pairs = Stream.of(pair1, pair2, pair3, pair4)
        .sorted((o1, o2) -> o2.getRight().compareTo(o1.getRight())).collect(Collectors.toList());

    pairs.forEach(data -> globalPriorityQueue.enqueue(data.getLeft()));
    List<Long> expected = pairs.stream().map(elem -> elem.getLeft().getId()).collect(Collectors.toList());

    List<Long> result = globalPriorityQueue.getIdListSortedByPriority();
    assertThat(result).isEqualTo(expected);

    for (int i = 0; i < pairs.size() - 1; i++) {
      assertThat(globalPriorityQueue.getPosition(pairs.get(i).getLeft().getId())).isEqualTo(i);
    }

  }

  @Test
  void testAverageOverSingleCategoryTest() {
    int curEpochSec = Util.getCurrentSeconds();

    QueueData queueData1 = new QueueData(curEpochSec + 10, 3);
    Pair<QueueData, Double> pair1 = new ImmutablePair<>(queueData1,
        QueueData.getPriority(QueueType.PRIORITY, queueData1.getEnqueueTimeSec() - curEpochSec));
    assertThat(QueueData.getOrderType(queueData1)).isEqualTo(QueueType.PRIORITY);

    QueueData queueData2 = new QueueData(curEpochSec + 20, 9);
    Pair<QueueData, Double> pair2 = new ImmutablePair<>(queueData2,
        QueueData.getPriority(QueueType.PRIORITY, queueData2.getEnqueueTimeSec() - curEpochSec));
    assertThat(QueueData.getOrderType(queueData2)).isEqualTo(QueueType.PRIORITY);

    QueueData queueData3 = new QueueData(curEpochSec + 30, 12);
    Pair<QueueData, Double> pair3 = new ImmutablePair<>(queueData3,
        QueueData.getPriority(QueueType.PRIORITY, queueData3.getEnqueueTimeSec() - curEpochSec));
    assertThat(QueueData.getOrderType(queueData3)).isEqualTo(QueueType.PRIORITY);

    List<Pair<QueueData, Double>> pairs = Stream.of(pair1, pair2, pair3)
        .sorted((o1, o2) -> o2.getRight().compareTo(o1.getRight())).collect(Collectors.toList());

    pairs.forEach(data -> globalPriorityQueue.enqueue(data.getLeft()));
    double avrExpected = pairs.stream().map(elem -> elem.getLeft().getEnqueueTimeSec() - curEpochSec)
        .mapToInt(value -> value).average().orElse(-1.0);

    double avrResult = globalPriorityQueue.getAverageWaitTime(curEpochSec);
    assertThat(avrExpected).isCloseTo(avrResult, TestConst.TEST_DOUBLE_OFFSET);

  }

  @Test
  void testAverageOverEmptyQueueTest() {
    double avrExpected = globalPriorityQueue.getAverageWaitTime(Util.getCurrentSeconds());
    assertThat(avrExpected).isCloseTo(-1.0, TestConst.TEST_DOUBLE_OFFSET);
  }

}