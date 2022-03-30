package com.alvaria.test.pqueue.controller;


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

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class QueueControllerTest {

  @MockBean
  private QueueService queueService;

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  void vanillaListOrdersTest() {

    given(queueService.listOrders()).willReturn(Collections.emptyList());

    ResponseEntity<List> response = restTemplate.getForEntity("/api/", List.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEmpty();
  }

  @Test
  void vanillaCreateNewOrder() {

    LocalDateTime now = LocalDateTime.now();
    String stringNow = now.format(DateTimeFormatter.ofPattern(Const.REST_DATETIME_FORMAT));
    willDoNothing().given(queueService).createNewOrder(1L, now);

    ResponseEntity<Void> response = restTemplate.postForEntity("/api/1/" + stringNow, null, Void.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  @Test
  void createNewOrderInvalidDate() {

    willDoNothing().given(queueService).createNewOrder(1L, LocalDateTime.now());

    ResponseEntity<Void> response = restTemplate.postForEntity("/api/1/dummy_date", null, Void.class);

    assertThat(response.getStatusCode()).isNotEqualTo(HttpStatus.OK);
  }

}