package com.alvaria.test.pqueue.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.alvaria.test.pqueue.model.QueueCategory;
import com.alvaria.test.pqueue.model.QueueData;
import com.alvaria.test.pqueue.util.TestConst;
import com.alvaria.test.pqueue.util.Util;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
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

  private static final int BULK_ODER_TEST = 1000;

  @Autowired
  private GlobalPriorityQueueService globalPriorityQueue;

  @BeforeEach
  public void setUp() {
    globalPriorityQueue.clear();
  }

  @Test
  void enqueueVanillaTest() {
    int curEpochSec = Util.getNowCurrentEpochSeconds();
    int testTime = curEpochSec + 3;

    globalPriorityQueue.enqueue(new QueueData(curEpochSec + 1, 1L));
    globalPriorityQueue.enqueue(new QueueData(curEpochSec + 3, 12L));

    assertThat(globalPriorityQueue.getIdListSortedByPriority(testTime)).isNotEmpty();
  }

  @Test
  void clearVanillaTest() {
    int curEpochSec = Util.getNowCurrentEpochSeconds();
    int testTime = curEpochSec + 3;

    globalPriorityQueue.enqueue(new QueueData(curEpochSec + 1, 1L));
    globalPriorityQueue.enqueue(new QueueData(curEpochSec + 3, 12L));

    assertThat(globalPriorityQueue.getIdListSortedByPriority(testTime)).isNotEmpty();

    globalPriorityQueue.clear();
    assertThat(globalPriorityQueue.getIdListSortedByPriority(testTime)).isEmpty();

  }

  @Test
  void enqueueInvalidIdTest() {

    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      globalPriorityQueue.enqueue(new QueueData(Util.getNowCurrentEpochSeconds(), 0L));
    });
  }

  @Test
  void enqueueInvalidTimeTest() {

    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      globalPriorityQueue.enqueue(new QueueData(Util.getNowCurrentEpochSeconds() - 10000000, 0L));
    });
  }

  @Test
  void enqueueRepeatedIdTest() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      globalPriorityQueue.enqueue(new QueueData(Util.getNowCurrentEpochSeconds() + 1, 1L));
      globalPriorityQueue.enqueue(new QueueData(Util.getNowCurrentEpochSeconds() + 2, 1L));
    });
  }

  @Test
  void enqueueSameNonManagerCategoryAndListTest() {
    int curEpochSec = Util.getNowCurrentEpochSeconds();
    int testTime = curEpochSec + 30;

    QueueData queueData1 = new QueueData(curEpochSec + 10, 3);
    Pair<QueueData, Double> pair1 = new ImmutablePair<>(queueData1,
        QueueData.getPriority(QueueCategory.PRIORITY, testTime - queueData1.getEnqueueTimeSec()));
    assertThat(QueueData.getOrderCategory(queueData1)).isEqualTo(QueueCategory.PRIORITY);

    QueueData queueData2 = new QueueData(curEpochSec + 20, 9);
    Pair<QueueData, Double> pair2 = new ImmutablePair<>(queueData2,
        QueueData.getPriority(QueueCategory.PRIORITY, testTime - queueData2.getEnqueueTimeSec()));
    assertThat(QueueData.getOrderCategory(queueData2)).isEqualTo(QueueCategory.PRIORITY);

    QueueData queueData3 = new QueueData(curEpochSec + 30, 12);
    Pair<QueueData, Double> pair3 = new ImmutablePair<>(queueData3,
        QueueData.getPriority(QueueCategory.PRIORITY, testTime - queueData3.getEnqueueTimeSec()));
    assertThat(QueueData.getOrderCategory(queueData3)).isEqualTo(QueueCategory.PRIORITY);

    List<Pair<QueueData, Double>> pairs = Stream.of(pair1, pair2, pair3)
        .sorted((o1, o2) -> o2.getRight().compareTo(o1.getRight())).collect(Collectors.toList());

    pairs.forEach(data -> globalPriorityQueue.enqueue(data.getLeft()));
    List<Long> expected = pairs.stream().map(elem -> elem.getLeft().getId()).collect(Collectors.toList());

    List<Long> result = globalPriorityQueue.getIdListSortedByPriority(testTime);
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void enqueueSamePrioritiesAndListTest() {
    int curEpochSec = Util.getNowCurrentEpochSeconds();
    int testTime = curEpochSec;

    QueueData queueData1 = new QueueData(curEpochSec, 3 * 5);
    assertThat(QueueData.getOrderCategory(queueData1)).isEqualTo(QueueCategory.MANAGEMENT);
    globalPriorityQueue.enqueue(queueData1);

    QueueData queueData2 = new QueueData(curEpochSec, 3 * 5 * 3);
    assertThat(QueueData.getOrderCategory(queueData2)).isEqualTo(QueueCategory.MANAGEMENT);
    globalPriorityQueue.enqueue(queueData2);

    QueueData queueData3 = new QueueData(curEpochSec, 22);
    assertThat(QueueData.getOrderCategory(queueData3)).isEqualTo(QueueCategory.NORMAL);
    globalPriorityQueue.enqueue(queueData3);

    QueueData queueData4 = new QueueData(curEpochSec, 23);
    assertThat(QueueData.getOrderCategory(queueData4)).isEqualTo(QueueCategory.NORMAL);
    globalPriorityQueue.enqueue(queueData4);

    List<Long> result = globalPriorityQueue.getIdListSortedByPriority(testTime);
    assertThat(result.subList(0, 2)).containsExactlyInAnyOrder(3 * 5L, 3 * 5 * 3L);
    assertThat(result.subList(2, 4)).containsExactlyInAnyOrder(22L, 23L);

  }

  @Test
  void enqueueDifferentCategoriesAndListTest() {
    int curEpochSec = Util.getNowCurrentEpochSeconds();
    int testTime = curEpochSec + 40;

    QueueData queueData1 = new QueueData(curEpochSec + 10, 3 * 5);
    Pair<QueueData, Double> pair1 = new ImmutablePair<>(queueData1,
        QueueData.getPriority(QueueCategory.MANAGEMENT, testTime - queueData1.getEnqueueTimeSec()));
    assertThat(QueueData.getOrderCategory(queueData1)).isEqualTo(QueueCategory.MANAGEMENT);

    QueueData queueData2 = new QueueData(curEpochSec + 20, 3);
    Pair<QueueData, Double> pair2 = new ImmutablePair<>(queueData2,
        QueueData.getPriority(QueueCategory.PRIORITY, testTime - queueData2.getEnqueueTimeSec()));
    assertThat(QueueData.getOrderCategory(queueData2)).isEqualTo(QueueCategory.PRIORITY);

    QueueData queueData3 = new QueueData(curEpochSec + 30, 5);
    Pair<QueueData, Double> pair3 = new ImmutablePair<>(queueData3,
        QueueData.getPriority(QueueCategory.VIP, testTime - queueData3.getEnqueueTimeSec()));
    assertThat(QueueData.getOrderCategory(queueData3)).isEqualTo(QueueCategory.VIP);

    QueueData queueData4 = new QueueData(curEpochSec + 40, 22);
    Pair<QueueData, Double> pair4 = new ImmutablePair<>(queueData4,
        QueueData.getPriority(QueueCategory.NORMAL, testTime - queueData4.getEnqueueTimeSec()));
    assertThat(QueueData.getOrderCategory(queueData4)).isEqualTo(QueueCategory.NORMAL);

    List<Pair<QueueData, Double>> pairs = Stream.of(pair1, pair2, pair3, pair4)
        .sorted((o1, o2) -> o2.getRight().compareTo(o1.getRight())).collect(Collectors.toList());

    pairs.forEach(data -> globalPriorityQueue.enqueue(data.getLeft()));
    List<Long> expected = pairs.stream().map(elem -> elem.getLeft().getId()).collect(Collectors.toList());

    List<Long> result = globalPriorityQueue.getIdListSortedByPriority(testTime);
    assertThat(result).isEqualTo(expected);

  }

  @Test
  void findPositionIdManagementCategoryDifferentPrioritiesTest() {
    int curEpochSec = Util.getNowCurrentEpochSeconds();
    int testTime = curEpochSec + 30;

    QueueData queueData1 = new QueueData(curEpochSec + 10, 3 * 5);
    assertThat(QueueData.getOrderCategory(queueData1)).isEqualTo(QueueCategory.MANAGEMENT);
    globalPriorityQueue.enqueue(queueData1);

    QueueData queueData2 = new QueueData(curEpochSec + 20, 3 * 5 * 3);
    assertThat(QueueData.getOrderCategory(queueData2)).isEqualTo(QueueCategory.MANAGEMENT);
    globalPriorityQueue.enqueue(queueData2);

    QueueData queueData3 = new QueueData(curEpochSec + 30, 3 * 5 * 3 * 5);
    assertThat(QueueData.getOrderCategory(queueData3)).isEqualTo(QueueCategory.MANAGEMENT);
    globalPriorityQueue.enqueue(queueData3);

    List<Long> resultList = globalPriorityQueue.getIdListSortedByPriority(testTime);

    assertThat(globalPriorityQueue.getPosition(queueData1.getId(), testTime))
        .isEqualTo(0);
    assertThat(globalPriorityQueue.getPosition(queueData2.getId(), testTime))
        .isEqualTo(1);
    assertThat(globalPriorityQueue.getPosition(queueData3.getId(), testTime))
        .isEqualTo(2);

    assertThat(resultList.get(0))
        .isEqualTo(resultList.get(globalPriorityQueue.getPosition(queueData1.getId(),testTime)));
    assertThat(resultList.get(1))
        .isEqualTo(resultList.get(globalPriorityQueue.getPosition(queueData2.getId(), testTime)));
    assertThat(resultList.get(2))
        .isEqualTo(resultList.get(globalPriorityQueue.getPosition(queueData3.getId(), testTime)));

  }

  @Test
  void findPositionIdNonManagementCategoriesDifferentPrioritiesTest() {
    int curEpochSec = Util.getNowCurrentEpochSeconds();
    int testTime = curEpochSec + 40;

    QueueData queueData1 = new QueueData(curEpochSec + 10, 5);
    assertThat(QueueData.getOrderCategory(queueData1)).isEqualTo(QueueCategory.VIP);
    Pair<QueueData, Double> pair1 = new ImmutablePair<>(queueData1,
        QueueData.getPriority(QueueCategory.VIP, testTime - queueData1.getEnqueueTimeSec()));

    QueueData queueData2 = new QueueData(curEpochSec + 20, 22);
    assertThat(QueueData.getOrderCategory(queueData2)).isEqualTo(QueueCategory.NORMAL);
    Pair<QueueData, Double> pair2 = new ImmutablePair<>(queueData2,
        QueueData.getPriority(QueueCategory.NORMAL, testTime - queueData2.getEnqueueTimeSec()));

    QueueData queueData3 = new QueueData(curEpochSec + 30, 23);
    assertThat(QueueData.getOrderCategory(queueData3)).isEqualTo(QueueCategory.NORMAL);
    Pair<QueueData, Double> pair3 = new ImmutablePair<>(queueData3,
        QueueData.getPriority(QueueCategory.NORMAL, testTime - queueData3.getEnqueueTimeSec()));

    QueueData queueData4 = new QueueData(curEpochSec + 40, 3);
    assertThat(QueueData.getOrderCategory(queueData4)).isEqualTo(QueueCategory.PRIORITY);
    Pair<QueueData, Double> pair4 = new ImmutablePair<>(queueData4,
        QueueData.getPriority(QueueCategory.PRIORITY, testTime - queueData4.getEnqueueTimeSec()));

    List<Pair<QueueData, Double>> pairs = Stream.of(pair1, pair2, pair3, pair4)
        .sorted((o1, o2) -> o2.getRight().compareTo(o1.getRight())).collect(Collectors.toList());

    pairs.forEach(data -> globalPriorityQueue.enqueue(data.getLeft()));
    List<Long> expected = pairs.stream().map(elem -> elem.getLeft().getId()).collect(Collectors.toList());

    List<Long> result = globalPriorityQueue.getIdListSortedByPriority(testTime);
    assertThat(result).isEqualTo(expected);

    for (int i = 0; i < pairs.size() - 1; i++) {
      assertThat(globalPriorityQueue.getPosition(pairs.get(i).getLeft().getId(), testTime)).isEqualTo(i);
    }

  }

  @Test
  void averageOverSingleCategoryTest() {
    int curEpochSec = Util.getNowCurrentEpochSeconds();
    int testTime = curEpochSec + 30;

    QueueData queueData1 = new QueueData(curEpochSec + 10, 3);
    Pair<QueueData, Double> pair1 = new ImmutablePair<>(queueData1,
        QueueData.getPriority(QueueCategory.PRIORITY, queueData1.getEnqueueTimeSec() - curEpochSec));
    assertThat(QueueData.getOrderCategory(queueData1)).isEqualTo(QueueCategory.PRIORITY);

    QueueData queueData2 = new QueueData(curEpochSec + 20, 9);
    Pair<QueueData, Double> pair2 = new ImmutablePair<>(queueData2,
        QueueData.getPriority(QueueCategory.PRIORITY, queueData2.getEnqueueTimeSec() - curEpochSec));
    assertThat(QueueData.getOrderCategory(queueData2)).isEqualTo(QueueCategory.PRIORITY);

    QueueData queueData3 = new QueueData(curEpochSec + 30, 12);
    Pair<QueueData, Double> pair3 = new ImmutablePair<>(queueData3,
        QueueData.getPriority(QueueCategory.PRIORITY, queueData3.getEnqueueTimeSec() - curEpochSec));
    assertThat(QueueData.getOrderCategory(queueData3)).isEqualTo(QueueCategory.PRIORITY);

    List<Pair<QueueData, Double>> pairs = Stream.of(pair1, pair2, pair3)
        .sorted((o1, o2) -> o2.getRight().compareTo(o1.getRight())).collect(Collectors.toList());

    pairs.forEach(data -> globalPriorityQueue.enqueue(data.getLeft()));
    double avrExpected = pairs.stream().map(elem -> testTime - elem.getLeft().getEnqueueTimeSec())
        .mapToInt(value -> value).average().orElse(-1.0);

    double avrResult = globalPriorityQueue.getAverageWaitTime(testTime);
    assertThat(avrExpected).isCloseTo(avrResult, TestConst.TEST_DOUBLE_OFFSET);

  }

  @Test
  void testAverageOverEmptyQueueTest() {
    double avrExpected = globalPriorityQueue.getAverageWaitTime(Util.getNowCurrentEpochSeconds());
    assertThat(avrExpected).isCloseTo(-1.0, TestConst.TEST_DOUBLE_OFFSET);
  }

  @Test
  void bulkRandomGeneratedOrdersDequeueTest() {
    int testTime = enqueueBulkData();


    double prevPriorityDequeued = -1.0;
    for (int i = 0; i < BULK_ODER_TEST; i++) {
      QueueData dequeuedData = globalPriorityQueue.dequeue(testTime);

      double priorityDequeued = QueueData.getPriority(QueueData.getOrderCategory(dequeuedData),
          testTime - dequeuedData.getEnqueueTimeSec());

      if (prevPriorityDequeued > 0) {
        assertThat(prevPriorityDequeued >= priorityDequeued).isTrue();
      }

      prevPriorityDequeued = priorityDequeued;
    }
  }

  @Test
  void bulkRandomGeneratedOrdersPositionCorrectTest() {
    int testTime = enqueueBulkData();

    List<Long> idList = globalPriorityQueue.getIdListSortedByPriority(testTime);
    assertThat(idList.size()).isEqualTo(BULK_ODER_TEST);

    for (int i = 0; i < idList.size(); i++) {
      long topId = idList.get(i);
      assertThat(globalPriorityQueue.getPosition(topId, testTime)).isEqualTo(i);
    }
  }

  private int enqueueBulkData() {
    int curEpochSec = Util.getNowCurrentEpochSeconds();

    int beginInsertionTime = curEpochSec + (int) Duration.of(1, ChronoUnit.MINUTES).getSeconds();
    int endInsertionTime = beginInsertionTime + (int) Duration.of(90, ChronoUnit.HOURS).getSeconds();
    int testTime = endInsertionTime + (int) Duration.of(2, ChronoUnit.SECONDS).getSeconds();

    ThreadLocalRandom random = ThreadLocalRandom.current();
    Map<QueueCategory, Integer> categories = new HashMap<>();
    for (int i = 0; i < BULK_ODER_TEST; i++) {

      int insertTime = random.nextInt(endInsertionTime - beginInsertionTime) + beginInsertionTime;
      QueueData queueData = new QueueData(insertTime, i + 1);

      QueueCategory category = QueueData.getOrderCategory(queueData);
      categories.put(category, categories.getOrDefault(category, 0) + 1);

      globalPriorityQueue.enqueue(queueData);
    }
    assertThat(categories.values().stream().mapToInt(value -> value).filter(value -> value == 0).count()).isZero();

    return testTime;
  }


}