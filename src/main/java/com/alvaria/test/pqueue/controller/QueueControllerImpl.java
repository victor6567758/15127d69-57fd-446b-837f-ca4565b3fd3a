package com.alvaria.test.pqueue.controller;

import com.alvaria.test.pqueue.model.QueueData;
import com.alvaria.test.pqueue.service.GlobalPriorityQueueService;
import com.alvaria.test.pqueue.util.Util;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController()
public class QueueControllerImpl implements QueueController {

  private final GlobalPriorityQueueService globalPriorityQueue;

  public void enqueueOrder(long id, LocalDateTime createTime) {
    globalPriorityQueue.enqueue(new QueueData(Util.getSeconds(createTime), id));
  }

  public QueueData dequeueOrder() {
    return globalPriorityQueue.dequeue();
  }

  public List<Long> getIdListSortedByPriority() {
    return globalPriorityQueue.getIdListSortedByPriority();
  }

  public void remove(long id) {
    globalPriorityQueue.remove(id);
  }

  public long getPosition(long id) {
    return globalPriorityQueue.getPosition(id);
  }

  public double getAverageWaitTime(LocalDateTime currentTime) {
    return globalPriorityQueue.getAverageWaitTime(Util.getSeconds(currentTime));
  }
}
