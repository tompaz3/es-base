package com.tp.esbase.event.testdomain;

import java.math.BigDecimal;

public record AccountBalance(
    Amount amount
) {

  public AccountBalance add(BigDecimal value) {
    return new AccountBalance(this.amount().add(value));
  }

  public AccountBalance subtract(BigDecimal value) {
    return new AccountBalance(this.amount().subtract(value));
  }

  public boolean isLowerThan(Amount other) {
    return this.amount().isLowerThan(other);
  }
}
