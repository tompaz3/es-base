package com.tp.esbase.event.defaults;

import static lombok.AccessLevel.PROTECTED;

import com.tp.esbase.event.AggregateId;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor(access = PROTECTED)
public abstract class AggregateUuidId implements AggregateId {

  @ToString.Include
  @EqualsAndHashCode.Include
  protected final UUID id;

  @Override
  public String value() {
    return id.toString();
  }
}
