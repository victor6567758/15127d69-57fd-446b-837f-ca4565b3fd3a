package com.alvaria.test.pqueue.util;

import static org.assertj.core.api.Assertions.offset;

import org.assertj.core.data.Offset;

public class TestConst {
  public static final Offset<Double> TEST_DOUBLE_OFFSET = offset(0.0000001);
}
