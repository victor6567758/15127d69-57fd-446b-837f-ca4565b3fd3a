package com.alvaria.test.pqueue.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class QueueDataTest {
  @Test
  void managementIdTest() {
    int id = 15;
    assertThat(id % 3 == 0 && id % 5 == 0).isTrue();
    assertThat(QueueData.getOrderType(new QueueData(1000, 15))).isEqualTo(QueueType.MANAGEMENT);
  }

  @Test
  void vipIdTest() {
    int id = 5;
    assertThat(id % 3 != 0 && id % 5 == 0).isTrue();
    assertThat(QueueData.getOrderType(new QueueData(1000, 5))).isEqualTo(QueueType.VIP);
  }

  @Test
  void priorityIdTest() {
    int id = 3;
    assertThat(id % 3 == 0 && id % 5 != 0).isTrue();
    assertThat(QueueData.getOrderType(new QueueData(1000, 3))).isEqualTo(QueueType.PRIORITY);
  }

  @Test
  void normalIdTest() {
    int id = 22;
    assertThat(id % 3 != 0 && id % 5 != 0).isTrue();
    assertThat(QueueData.getOrderType(new QueueData(1000, 22))).isEqualTo(QueueType.NORMAL);
  }
}