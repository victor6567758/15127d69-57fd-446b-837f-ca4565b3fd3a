package com.alvaria.test.pqueue.controller;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

import com.alvaria.test.pqueue.model.QueueData;
import com.alvaria.test.pqueue.service.GlobalPriorityQueueService;
import com.alvaria.test.pqueue.util.Const;
import com.alvaria.test.pqueue.util.TestConst;
import com.alvaria.test.pqueue.util.Util;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class QueueControllerTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @MockBean
  private GlobalPriorityQueueService queueService;


  @Test
  void vanillaListOrdersTest() {

    given(queueService.getIdListSortedByPriority()).willReturn(Arrays.asList(1L, 2L, 3L));

    ResponseEntity<Integer[]> response = restTemplate.getForEntity("/api/", Integer[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).containsExactly(1, 2, 3);
  }

  @Test
  void vanillaEnqueueTest() {

    LocalDateTime now = LocalDateTime.now();
    String stringNow = now.format(DateTimeFormatter.ofPattern(Const.REST_DATETIME_FORMAT));
    willDoNothing().given(queueService).enqueue(new QueueData(Util.getSeconds(now), 1L));

    ResponseEntity<Void> response = restTemplate.postForEntity("/api/1/" + stringNow, null, Void.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
  }

  @Test
  void vanillaDequeueTest() {

    LocalDateTime now = LocalDateTime.now();
    int secs = Util.getSeconds(now);
    given(queueService.dequeue()).willReturn(new QueueData(secs, 2L));

    ResponseEntity<QueueData> response = restTemplate.getForEntity("/api/top", QueueData.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getId()).isEqualTo(2L);
    assertThat(response.getBody().getEnqueueTimeSec()).isEqualTo(secs);
  }

  @Test
  void createEnqueueOrderInvalidDateTest() {

    willDoNothing().given(queueService).enqueue(new QueueData(Util.getSeconds(LocalDateTime.now()), 1L));

    ResponseEntity<Void> response = restTemplate.postForEntity("/api/1/dummy_date", null, Void.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void createEnqueueOrderInvalidIdTest() {
    LocalDateTime now = LocalDateTime.now();
    String stringNow = now.format(DateTimeFormatter.ofPattern(Const.REST_DATETIME_FORMAT));
    willDoNothing().given(queueService).enqueue(new QueueData(Util.getSeconds(LocalDateTime.now()), 1L));

    ResponseEntity response = restTemplate.postForEntity("/api/-1/" + stringNow, null, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Test
  void vanillaRemoveByIdTest() {
    willDoNothing().given(queueService).remove(1L);

    ResponseEntity<Void> response = restTemplate.exchange("/api/1", HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void vanillaGetPositionTest() {
    given(queueService.getPosition(1L)).willReturn(48);

    ResponseEntity<Integer> response = restTemplate.getForEntity("/api/position/1", Integer.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo(48);
  }

  @Test
  void vanillaAvrAwaitTimeTest() {
    LocalDateTime now = LocalDateTime.now();
    String stringNow = now.format(DateTimeFormatter.ofPattern(Const.REST_DATETIME_FORMAT));

    given(queueService.getAverageWaitTime(Util.getSeconds(now))).willReturn(48.0);

    ResponseEntity<Double> response = restTemplate.getForEntity("/api/avrwait/" + stringNow, Double.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isCloseTo(48.0, TestConst.TEST_DOUBLE_OFFSET);
  }

}