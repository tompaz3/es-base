package com.tp.esbase.event.store;

import com.tp.esbase.event.AggregateId;
import com.tp.esbase.event.DomainEvent;

public interface EventSerializer {

  <ID extends AggregateId, E extends DomainEvent<ID>, P> SerializedEvent<P> serialize(E event);
}
