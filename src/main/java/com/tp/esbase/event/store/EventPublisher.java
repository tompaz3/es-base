package com.tp.esbase.event.store;

import com.tp.esbase.event.AggregateId;
import com.tp.esbase.event.DomainEvent;
import java.util.List;

public interface EventPublisher {

  <ID extends AggregateId> void publish(List<DomainEvent<ID>> events);
}
