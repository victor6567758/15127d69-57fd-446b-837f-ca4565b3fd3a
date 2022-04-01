package com.alvaria.test.pqueue.service.queues.rules;

@FunctionalInterface
public interface RemoveRule {

  boolean removeHelper(long id);

  default RemoveRule appendNextRemove(RemoveRule removeRule) {
    return id -> {
      if (!removeHelper(id)) {
        return removeRule.removeHelper(id);
      }
      return false;
    };
  }
}
