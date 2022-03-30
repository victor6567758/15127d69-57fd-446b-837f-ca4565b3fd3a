package com.alvaria.test.pqueue.controller;

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
  void createNewOrder(
      @PathVariable("id") @Min(1) long id,
      @DateTimeFormat(pattern = "ddMMyyyy_HHmmss") @PathVariable("createTime") LocalDateTime createTime);

  @GetMapping("/top")
  long pollOrder();

  @GetMapping("/")
  List<Long> listOrders();

  @DeleteMapping("/{id}")
  void removeOrder(@PathParam("id") long id);

  @GetMapping("/position/{id}")
  long getOrderPosition(@PathVariable("id") @Min(1) long id);

  @GetMapping("/avrwait/{currentTime}")
  double getAverageWaitTime(
      @DateTimeFormat(pattern = "ddMMyyyy_HHmmss") @PathVariable("currentTime") LocalDateTime currentTime);
}
