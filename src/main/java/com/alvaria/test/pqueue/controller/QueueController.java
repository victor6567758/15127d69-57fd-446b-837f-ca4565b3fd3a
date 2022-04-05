package com.alvaria.test.pqueue.controller;

import com.alvaria.test.pqueue.model.response.QueueDataResponse;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.Min;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/api")
@Validated
public interface QueueController {

  @PostMapping("/{id}/{createTime}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  void enqueueOrder(
      @PathVariable("id") @Min(1) long id,
      @DateTimeFormat(pattern = "ddMMyyyy_HHmmss") @PathVariable("createTime") LocalDateTime createTime);

  @GetMapping("/top")
  QueueDataResponse dequeueOrder();

  @GetMapping("/")
  List<Long> getIdListSortedByPriority();

  @DeleteMapping("/{id}")
  void remove(@PathVariable("id") long id);

  @GetMapping("/position/{id}")
  long getPosition(@PathVariable("id") @Min(1) long id);

  @GetMapping("/avrwait/{currentTime}")
  double getAverageWaitTime(
      @DateTimeFormat(pattern = "ddMMyyyy_HHmmss") @PathVariable("currentTime") LocalDateTime currentTime);
}
