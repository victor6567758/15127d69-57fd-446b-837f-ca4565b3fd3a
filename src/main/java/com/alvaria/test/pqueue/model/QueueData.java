package com.alvaria.test.pqueue.model;

import lombok.Data;

@Data
public class QueueData {

  private final int enqueueTimeSec;
  private final long id;

  public static double getPriority(QueueCategory queueType, int secs) {

    if (secs < 0) {
      throw new IllegalArgumentException("Invalid number of seconds for queued data");
    }

    switch (queueType) {
      case VIP:
        return Math.max(4.0, 2.0 * secs * Math.log(secs));
      case NORMAL:
        return secs;
      case PRIORITY:
        return Math.max(3.0, secs * Math.log(secs));
      case MANAGEMENT:
        return Double.MAX_VALUE;
      default:
        throw new IllegalArgumentException("Queue type is invalid");
    }
  }

  public static QueueCategory getOrderCategory(QueueData queueData) {
    boolean div3 = queueData.getId() % 3 == 0;
    boolean div5 = queueData.getId() % 5 == 0;
    if (div3 && div5) {
      return QueueCategory.MANAGEMENT;
    } else if (div5) {
      return QueueCategory.VIP;
    } else if (div3) {
      return QueueCategory.PRIORITY;
    }
    return QueueCategory.NORMAL;
  }



}
