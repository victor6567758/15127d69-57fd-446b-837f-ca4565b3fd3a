package com.alvaria.test.pqueue.util;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import org.springframework.util.CollectionUtils;

public class MaxIterableAdapter<T> implements Iterable<T> {

  private class MaxIterableAdapterIterator implements Iterator<T> {


    private final List<T> curRowValues;
    private final BitSet liveIteratorsBitSet = new BitSet(MaxIterableAdapter.this.sourceIterators.size());

    private T nextVal;


    private MaxIterableAdapterIterator() {
      curRowValues = new ArrayList<>(Collections.nCopies(MaxIterableAdapter.this.sourceIterators.size(), null));
      liveIteratorsBitSet.set(0, MaxIterableAdapter.this.sourceIterators.size(), true);
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
      if (liveIteratorsBitSet.get(idx) && MaxIterableAdapter.this.sourceIterators.get(idx).hasNext()) {
        return MaxIterableAdapter.this.sourceIterators.get(idx).next();
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

          if (MaxIterableAdapter.this.priorityProviders.get(i).apply(elem) >
              MaxIterableAdapter.this.priorityProviders.get(maxElemIdx).apply(maxElem)) {
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


  private final List<Iterator<T>> sourceIterators;
  private final List<Function<T, Double>> priorityProviders;

  public MaxIterableAdapter(List<Function<T, Double>> priorityProviders, List<Iterator<T>> sourceIterators) {
    if (priorityProviders == null) {
      throw new IllegalArgumentException("Priority supplier must be provided");
    }
    this.priorityProviders = priorityProviders;

    if (CollectionUtils.isEmpty(sourceIterators)) {
      throw new IllegalArgumentException("Invalid source iterators");
    }
    this.sourceIterators = sourceIterators;

    if (sourceIterators.size() != priorityProviders.size()) {
      throw new IllegalArgumentException("Source iterators and priority providers must be of the same size");
    }
  }

  @Override
  public Iterator<T> iterator() {
    return new MaxIterableAdapterIterator();
  }
}
