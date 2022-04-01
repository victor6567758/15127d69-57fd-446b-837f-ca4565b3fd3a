package com.alvaria.test.pqueue.util;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.util.CollectionUtils;

public class MaxIterableAdapter<T> implements Iterable<T> {

  private class MaxIterableAdapterIterator implements Iterator<T> {

    private final List<Iterator<T>> source =
        new ArrayList<>(MaxIterableAdapter.this.sourceIterators);

    private final List<T> curRowValues;
    private final BitSet liveIteratorsBitSet = new BitSet(source.size());

    private T nextVal;


    private MaxIterableAdapterIterator() {
      curRowValues = new ArrayList<>(Collections.nCopies(source.size(), null));
      liveIteratorsBitSet.set(0, source.size(), true);
    }

    @Override
    public boolean hasNext() {

      if (nextVal == null) {
        nextVal = getNextMaxElem();
      }

      return nextVal != null;
    }

    @Override
    public T next() {
      if (nextVal == null) {
        throw new NoSuchElementException();
      }
      T tmp = nextVal;
      nextVal = null;

      return tmp;
    }


    private T readFromIterator(int idx) {
      if (liveIteratorsBitSet.isEmpty()) {
        return null;
      }
      if (liveIteratorsBitSet.get(idx) && source.get(idx).hasNext()) {
        return source.get(idx).next();
      } else {
        liveIteratorsBitSet.set(idx, false);
        return null;
      }
    }

    private T getNextMaxElem() {
      T maxElem = null;
      int maxElemIdx = -1;

      for (int i = 0; i < curRowValues.size(); i++) {
        T elem = curRowValues.get(i);
        if (elem == null) {
          elem = readFromIterator(i);
          if (elem != null) {
            curRowValues.set(i, elem);
          }
        }

        if (elem != null) {

          if (maxElem == null) {
            maxElem = elem;
            maxElemIdx = i;
            continue;
          }

          if (MaxIterableAdapter.this.comparator.compare(elem, maxElem) > 0) {
            maxElem = elem;
            maxElemIdx = i;
          }
        }
      }

      if (maxElem != null) {
        curRowValues.set(maxElemIdx, readFromIterator(maxElemIdx));
      }
      return maxElem;
    }
  }

  private final Comparator<T> comparator;
  private final List<Iterator<T>> sourceIterators;

  public MaxIterableAdapter(Comparator<T> comparator, List<Iterator<T>> sourceIterators) {
    if (comparator == null) {
      throw new IllegalArgumentException("Comparator must be provided");
    }
    this.comparator = comparator;

    if (CollectionUtils.isEmpty(sourceIterators)) {
      throw new IllegalArgumentException("Invalid source iterators");
    }
    this.sourceIterators = sourceIterators;
  }

  @Override
  public Iterator<T> iterator() {
    return new MaxIterableAdapterIterator();
  }
}
