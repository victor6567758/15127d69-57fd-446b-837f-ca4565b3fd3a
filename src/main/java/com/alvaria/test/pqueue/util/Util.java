package com.alvaria.test.pqueue.util;

import com.alvaria.test.pqueue.model.QueueType;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Util {
  public static int getCurrentSeconds() {
    return getSeconds(LocalDateTime.now());
  }

  public static int getSeconds(LocalDateTime localDateTime) {
    return (int) localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond();
  }


}
