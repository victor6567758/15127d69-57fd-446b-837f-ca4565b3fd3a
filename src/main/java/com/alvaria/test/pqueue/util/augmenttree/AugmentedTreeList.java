package com.alvaria.test.pqueue.util.augmenttree;


import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class AugmentedTreeList<T> implements Iterable<T> {

  private class AugmentedTreeListIterator implements Iterator<T> {

    private final int modCount = AugmentedTreeList.this.modCount;

    private AugmentedTreeListNode<T> nextNode;

    private AugmentedTreeListNode<T> prevNode;

    private AugmentedTreeListIterator() {
      prevNode = null;
      if (root.getSize() > 0) {
        nextNode = root.min();
      } else {
        nextNode = null;
      }

    }

    @Override
    public boolean hasNext() {
      if (modCount != AugmentedTreeList.this.modCount) {
        throw new ConcurrentModificationException();
      }
      return nextNode != null;
    }

    @Override
    public T next() {
      if (nextNode == null) {
        throw new NoSuchElementException("Reached the end of the list");
      } else if (modCount != AugmentedTreeList.this.modCount) {
        throw new ConcurrentModificationException();
      }

      prevNode = nextNode;
      nextNode = nextNode.successor();
      return prevNode.getValue();
    }

  }

  private final AugmentedTreeListNode<T> leaf = new AugmentedTreeListNode<>(null);

  private final Comparator<AugmentedTreeListNode<T>> nodeComparator;

  private final Comparator<T> valueComparator;

  private AugmentedTreeListNode<T> root = leaf;

  private int modCount = 0;

  public AugmentedTreeList(Comparator<T> valueComparator) {
    this.valueComparator = valueComparator;
    nodeComparator = (node1, node2) -> valueComparator.compare(node1.getValue(), node2.getValue());
  }

  @Override
  public Iterator<T> iterator() {
    return new AugmentedTreeListIterator();
  }

  public T poll() {
    AugmentedTreeListNode<T> maxNode = root.max();
    if (maxNode == null) {
      return null;
    }

    return maxNode.getValue();
  }

  public T dequeue() {
    AugmentedTreeListNode<T> maxNode = root.max();
    if (maxNode == null) {
      throw new IllegalArgumentException("Tree is empty");
    }

    maxNode.remove();
    return maxNode.getValue();
  }

  public void enqueue(T value) {
    AugmentedTreeListNode<T> newNode = new AugmentedTreeListNode<>(value);
    root = root.insert(newNode, false, nodeComparator);
    modCount++;
  }

  public T findItemWithRank(int rank) {
    return findNodeWithRank(root, rank).getValue();
  }

  public int size() {
    return root.getSize();
  }

  public int getItemRank(T value) {
    AugmentedTreeListNode<T> foundNode = findNode(value);
    if (foundNode == null) {
      throw new IllegalArgumentException("Node is not found");
    }
    return getNodeRank(foundNode);
  }

  public void remove(T value) {
    AugmentedTreeListNode<T> foundNode = findNode(value);
    if (foundNode == null) {
      throw new IllegalArgumentException("Node is not found");
    }

    foundNode.remove();
  }

  private AugmentedTreeListNode<T> findNodeWithRank(AugmentedTreeListNode<T> node, int rank) {
    if (rank < 0 || rank >= node.getSize()) {
      throw new IndexOutOfBoundsException();
    }
    if (rank == node.left.getSize()) {
      return node;
    } else if (rank < node.left.getSize()) {
      return findNodeWithRank(node.left, rank);
    } else {
      return findNodeWithRank(node.right, rank - node.left.getSize() - 1);
    }
  }

  private int getNodeRank(AugmentedTreeListNode<T> node) {
    int rank = node.left.getSize();
    AugmentedTreeListNode<T> curNode = node;

    while (valueComparator.compare(curNode.getValue(), root.getValue()) != 0) {
      int c = valueComparator.compare(curNode.getValue(), curNode.parent.right.getValue());
      if (c == 0) {
        rank += curNode.parent.left.getSize() + 1;
      }
      curNode = curNode.parent;
    }

    return rank;
  }


  private AugmentedTreeListNode<T> findNode(T value) {
    AugmentedTreeListNode<T> node = root;
    while (!node.isLeaf()) {
      int compare = valueComparator.compare(value, node.getValue());
      if (compare == 0) {
        return node;
      } else if (compare < 0) {
        node = node.left;
      } else {
        node = node.right;
      }
    }
    return null;
  }
}
