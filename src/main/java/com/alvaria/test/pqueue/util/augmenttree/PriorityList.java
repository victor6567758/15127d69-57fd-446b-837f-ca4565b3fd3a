package com.alvaria.test.pqueue.util.augmenttree;


import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class PriorityList<T> implements Iterable<T> {

  private class PriorityListIterator implements Iterator<T> {

    private PriorityListNode<T> nextNode;

    private PriorityListNode<T> prevNode;

    private PriorityListIterator() {
      prevNode = null;
      if (root.size > 0) {
        nextNode = root.min();
      } else {
        nextNode = null;
      }

    }

    @Override
    public boolean hasNext() {
      return nextNode != null;
    }

    @Override
    public T next() {
      if (nextNode == null) {
        throw new NoSuchElementException("Reached the end of the list");
      }

      prevNode = nextNode;
      nextNode = nextNode.successor();
      return prevNode.value;
    }


  }


  /**
   * The dummy leaf node.
   */
  private final PriorityListNode<T> leaf = new PriorityListNode<>(null);

  private final Comparator<PriorityListNode<T>> comparator;

  private final Comparator<T> valueComparator;

  public PriorityListNode<T> root = leaf;

  public PriorityList(Comparator<T> valueComparator) {
    this.valueComparator = valueComparator;
    comparator = (node1, node2) -> valueComparator.compare(node1.value, node2.value);
  }

  @Override
  public Iterator<T> iterator() {
    return new PriorityListIterator();
  }


  public void add(T value) {
    PriorityListNode<T> newNode = new PriorityListNode<>(value);
    root = root.insert(newNode, false, comparator);
  }

  /**
   * Returns the node containing the specified value, if any.
   */
  public PriorityListNode<T> find(T value) {
    PriorityListNode<T> node = root;
    while (!node.isLeaf()) {
      int c = valueComparator.compare(value, node.value);
      if (c == 0) {
        return node;
      } else if (c < 0) {
        node = node.left;
      } else {
        node = node.right;
      }
    }
    return null;
  }

  public boolean contains(T value) {
    return find(value) != null;
  }

  public void remove(T value) {
    PriorityListNode<T> node = find(value);
    if (node != null) {
      root = node.remove();
    }
  }

  /**
   * Returns the (rank + 1)th node in the subtree rooted at "node".
   */
  public PriorityListNode<T> getNodeWithRank(PriorityListNode<T> node, int rank) {
    if (rank < 0 || rank >= node.size) {
      throw new IndexOutOfBoundsException();
    }
    if (rank == node.left.size) {
      return node;
    } else if (rank < node.left.size) {
      return getNodeWithRank(node.left, rank);
    } else {
      return getNodeWithRank(node.right, rank - node.left.size - 1);
    }
  }


  /**
   * Returns the (rank + 1)th-smallest value in the tree.
   */
  public T getItemWithRank(int rank) {
    return getNodeWithRank(root, rank).value;
  }

  public int size() {
    return root.size;
  }

  public int rank(PriorityListNode<T> node) {
    int r = node.left.size;
    PriorityListNode<T> y = node;

    while (comparator.compare(y, root) != 0) {
      int c = comparator.compare(y, y.parent.right);
      if (c == 0) {
        r = r + y.parent.left.size + 1;
      }
      y = y.parent;
    }

    return r;
  }
}
