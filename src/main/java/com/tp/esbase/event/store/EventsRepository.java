package com.tp.esbase.event.store;

import com.tp.esbase.event.AggregateId;
import com.tp.esbase.event.AggregateType;
import java.util.List;

public interface EventsRepository<P> {

  <ID extends AggregateId> List<SerializedEvent<P>> findById(AggregateType aggregateType, ID id);

  void save(List<SerializedEvent<P>> events);
}
