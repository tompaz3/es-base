package com.tp.esbase.event.store;

import com.tp.esbase.event.AggregateId;
import com.tp.esbase.event.DomainEvent;

public interface EventSerializer<P> {

  <ID extends AggregateId, E extends DomainEvent<ID>> SerializedEvent<P> serialize(E event);
}
