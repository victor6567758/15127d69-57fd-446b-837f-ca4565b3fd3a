package com.alvaria.test.pqueue.controller;

import com.alvaria.test.pqueue.service.QueueService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController()
public class QueueControllerImpl implements QueueController {

  private final QueueService queueService;

  public void createNewOrder(long id, LocalDateTime createTime) {
    queueService.createNewOrder(id, createTime);
  }

  public long pollOrder() {
    return queueService.pollOrder();
  }

  public List<Long> listOrders() {
    return queueService.listOrders();
  }

  public void removeOrder(long id) {
    queueService.removeOrder(id);
  }

  public long getOrderPosition(long id) {
    return queueService.getOrderPosition(id);
  }

  public double getAverageWaitTime(LocalDateTime currentTime) {
    return queueService.getAverageWaitTime(currentTime);
  }
}
