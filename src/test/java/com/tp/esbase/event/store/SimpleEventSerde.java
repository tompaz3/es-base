package com.tp.esbase.event.store;

import com.tp.esbase.event.AggregateId;
import com.tp.esbase.event.DomainEvent;

class SimpleEventSerde implements EventSerializer<Object>, EventDeserializer<Object> {

  @Override
  public <ID extends AggregateId, E extends DomainEvent<ID>> SerializedEvent<Object> serialize(E event) {
    return new SerializedEvent<>(
        event.header().id().value(),
        event.getClass().getSimpleName(),
        event.header().aggregateId().value(),
        event
    );
  }

  @Override
  public <ID extends AggregateId> AggregateDomainEvent<ID> deserialize(SerializedEvent<Object> event) {
    @SuppressWarnings("unchecked")
    var domainEvent = (DomainEvent<ID>) event.payload();
    return new AggregateDomainEvent<>(
        domainEvent,
        event.id()
    );
  }
}
