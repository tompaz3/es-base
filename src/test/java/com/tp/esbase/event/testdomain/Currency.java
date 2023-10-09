package com.tp.esbase.event.testdomain;

import static lombok.AccessLevel.PRIVATE;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;

@Value
@Accessors(fluent = true)
@RequiredArgsConstructor(access = PRIVATE)
public final class Currency {

  private final String name;

  public static Currency of(String name) {
    return new Currency(name.toLowerCase());
  }
}
