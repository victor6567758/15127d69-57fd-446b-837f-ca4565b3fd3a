package com.alvaria.test.pqueue.util.augmenttree;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AugmentedTreeListNode<T> extends RedBlackNode<AugmentedTreeListNode<T>> {

  private final T value;

  private int size;

  @Override
  public boolean augment() {
    int newSize = left.size + right.size + 1;
    if (newSize == size) {
      return false;
    } else {
      size = newSize;
      return true;
    }
  }


}
