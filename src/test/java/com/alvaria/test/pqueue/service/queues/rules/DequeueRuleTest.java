package com.alvaria.test.pqueue.service.queues.rules;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DequeueRuleTest {

  @Getter
  @Setter
  private abstract static class DequeueRuleMocked implements DequeueRule {

    protected boolean dequeueCalled;
    protected int id;
    protected double priority;
  }

  private static class Rule1Mocked extends DequeueRuleMocked {

    private Rule1Mocked() {
      id = 1;
      priority = 200;
    }

    @Override
    public Pair<DequeueRule, Double> dequeueHelper(double prevPriority, DequeueRule prevQueue) {
      if (prevQueue == null) {
        return new ImmutablePair<>(this, prevPriority);
      }

      return priority > prevPriority ? new ImmutablePair<>(this, priority) : new ImmutablePair<>(prevQueue, prevPriority);
    }
  }

  private static class Rule2Mocked extends DequeueRuleMocked {

    private Rule2Mocked() {
      id = 1;
      priority = 300;
    }

    @Override
    public Pair<DequeueRule, Double> dequeueHelper(double prevPriority, DequeueRule prevQueue) {
      if (prevQueue == null) {
        return new ImmutablePair<>(this, prevPriority);
      }

      return priority > prevPriority ? new ImmutablePair<>(this, priority) : new ImmutablePair<>(prevQueue, prevPriority);
    }
  }

  private static class Rule3Mocked extends DequeueRuleMocked {

    private Rule3Mocked() {
      id = 3;
      priority = 100;
    }

    @Override
    public Pair<DequeueRule, Double> dequeueHelper(double prevPriority, DequeueRule prevQueue) {
      if (prevQueue == null) {
        return new ImmutablePair<>(this, prevPriority);
      }

      return priority > prevPriority ? new ImmutablePair<>(this, priority) : new ImmutablePair<>(prevQueue, prevPriority);
    }
  }

  private final Rule1Mocked routedRule1 = new Rule1Mocked();
  private final Rule2Mocked routedRule2 = new Rule2Mocked();
  private final Rule3Mocked routedRule3 = new Rule3Mocked();
  private final DequeueRule dequeueRule = routedRule1.appendNextDequeueRule(routedRule2).appendNextDequeueRule(routedRule3);

  @BeforeEach
  public void setUp() {
    routedRule1.setDequeueCalled(false);
    routedRule2.setDequeueCalled(false);
    routedRule3.setDequeueCalled(false);
  }

  @Test
  void dequeueMaxPriorityTest() {
    DequeueRuleMocked topRule = (DequeueRuleMocked) dequeueRule.dequeueHelper(-1, null).getLeft();

    assertThat(topRule.getId()).isEqualTo(1);
  }

}