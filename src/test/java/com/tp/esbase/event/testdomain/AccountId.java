package com.tp.esbase.event.testdomain;

import com.tp.esbase.event.defaults.AggregateUuidId;
import java.util.UUID;

public final class AccountId extends AggregateUuidId {

  public AccountId(UUID id) {
    super(id);
  }
}
