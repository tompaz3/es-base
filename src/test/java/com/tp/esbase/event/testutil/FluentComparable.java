package com.tp.esbase.event.testutil;

public interface FluentComparable<T> extends Comparable<T> {

  default boolean isGreaterThan(T other) {
    return compareTo(other) > 0;
  }

  default boolean isEqualTo(T other) {
    return compareTo(other) == 0;
  }

  default boolean isLowerThan(T other) {
    return compareTo(other) < 0;
  }

  default boolean isGreaterOrEqualTo(T other) {
    return compareTo(other) >= 0;
  }

  default boolean isLowerOrEqualTo(T other) {
    return compareTo(other) <= 0;
  }
}
