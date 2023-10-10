package com.tp.esbase.event.store;

import com.tp.esbase.event.AggregateId;
import com.tp.esbase.event.DomainEvent;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
class InMemoryListEventPublisher implements EventPublisher {
  
  private final List<DomainEvent<?>> eventsStore = new LinkedList<>();

  @Override
  public <ID extends AggregateId> void publish(List<DomainEvent<ID>> domainEvents) {
    eventsStore.addAll(domainEvents);
  }
}
