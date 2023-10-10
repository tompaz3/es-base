package com.tp.esbase.event.store;

import com.tp.esbase.event.AggregateId;

public interface EventDeserializer<P> {

  <ID extends AggregateId> AggregateDomainEvent<ID> deserialize(SerializedEvent<P> event);
}
