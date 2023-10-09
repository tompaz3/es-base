package com.tp.esbase.event.testdomain;

public record AccountNumber(
    Iban iban
) {

  public record Iban(String value) {

  }
}
