package com.alvaria.test.pqueue.service.queues.rules;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RemoveRuleTest {

  @Getter
  @Setter
  private abstract static class RemoveRuleMocked implements RemoveRule {

    protected boolean removed;
  }

  private static class Rule1Mocked extends RemoveRuleMocked {

    @Override
    public boolean removeHelper(long id) {
      if (id == 1) {
        removed = true;
        return true;
      }
      ;
      return false;
    }
  }

  private static class Rule2Mocked extends RemoveRuleMocked {

    @Override
    public boolean removeHelper(long id) {
      if (id == 2) {
        removed = true;
        return true;
      }
      ;
      return false;
    }
  }

  private final Rule1Mocked routedRule1 = new Rule1Mocked();
  private final Rule2Mocked routedRule2 = new Rule2Mocked();
  private final RemoveRule removeRule = routedRule1.appendNextRemoveRule(routedRule2);


  @BeforeEach
  public void setUp() {
    routedRule1.setRemoved(false);
    routedRule2.setRemoved(false);
  }

  @Test
  void removeRule1Test() {
    removeRule.removeHelper(1);
    assertThat(routedRule1.isRemoved()).isTrue();
    assertThat(routedRule2.isRemoved()).isFalse();
  }

  @Test
  void removeRule2Test() {
    removeRule.removeHelper(2);
    assertThat(routedRule1.isRemoved()).isFalse();
    assertThat(routedRule2.isRemoved()).isTrue();
  }

  @Test
  void removeRuleNoMatchTest() {
    removeRule.removeHelper(3);
    assertThat(routedRule1.isRemoved()).isFalse();
    assertThat(routedRule2.isRemoved()).isFalse();
  }
}