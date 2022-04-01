package com.alvaria.test.pqueue.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Util {
  public static int getCurrentSeconds() {
    return (int) LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();
  }
}
