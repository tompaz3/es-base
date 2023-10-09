package com.tp.esbase.event.testdomain;

public record Block(
    BlockId id,
    Amount amount
) {

  public record BlockId(String value) {

  }
}
