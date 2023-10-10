package com.tp.esbase.event.store;

import com.tp.esbase.event.AggregateId;
import com.tp.esbase.event.AggregateRoot;
import com.tp.esbase.event.AggregateType;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EventStore<P> {

  private final EventSerializer<P> eventSerializer;
  private final EventsRepository<P> eventsRepository;
  private final EventDeserializer<P> eventDeserializer;

  public <ID extends AggregateId> List<AggregateDomainEvent<ID>> findEventsForAggregate(
      AggregateType type,
      ID id
  ) {
    return eventsRepository.findById(type, id).stream()
        .<AggregateDomainEvent<ID>>map(eventDeserializer::deserialize)
        .toList();
  }

  public <ID extends AggregateId, A extends AggregateRoot<ID>> void saveEvents(A aggregateRoot) {
    var events = aggregateRoot.getAndClearInEvents().stream()
        .map(eventSerializer::serialize)
        .toList();
    eventsRepository.save(events);
  }
}
