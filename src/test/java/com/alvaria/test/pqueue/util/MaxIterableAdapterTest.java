package com.alvaria.test.pqueue.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class MaxIterableAdapterTest {

  @Test
  void vanillaTest() {

    List<Integer> list1 = Arrays.asList(1, 8, 12, 25, 40);
    List<Integer> list2 = Arrays.asList(5, 6, 7);
    List<Integer> list3 = Arrays.asList(100, 200);

    MaxIterableAdapter<Integer> adapter = new MaxIterableAdapter<>(Comparator.reverseOrder(),
        Arrays.asList(list1.iterator(), list2.iterator(), list3.iterator()));

    List<Integer> result = StreamSupport.stream(adapter.spliterator(), false).collect(Collectors.toList());
    assertThat(result).containsExactly(1, 5, 6, 7, 8, 12, 25, 40, 100, 200);
  }

  @Test
  void emptyListTest() {

    List<Integer> list1 = Arrays.asList(1, 8, 12, 25, 40);
    List<Integer> list2 = Collections.emptyList();
    List<Integer> list3 = Arrays.asList(100, 200);

    MaxIterableAdapter<Integer> adapter = new MaxIterableAdapter<>(Comparator.reverseOrder(),
        Arrays.asList(list1.iterator(), list2.iterator(), list3.iterator()));

    List<Integer> result = StreamSupport.stream(adapter.spliterator(), false).collect(Collectors.toList());
    assertThat(result).containsExactly(1, 8, 12, 25, 40, 100, 200);
  }

  @Test
  void emptyAllListTest() {
    List<Integer> list1 = Collections.emptyList();
    List<Integer> list2 = Collections.emptyList();

    MaxIterableAdapter<Integer> adapter = new MaxIterableAdapter<>(Comparator.reverseOrder(),
        Arrays.asList(list1.iterator(), list2.iterator()));

    List<Integer> result = StreamSupport.stream(adapter.spliterator(), false).collect(Collectors.toList());
    assertThat(result).isEmpty();
  }

  @Test
  void repeatedValuesTest() {

    List<Integer> list1 = Arrays.asList(1, 8, 8, 8, 40);
    List<Integer> list2 = Arrays.asList(8, 40, 40);
    List<Integer> list3 = Arrays.asList(100, 200);

    MaxIterableAdapter<Integer> adapter = new MaxIterableAdapter<>(Comparator.reverseOrder(),
        Arrays.asList(list1.iterator(), list2.iterator(), list3.iterator()));

    List<Integer> result = StreamSupport.stream(adapter.spliterator(), false).collect(Collectors.toList());
    assertThat(result).containsExactly(1, 8, 8, 8, 8, 40, 40, 40, 100, 200);
  }

  @Test
  void invalidInputParametersTest() {


    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      new MaxIterableAdapter<Integer>(Comparator.reverseOrder(), null);
    });

    List<Integer> list1 = Arrays.asList(1, 8, 8, 8, 40);
    List<Integer> list2 = Arrays.asList(8, 40, 40);
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      new MaxIterableAdapter<>(null, Arrays.asList(list1.iterator(), list2.iterator()));
    });


  }
}