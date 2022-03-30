package com.alvaria.test.pqueue.util.augmenttree;

public class PriorityListNode<T> extends RedBlackNode<PriorityListNode<T>> {
  /** The value we are storing in the node. */
  public final T value;

  /** The number of nodes in this subtree. */
  public int size;

  public PriorityListNode(T value) {
    this.value = value;
  }

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

  @Override
  public void assertNodeIsValid() {
    int expectedSize;
    if (isLeaf()) {
      expectedSize = 0;
    } else {
      expectedSize = left.size + right.size + 1;
    }
    if (size != expectedSize) {
      throw new RuntimeException("The node's size does not match that of the children");
    }
  }


}
