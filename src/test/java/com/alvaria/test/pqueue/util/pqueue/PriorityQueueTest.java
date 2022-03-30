package com.alvaria.test.pqueue.util.pqueue;

import static org.junit.jupiter.api.Assertions.*;

import com.alvaria.test.pqueue.model.QueueData;
import com.alvaria.test.pqueue.util.Util;
import com.alvaria.test.pqueue.util.augmenttree.PriorityList;
import com.alvaria.test.pqueue.util.augmenttree.PriorityListNode;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;

import com.alvaria.test.pqueue.service.QueueService;
import com.alvaria.test.pqueue.util.Const;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class PriorityQueueTest {

  @Test
  void enqueueVanillaTest() {
    PriorityQueue priorityQueue = new PriorityQueueImpl();

    QueueData queueData1 = new QueueData(Util.getCurrentSeconds() - 2, 1L);
    QueueData queueData2 = new QueueData(Util.getCurrentSeconds() - 3, 2L);

    priorityQueue.enqueue(queueData1);
    priorityQueue.enqueue(queueData2);

    List<QueueData> dataList = priorityQueue.getList();
    assertThat(dataList).isNotEmpty();

  }

  @Test
  void dequeue() {
  }

  @Test
  void poll() {
  }

  @Test
  void getList() {
  }

  @Test
  void getPosition() {
  }

  @Test
  void isEmpty() {
  }

  @Test
  void avrSeqWaitTime() {
  }

  @Test
  void dummyTest1() {
    PriorityList<QueueData> list = new PriorityList<>(Comparator.comparingLong(QueueData::getEnqueueEpochTimeSec).reversed());

    long now = Util.getCurrentSeconds();
    QueueData qd0 = new QueueData(now - 8, 0L);
    QueueData qd1 = new QueueData(now - 4, 1L);
    QueueData qd2 = new QueueData(now - 4, 2L);
    QueueData qd3 = new QueueData(now - 3, 13L);
    QueueData qd4 = new QueueData(now - 2, 4L);
    QueueData qd5 = new QueueData(now - 1, 45L);
    QueueData qd6 = new QueueData(now - 0, 48L);


    list.add(qd1);
    list.add(qd2);
    list.add(qd3);
    list.add(qd4);
    list.add(qd5);
    list.add(qd6);
    list.add(qd0);



    for (QueueData qd: list) {
      System.out.println(qd);
      System.out.println("----");
    }

    PriorityListNode<QueueData> item0 = list.getNodeWithRank(list.root, 0);
    PriorityListNode<QueueData> item1 = list.getNodeWithRank(list.root, 1);
    PriorityListNode<QueueData> item2 = list.getNodeWithRank(list.root, 2);
    PriorityListNode<QueueData> item3 = list.getNodeWithRank(list.root, 3);
    PriorityListNode<QueueData> item4 = list.getNodeWithRank(list.root, 4);
    PriorityListNode<QueueData> item5 = list.getNodeWithRank(list.root, 5);

    PriorityListNode<QueueData> find13L = list.find(qd3);

    int rank0 = list.rank(item0);
    int rank1 = list.rank(item1);
    int rank2 = list.rank(item2);
    int rank3 = list.rank(item3);
    int rank4 = list.rank(item4);
    int rank5 = list.rank(item5);


    int t = 0;
  }
}