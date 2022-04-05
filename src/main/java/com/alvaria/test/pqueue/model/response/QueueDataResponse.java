package com.alvaria.test.pqueue.model.response;

import com.alvaria.test.pqueue.model.QueueData;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class QueueDataResponse {

  public QueueDataResponse(QueueData queueData) {
    id = queueData.getId();
    enqueueTime = Instant.ofEpochSecond(queueData.getEnqueueTimeSec())
        .atZone(ZoneId.systemDefault()).toLocalDateTime();
  }

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime enqueueTime;
  private long id;
}
