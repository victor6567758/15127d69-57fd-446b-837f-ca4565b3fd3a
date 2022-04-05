package com.alvaria.test.pqueue.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Util {

  public static int getNowCurrentEpochSeconds() {
    return getSeconds(LocalDateTime.now());
  }

  public static int getSeconds(LocalDateTime localDateTime) {
    return (int) localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond();
  }

  public String epochSecondToString(int epochSec) {
    return Instant.ofEpochSecond(epochSec).atZone(ZoneId.systemDefault()).format(
        DateTimeFormatter.ISO_LOCAL_DATE_TIME);
  }

  public String epochSecondToString(int epochSec, String pattern) {
    return Instant.ofEpochSecond(epochSec).atZone(ZoneId.systemDefault()).format(
        DateTimeFormatter.ofPattern(pattern));
  }


}
