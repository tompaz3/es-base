package com.tp.esbase.event;

import com.tp.esbase.event.DomainEvent.DomainEventHeader;
import com.tp.esbase.event.DomainEvent.EventId;
import com.tp.esbase.event.DomainEvent.EventTimestamp;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public abstract class AggregateRoot<ID extends AggregateId> {

  @Getter
  protected ID id;
  @Getter
  protected final AggregateType type;
  @Getter
  protected Version version;
  protected EventId latestEvent;
  protected List<DomainEvent<ID>> inEvents = new ArrayList<>();
  protected List<DomainEvent<ID>> outEvents = new ArrayList<>();

  protected AggregateRoot(
      AggregateType type,
      Version version,
      EventId latestEvent
  ) {
    this.type = type;
    this.version = version;
    this.latestEvent = latestEvent;
  }

  protected void handleAndRegisterEvent(DomainEvent<ID> event) {
    this.inEvents.add(event);
    handle(event);
  }

  protected DomainEventHeader<ID> nextDomainEventHeader() {
    return nextDomainEventHeader(this.id);
  }

  protected DomainEventHeader<ID> nextDomainEventHeader(ID id) {
    this.latestEvent = this.latestEvent.next();
    return new DomainEventHeader<>(latestEvent, id, EventTimestamp.now());
  }

  protected void registerOutEvent(DomainEvent<ID> event) {
    this.outEvents.add(event);
  }

  protected abstract void handle(DomainEvent<ID> event);

  public List<DomainEvent<ID>> getAndClearInEvents() {
    var events = inEvents;
    this.inEvents = new ArrayList<>();
    return events;
  }

  public List<DomainEvent<ID>> getAndClearOutEvents() {
    var events = outEvents;
    this.outEvents = new ArrayList<>();
    return events;
  }

  public static <ID extends AggregateId, A extends AggregateRoot<ID>> A restore(
      List<DomainEvent<ID>> inEvents,
      Version version,
      AggregateRootInitialConstructor<A> constructor
  ) {
    var lastEvent = inEvents.get(inEvents.size() - 1);
    var lastEventId = lastEvent.header().id();
    var aggregate = constructor.newInstance(version, lastEventId);
    // handle events, skip storing as in events
    inEvents.forEach(aggregate::handle);
    // do not produce out events when restoring aggregate
    aggregate.outEvents.clear();
    return aggregate;
  }

  public interface AggregateRootInitialConstructor<A> {

    A newInstance(Version version, EventId latestEvent);
  }
}
