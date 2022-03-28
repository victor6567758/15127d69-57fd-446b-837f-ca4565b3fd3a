package com.alvaria.test.pqueue.service;

import java.time.LocalDateTime;
import java.util.List;

public interface QueueService {
  void createNewOrder(long id, LocalDateTime createTime);

  long pollOrder();

  List<Long> listOrders();

  void removeOrder(long id);

  long getOrderPosition(long id);

  double getAverageWaitTime(LocalDateTime currentTime);
}
