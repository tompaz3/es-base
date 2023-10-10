package com.tp.esbase.event.store;

import com.tp.esbase.event.AggregateId;
import com.tp.esbase.event.AggregateRoot;
import com.tp.esbase.event.AggregateRoot.AggregateRootInitialConstructor;
import com.tp.esbase.event.AggregateType;
import com.tp.esbase.event.Version;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AggregateRootRepository<P> {

  private final EventStore<P> eventStore;
  private final EventPublisher eventPublisher;

  public <ID extends AggregateId, A extends AggregateRoot<ID>> A findById(
      AggregateType type,
      ID id,
      AggregateRootInitialConstructor<A> constructor
  ) {
    var events = eventStore.findEventsForAggregate(type, id);
    var lastEvent = events.get(events.size() - 1);
    return AggregateRoot.restore(
        events.stream().map(AggregateDomainEvent::event).toList(),
        Version.specified(lastEvent.revision()),
        constructor
    );
  }

  public <ID extends AggregateId, A extends AggregateRoot<ID>> void save(A aggregateRoot) {
    eventStore.saveEvents(aggregateRoot);
    eventPublisher.publish(aggregateRoot.getAndClearOutEvents());
  }
}
