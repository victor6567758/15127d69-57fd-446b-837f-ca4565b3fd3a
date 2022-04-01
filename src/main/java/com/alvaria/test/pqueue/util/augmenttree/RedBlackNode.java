package com.alvaria.test.pqueue.util.augmenttree;

import java.util.Comparator;


public abstract class RedBlackNode<N extends RedBlackNode<N>> implements Comparable<N> {

  protected N parent;

  protected N left;

  protected N right;

  protected boolean isRed;


  public abstract boolean augment();


  public boolean isLeaf() {
    return left == null;
  }

  @SuppressWarnings("unchecked")
  public N root() {
    N node = (N) this;
    while (node.parent != null) {
      node = node.parent;
    }
    return node;
  }

  @SuppressWarnings("unchecked")
  public N min() {
    if (isLeaf()) {
      return null;
    }
    N node = (N) this;
    while (!node.left.isLeaf()) {
      node = node.left;
    }
    return node;
  }

  @SuppressWarnings("unchecked")
  public N max() {
    if (isLeaf()) {
      return null;
    }
    N node = (N) this;
    while (!node.right.isLeaf()) {
      node = node.right;
    }
    return node;
  }

  @SuppressWarnings("unchecked")
  public N predecessor() {
    if (!left.isLeaf()) {
      N node;
      node = left;
      while (!node.right.isLeaf()) {
        node = node.right;
      }
      return node;
    } else if (parent == null) {
      return null;
    } else {
      N node = (N) this;
      while (node.parent != null && node.parent.left == node) {
        node = node.parent;
      }
      return node.parent;
    }
  }

  @SuppressWarnings("unchecked")
  public N successor() {
    if (!right.isLeaf()) {
      N node;
      node = right;
      while (!node.left.isLeaf()) {
        node = node.left;
      }
      return node;
    } else if (parent == null) {
      return null;
    } else {
      N node = (N) this;
      while (node.parent != null && node.parent.right == node) {
        node = node.parent;
      }
      return node.parent;
    }
  }

  @SuppressWarnings("unchecked")
  public N insert(N newNode, boolean allowDuplicates, Comparator<? super N> comparator) {
    if (parent != null) {
      throw new IllegalArgumentException("This is not the root of a tree");
    }

    N nThis = (N) this;
    if (isLeaf()) {
      newNode.isRed = false;
      newNode.left = nThis;
      newNode.right = nThis;
      newNode.parent = null;
      newNode.augment();
      return newNode;
    }

    N node = nThis;
    int comparison;
    while (true) {
      comparison = comparator.compare(newNode, node);
      if (comparison < 0) {
        if (!node.left.isLeaf()) {
          node = node.left;
        } else {
          newNode.left = node.left;
          newNode.right = node.left;
          node.left = newNode;
          newNode.parent = node;
          break;
        }
      } else if (comparison > 0 || allowDuplicates) {
        if (!node.right.isLeaf()) {
          node = node.right;
        } else {
          newNode.left = node.right;
          newNode.right = node.right;
          node.right = newNode;
          newNode.parent = node;
          break;
        }
      } else {
        newNode.parent = null;
        return nThis;
      }
    }
    newNode.isRed = true;
    return newNode.fixInsertion();
  }

  public N remove() {
    if (isLeaf()) {
      throw new IllegalArgumentException("Attempted to remove a leaf node");
    }

    N node;
    if (parent != null) {
      node = parent;
    } else if (!left.isLeaf()) {
      node = left;
    } else if (!right.isLeaf()) {
      node = right;
    } else {
      return left;
    }

    removeWithoutGettingRoot();
    return node.root();
  }


  @SuppressWarnings("unchecked")
  protected boolean rotateLeft() {
    if (isLeaf() || right.isLeaf()) {
      throw new IllegalArgumentException("The node or its right child is a leaf");
    }
    N newParent = right;
    right = newParent.left;
    N nThis = (N) this;
    if (!right.isLeaf()) {
      right.parent = nThis;
    }
    newParent.parent = parent;
    parent = newParent;
    newParent.left = nThis;
    if (newParent.parent != null) {
      if (newParent.parent.left == this) {
        newParent.parent.left = newParent;
      } else {
        newParent.parent.right = newParent;
      }
    }
    augment();
    return newParent.augment();
  }

