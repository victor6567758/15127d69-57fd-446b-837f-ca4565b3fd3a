package com.alvaria.test.pqueue.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"id"})
public class QueueData {

  private final long enqueueEpochTimeSec;
  private final long id;
  private int seqNum = -1;
}
