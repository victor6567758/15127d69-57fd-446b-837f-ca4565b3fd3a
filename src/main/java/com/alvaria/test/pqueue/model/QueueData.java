package com.alvaria.test.pqueue.model;

import lombok.Data;

@Data
public class QueueData {

  private final int enqueueTimeSec;
  private final long id;

  public static double getPriority(QueueType queueType, int secs) {
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

  public static QueueType getOrderType(QueueData queueData) {
    boolean div3 = queueData.getId() % 3 == 0;
    boolean div5 = queueData.getId() % 5 == 0;
    if (div3 && div5) {
      return QueueType.MANAGEMENT;
    } else if (div5) {
      return QueueType.VIP;
    } else if (div3) {
      return QueueType.PRIORITY;
    }
    return QueueType.NORMAL;
  }



}
