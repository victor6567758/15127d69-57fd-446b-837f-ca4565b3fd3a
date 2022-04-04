package com.alvaria.test.pqueue.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ReadView<T, I> implements Iterable<T> {

  private final Map<I, Integer> keys = new HashMap<>();
  private final List<T> data = new ArrayList<>();

  public ReadView(Iterable<T> initData, Function<T, I> idSupplier) {
    initData.forEach(elem -> {
      data.add(elem);
      keys.put(idSupplier.apply(elem), data.size() - 1);
    });
  }

  @Override
  public Iterator<T> iterator() {
    return data.iterator();
  }

  public int getPosition(long id) {
    return keys.getOrDefault(id, -1);
  }


}
