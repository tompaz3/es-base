package com.tp.esbase.event;

import java.time.Instant;

public interface DomainEvent<ID extends AggregateId> {

  DomainEventHeader<ID> header();

  record DomainEventHeader<ID extends AggregateId>(
      EventId id,
      ID aggregateId,
      EventTimestamp timestamp
  ) {

    public static <ID extends AggregateId> DomainEventHeader<ID> initial(ID id) {
      return new DomainEventHeader<>(
          EventId.initial(),
          id,
          EventTimestamp.now()
      );
    }
  }

  record EventId(Long value) {

    private static final EventId EMPTY = new EventId(0L);

    public EventId next() {
      return new EventId(value + 1);
    }

    public static EventId initial() {
      return EMPTY;
    }
  }

  record EventTimestamp(Instant value) {

    public static EventTimestamp now() {
      return new EventTimestamp(Instant.now());
    }
  }
}
