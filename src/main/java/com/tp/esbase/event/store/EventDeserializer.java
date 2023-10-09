package com.tp.esbase.event.store;

import com.tp.esbase.event.AggregateId;

public interface EventDeserializer {

  <ID extends AggregateId, P> AggregateDomainEvent<ID> deserialize(SerializedEvent<P> event);
}
