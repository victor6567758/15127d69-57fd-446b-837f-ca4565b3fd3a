package com.alvaria.test.pqueue.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class QueueServiceImpl implements QueueService {

  public void createNewOrder(long id, LocalDateTime createTime) {

  }

  public long pollOrder() {
    return -1;
  }

  public List<Long> listOrders() {
    return Collections.emptyList();
  }

  public void removeOrder(long id) {

  }

  public long getOrderPosition(long id) {
    return -1;
  }

  public double getAverageWaitTime(LocalDateTime currentTime) {
    return 0.0;
  }
}
