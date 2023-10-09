package com.tp.esbase.event.testdomain;

import com.tp.esbase.event.testutil.FluentComparable;
import java.math.BigDecimal;

public record Amount(
    Currency currency,
    BigDecimal value
) implements FluentComparable<Amount> {

  @Override
  public int compareTo(Amount other) {
    return this.value().compareTo(other.value());
  }

  public Amount add(BigDecimal value) {
    return new Amount(currency, this.value().add(value));
  }

  public Amount subtract(BigDecimal value) {
    return new Amount(currency, this.value().subtract(value));
  }
}
