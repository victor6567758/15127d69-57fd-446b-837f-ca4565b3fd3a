package com.alvaria.test.pqueue.service.queues.rules;

import static org.assertj.core.api.Assertions.assertThat;

import com.alvaria.test.pqueue.model.QueueData;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EnqueueRuleTest {

  @Getter
  @Setter
  private abstract static class EnqueueRuleMocked implements EnqueueRule {

    protected boolean enqueueCalled;
  }

  private static class Rule1Mocked extends EnqueueRuleMocked {

    @Override
    public boolean enqueueHelper(QueueData queueData) {
      if (queueData.getId() % 3 == 0 && queueData.getId() % 5 == 0) {
        enqueueCalled = true;
        return true;
      }
      return false;
    }
  }

  private static class Rule2Mocked extends EnqueueRuleMocked {

    @Override
    public boolean enqueueHelper(QueueData queueData) {
      if (queueData.getId() % 3 == 0) {
        enqueueCalled = true;
        return true;
      }
      return false;
    }
  }

  private static class Rule3Mocked extends EnqueueRuleMocked {

    @Override
    public boolean enqueueHelper(QueueData queueData) {
      if (queueData.getId() % 5 == 0) {
        enqueueCalled = true;
        return true;
      }
      return false;
    }
  }

  private final Rule1Mocked enqueueRule1 = new Rule1Mocked();
  private final Rule2Mocked enqueueRule2 = new Rule2Mocked();
  private final Rule3Mocked enqueueRule3 = new Rule3Mocked();
  private final EnqueueRule enqueueRule = enqueueRule1.appendNextEnqueueRule(enqueueRule2).appendNextEnqueueRule(enqueueRule3);

  @BeforeEach
  public void setUp() {
    enqueueRule1.setEnqueueCalled(false);
    enqueueRule2.setEnqueueCalled(false);
    enqueueRule3.setEnqueueCalled(false);
  }

  @Test
  void routingIdRoute1Test() {
    int id_3_5_div = 3 * 5;
    enqueueRule.enqueueHelper(new QueueData(100, id_3_5_div));

    assertThat(enqueueRule1.isEnqueueCalled()).isTrue();
    assertThat(enqueueRule2.isEnqueueCalled()).isFalse();
    assertThat(enqueueRule3.isEnqueueCalled()).isFalse();

  }

  @Test
  void routingIdRoute2Test() {
    int id_3_div = 3;
    enqueueRule.enqueueHelper(new QueueData(100, id_3_div));

    assertThat(enqueueRule1.isEnqueueCalled()).isFalse();
    assertThat(enqueueRule2.isEnqueueCalled()).isTrue();
    assertThat(enqueueRule3.isEnqueueCalled()).isFalse();

  }

  @Test
  void routingIdRoute3Test() {
    int id_5_div = 5;
    enqueueRule.enqueueHelper(new QueueData(100, id_5_div));

    assertThat(enqueueRule1.isEnqueueCalled()).isFalse();
    assertThat(enqueueRule2.isEnqueueCalled()).isFalse();
    assertThat(enqueueRule3.isEnqueueCalled()).isTrue();

  }

  @Test
  void routingIdNoMatchTest() {
    int id_div = 1;
    assertThat(id_div % 3 == 0).isFalse();
    assertThat(id_div % 5 == 0).isFalse();

    enqueueRule.enqueueHelper(new QueueData(100, id_div));

    assertThat(enqueueRule1.isEnqueueCalled()).isFalse();
    assertThat(enqueueRule2.isEnqueueCalled()).isFalse();
    assertThat(enqueueRule3.isEnqueueCalled()).isFalse();

  }
}