  @SuppressWarnings("unchecked")
  protected boolean rotateRight() {
    if (isLeaf() || left.isLeaf()) {
      throw new IllegalArgumentException("The node or its left child is a leaf");
    }
    N newParent = left;
    left = newParent.right;

    N nThis = (N) this;
    if (!left.isLeaf()) {
      left.parent = nThis;
    }
    newParent.parent = parent;
    parent = newParent;
    newParent.right = nThis;
    if (newParent.parent != null) {
      if (newParent.parent.left == this) {
        newParent.parent.left = newParent;
      } else {
        newParent.parent.right = newParent;
      }
    }
    augment();
    return newParent.augment();
  }

  protected void fixInsertionWithoutGettingRoot() {
    if (!isRed) {
      throw new IllegalArgumentException("The node must be red");
    }
    boolean changed = true;
    augment();

    RedBlackNode<N> node = this;
    while (node.parent != null && node.parent.isRed) {
      N parentNode = node.parent;
      N grandparent = parentNode.parent;
      if (grandparent.left.isRed && grandparent.right.isRed) {
        grandparent.left.isRed = false;
        grandparent.right.isRed = false;
        grandparent.isRed = true;

        if (changed) {
          changed = parentNode.augment();
          if (changed) {
            changed = grandparent.augment();
          }
        }
        node = grandparent;
      } else {
        if (parentNode.left == node) {
          if (grandparent.right == parentNode) {
            parentNode.rotateRight();
            node = parentNode;
            parentNode = node.parent;
          }
        } else if (grandparent.left == parentNode) {
          parentNode.rotateLeft();
          node = parentNode;
          parentNode = node.parent;
        }

        if (parentNode.left == node) {
          changed = grandparent.rotateRight();

        } else {
          changed = grandparent.rotateLeft();
        }

        parentNode.isRed = false;
        grandparent.isRed = true;
        node = parentNode;
        break;
      }
    }

    if (node.parent == null) {
      node.isRed = false;
    }
    if (changed) {
      for (node = node.parent; node != null; node = node.parent) {
        if (!node.augment()) {
          break;
        }
      }
    }
  }

  protected N fixInsertion() {
    fixInsertionWithoutGettingRoot();
    return root();
  }


  @SuppressWarnings("unchecked")
  private N swapWithSuccessor() {
    N replacement = successor();
    boolean oldReplacementIsRed = replacement.isRed;
    N oldReplacementLeft = replacement.left;
    N oldReplacementRight = replacement.right;
    N oldReplacementParent = replacement.parent;

    replacement.isRed = isRed;
    replacement.left = left;
    replacement.right = right;
    replacement.parent = parent;
    if (parent != null) {
      if (parent.left == this) {
        parent.left = replacement;
      } else {
        parent.right = replacement;
      }
    }

    N nThis = (N) this;
    isRed = oldReplacementIsRed;
    left = oldReplacementLeft;
    right = oldReplacementRight;
    if (oldReplacementParent == this) {
      parent = replacement;
      parent.right = nThis;
    } else {
      parent = oldReplacementParent;
      parent.left = nThis;
    }

    replacement.right.parent = replacement;
    if (!replacement.left.isLeaf()) {
      replacement.left.parent = replacement;
    }
    if (!right.isLeaf()) {
      right.parent = nThis;
    }
    return replacement;
  }

  private void fixSiblingDeletion() {
    RedBlackNode<N> sibling = this;
    boolean changed = true;
    boolean haveAugmentedParent = false;
    boolean haveAugmentedGrandparent = false;
    while (true) {
      N parentNode = sibling.parent;
      if (sibling.isRed) {
        parentNode.isRed = true;
        sibling.isRed = false;
        if (parentNode.left == sibling) {
          changed = parentNode.rotateRight();
          sibling = parentNode.left;
        } else {
          changed = parentNode.rotateLeft();
          sibling = parentNode.right;
        }
        haveAugmentedParent = true;
        haveAugmentedGrandparent = true;
      } else if (!sibling.left.isRed && !sibling.right.isRed) {
        sibling.isRed = true;
        if (parentNode.isRed) {
          parentNode.isRed = false;
          break;
        } else {
          if (changed && !haveAugmentedParent) {
            changed = parentNode.augment();
          }
          N grandparent = parentNode.parent;
          if (grandparent == null) {
            break;
          } else if (grandparent.left == parentNode) {
            sibling = grandparent.right;
          } else {
            sibling = grandparent.left;
          }
          haveAugmentedParent = haveAugmentedGrandparent;
          haveAugmentedGrandparent = false;
        }
      } else {
        if (sibling == parentNode.left) {
          if (!sibling.left.isRed) {
            sibling.rotateLeft();
            sibling = sibling.parent;
          }
        } else if (!sibling.right.isRed) {
          sibling.rotateRight();
          sibling = sibling.parent;
        }
        sibling.isRed = parentNode.isRed;
        parentNode.isRed = false;
        if (sibling == parentNode.left) {
          sibling.left.isRed = false;
          changed = parentNode.rotateRight();
        } else {
          sibling.right.isRed = false;
          changed = parentNode.rotateLeft();
        }
        haveAugmentedParent = haveAugmentedGrandparent;
        haveAugmentedGrandparent = false;
        break;
      }
    }

    // Update augmentation info
    N parentNode = sibling.parent;
    if (changed && parentNode != null) {
      if (!haveAugmentedParent) {
        changed = parentNode.augment();
      }
      if (changed && parentNode.parent != null) {
        parentNode = parentNode.parent;
        if (!haveAugmentedGrandparent) {
          changed = parentNode.augment();
        }
        if (changed) {
          for (parentNode = parentNode.parent; parentNode != null; parentNode = parentNode.parent) {
            if (!parentNode.augment()) {
              break;
            }
          }
        }
      }
    }
  }


