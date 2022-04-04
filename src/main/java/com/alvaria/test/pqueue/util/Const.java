package com.alvaria.test.pqueue.util;

import java.time.format.DateTimeFormatter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Const {
  public static final String REST_DATETIME_FORMAT = "ddMMyyyy_HHmmss";

  public static final DateTimeFormatter REST_DATETIME_FORMAT_FORMATTER  =
      DateTimeFormatter.ofPattern(REST_DATETIME_FORMAT);
}
