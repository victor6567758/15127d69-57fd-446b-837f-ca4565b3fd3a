package com.alvaria.test.pqueue.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum QueueCategory {
  DUMMY(-1),
  MANAGEMENT(0),
  PRIORITY(1),
  VIP(2),
  NORMAL(3);

  @Getter
  final int idx;
}
