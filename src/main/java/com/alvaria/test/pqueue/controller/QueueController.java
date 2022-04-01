package com.alvaria.test.pqueue.controller;

import com.alvaria.test.pqueue.model.QueueData;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.Min;
import javax.websocket.server.PathParam;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.validation.annotation.Validated;

@RequestMapping("/api")
@Validated
public interface QueueController {

  @PostMapping("/{id}/{createTime}")
  void enqueueOrder(
      @PathVariable("id") @Min(1) long id,
      @DateTimeFormat(pattern = "ddMMyyyy_HHmmss") @PathVariable("createTime") LocalDateTime createTime);

  @GetMapping("/top")
  QueueData dequeueOrder();

  @GetMapping("/")
  List<Long> getIdListSortedByPriority();

  @DeleteMapping("/{id}")
  void remove(@PathParam("id") long id);

  @GetMapping("/position/{id}")
  long getPosition(@PathVariable("id") @Min(1) long id);

  @GetMapping("/avrwait/{currentTime}")
  double getAverageWaitTime(
      @DateTimeFormat(pattern = "ddMMyyyy_HHmmss") @PathVariable("currentTime") LocalDateTime currentTime);
}
