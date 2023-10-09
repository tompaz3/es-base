package com.tp.esbase.event.testutil;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FluentComparableTest {

  @CsvSource(delimiter = '|', nullValues = "null", value = {
//    " first | other | expected ",
      " 1     | 1     | false    ",
      " 1     | 2     | false    ",
      " 1     | 0     | true     ",
  })
  @ParameterizedTest(name = "[{index}] value = {0} other = {1} expected = {2}")
  void should_verify_greater_than(int first, int other, boolean expected) {
    // given
    var firstComparable = new IntFluentComparable(first);
    var otherComparable = new IntFluentComparable(other);

    // when
    var result = firstComparable.isGreaterThan(otherComparable);

    // then
    assertThat(result).isEqualTo(expected);
  }

  @CsvSource(delimiter = '|', nullValues = "null", value = {
//    " first | other | expected ",
      " 1     | 1     | true     ",
      " 1     | 2     | false    ",
      " 1     | 0     | false    ",
  })
  @ParameterizedTest(name = "[{index}] value = {0} other = {1} expected = {2}")
  void should_verify_equal_to(int first, int other, boolean expected) {
    // given
    var firstComparable = new IntFluentComparable(first);
    var otherComparable = new IntFluentComparable(other);

    // when
    var result = firstComparable.isEqualTo(otherComparable);

    // then
    assertThat(result).isEqualTo(expected);
  }

  @CsvSource(delimiter = '|', nullValues = "null", value = {
//    " first | other | expected ",
      " 1     | 1     | false    ",
      " 1     | 2     | true     ",
      " 1     | 0     | false    ",
  })
  @ParameterizedTest(name = "[{index}] value = {0} other = {1} expected = {2}")
  void should_verify_lower_than(int first, int other, boolean expected) {
    // given
    var firstComparable = new IntFluentComparable(first);
    var otherComparable = new IntFluentComparable(other);

    // when
    var result = firstComparable.isLowerThan(otherComparable);

    // then
    assertThat(result).isEqualTo(expected);
  }

  @CsvSource(delimiter = '|', nullValues = "null", value = {
//    " first | other | expected ",
      " 1     | 1     | true     ",
      " 1     | 2     | false    ",
      " 1     | 0     | true     ",
  })
  @ParameterizedTest(name = "[{index}] value = {0} other = {1} expected = {2}")
  void should_verify_greater_than_or_equal_to(int first, int other, boolean expected) {
    // given
    var firstComparable = new IntFluentComparable(first);
    var otherComparable = new IntFluentComparable(other);

    // when
    var result = firstComparable.isGreaterOrEqualTo(otherComparable);

    // then
    assertThat(result).isEqualTo(expected);
  }

  @CsvSource(delimiter = '|', nullValues = "null", value = {
//    " first | other | expected ",
      " 1     | 1     | true     ",
      " 1     | 2     | true     ",
      " 1     | 0     | false    ",
  })
  @ParameterizedTest(name = "[{index}] value = {0} other = {1} expected = {2}")
  void should_verify_lower_than_or_equal_to(int first, int other, boolean expected) {
    // given
    var firstComparable = new IntFluentComparable(first);
    var otherComparable = new IntFluentComparable(other);

    // when
    var result = firstComparable.isLowerOrEqualTo(otherComparable);

    // then
    assertThat(result).isEqualTo(expected);
  }

  private record IntFluentComparable(int value) implements FluentComparable<IntFluentComparable> {

    @Override
    public int compareTo(IntFluentComparable other) {
      return Integer.compare(value, other.value());
    }
  }
}