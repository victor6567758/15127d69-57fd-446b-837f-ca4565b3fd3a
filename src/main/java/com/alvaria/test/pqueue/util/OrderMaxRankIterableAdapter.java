package com.alvaria.test.pqueue.util;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OrderMaxRankIterableAdapter<T> implements Iterable<T> {

  private class OrderMaxRankIterableAdapterIterator implements Iterator<T> {

    private final List<Iterator<T>> source =
        new ArrayList<>(OrderMaxRankIterableAdapter.this.sourceIterators);

    private final List<T> curRowValues = new ArrayList<>(source.size());
    private final BitSet liveIteratorsBitSet = new BitSet(source.size());


    private int validIteratorsCnt = source.size();
    private int curRowElemsRemain;


    @Override
    public boolean hasNext() {
      if (validIteratorsCnt == 0) {
        return false;
      }
      return true;
    }

    @Override
    public T next() {
      if (validIteratorsCnt == 0) {
        throw new NoSuchElementException();
      }

      if (curRowElemsRemain == 0) {
        readTopRow();
      }

      return getCurrentRowNextMaxElem();
    }

    private void readTopRow() {
      Collections.fill(curRowValues, null);
      for (int i = 0; i < source.size(); i++) {
        if (!liveIteratorsBitSet.get(i) && source.get(i).hasNext()) {
          curRowValues.set(i, source.get(i).next());
          curRowElemsRemain++;
        } else {
          liveIteratorsBitSet.set(i);
          validIteratorsCnt--;
        }
      }
    }

    private T getCurrentRowNextMaxElem() {
      T maxElem = null;
      for (T elem : curRowValues) {
        if (elem != null) {

          if (maxElem == null) {
            maxElem = elem;
            continue;
          }

          if (OrderMaxRankIterableAdapter.this.comparator.compare(maxElem, elem) > 0) {
            maxElem = elem;
          }
        }

      }
      if (maxElem != null) {
        curRowElemsRemain--;
      }
      return maxElem;
    }
  }

  private final Comparator<T> comparator;
  private final List<Iterator<T>> sourceIterators;

  @Override
  public Iterator<T> iterator() {
    return new OrderMaxRankIterableAdapterIterator();
  }
}
