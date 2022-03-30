package com.alvaria.test.pqueue.service;

import static org.junit.jupiter.api.Assertions.*;
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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class QueueServiceTest {

  @Autowired
  private QueueService queueService;

  @Test
  void createNewOrderTest() {
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