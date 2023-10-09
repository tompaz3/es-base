package com.tp.esbase.event;

import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;

public sealed interface Version {

  Long value();

  default boolean isInitial() {
    return InitialVersion.INSTANCE.equals(this);
  }

  static Version initial() {
    return InitialVersion.INSTANCE;
  }

  static Version specified(Long value) {
    return new SpecifiedVersion(value);
  }

  @NoArgsConstructor(access = PRIVATE)
  final class InitialVersion implements Version {

    private static final InitialVersion INSTANCE = new InitialVersion();

    @Override
    public Long value() {
      return 0L;
    }
  }

  record SpecifiedVersion(Long value) implements Version {

  }
}
