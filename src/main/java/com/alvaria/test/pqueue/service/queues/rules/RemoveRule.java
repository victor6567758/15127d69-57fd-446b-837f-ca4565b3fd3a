package com.alvaria.test.pqueue.service.queues.rules;

@FunctionalInterface
public interface RemoveRule {

  boolean removeHelper(long id);

  default RemoveRule appendNextRemoveRule(RemoveRule removeRule) {
    return id -> {
      if (!removeHelper(id)) {
        return removeRule.removeHelper(id);
      }
      return true;
    };
  }
}