  private void removeWithoutGettingRoot() {
    if (isLeaf()) {
      throw new IllegalArgumentException("Attempted to remove a leaf node");
    }
    N replacement;
    if (left.isLeaf() || right.isLeaf()) {
      replacement = null;
    } else {
      replacement = swapWithSuccessor();
    }

    N child;
    if (!left.isLeaf()) {
      child = left;
    } else if (!right.isLeaf()) {
      child = right;
    } else {
      child = null;
    }

    if (child != null) {

      child.parent = parent;
      if (parent != null) {
        if (parent.left == this) {
          parent.left = child;
        } else {
          parent.right = child;
        }
      }
      child.isRed = false;

      if (child.parent != null) {
        N parentNode;
        for (parentNode = child.parent; parentNode != null; parentNode = parentNode.parent) {
          if (!parentNode.augment()) {
            break;
          }
        }
      }
    } else if (parent != null) {

      N leaf = left;
      N parentNode = this.parent;
      N sibling;
      if (parentNode.left == this) {
        parentNode.left = leaf;
        sibling = parentNode.right;
      } else {
        parentNode.right = leaf;
        sibling = parentNode.left;
      }

      if (!isRed) {
        RedBlackNode<N> siblingNode = sibling;
        siblingNode.fixSiblingDeletion();
      } else {
        while (parentNode != null) {
          if (!parentNode.augment()) {
            break;
          }
          parentNode = parentNode.parent;
        }
      }
    }

    if (replacement != null) {
      replacement.augment();
      for (N parentNode = replacement.parent; parentNode != null; parentNode = parentNode.parent) {
        if (!parentNode.augment()) {
          break;
        }
      }
    }

    parent = null;
    left = null;
    right = null;
    isRed = true;
  }


  @Override
  public int compareTo(N other) {
    if (isLeaf() || other.isLeaf()) {
      throw new IllegalArgumentException("One of the nodes is a leaf node");
    }
    if (this == other) {
      return 0;
    }

    int depth = 0;
    RedBlackNode<N> parentNode;
    for (parentNode = this; parentNode.parent != null; parentNode = parentNode.parent) {
      depth++;
    }
    int otherDepth = 0;
    N otherParent;
    for (otherParent = other; otherParent.parent != null; otherParent = otherParent.parent) {
      otherDepth++;
    }

    // Go up to nodes of the same depth
    if (depth < otherDepth) {
      otherParent = other;
      for (int i = otherDepth - 1; i > depth; i--) {
        otherParent = otherParent.parent;
      }
      if (otherParent.parent != this) {
        otherParent = otherParent.parent;
      } else if (left == otherParent) {
        return 1;
      } else {
        return -1;
      }
      parentNode = this;
    } else if (depth > otherDepth) {
      parentNode = this;
      for (int i = depth - 1; i > otherDepth; i--) {
        parentNode = parentNode.parent;
      }
      if (parentNode.parent != other) {
        parentNode = parentNode.parent;
      } else if (other.left == parentNode) {
        return -1;
      } else {
        return 1;
      }
      otherParent = other;
    } else {
      parentNode = this;
      otherParent = other;
    }

    // Keep going up until we reach the lowest common ancestor
    while (parentNode.parent != otherParent.parent) {
      parentNode = parentNode.parent;
      otherParent = otherParent.parent;
    }
    if (parentNode.parent == null) {
      throw new IllegalArgumentException("The nodes do not belong to the same tree");
    }
    if (parentNode.parent.left == parentNode) {
      return -1;
    } else {
      return 1;
    }
  }


}
