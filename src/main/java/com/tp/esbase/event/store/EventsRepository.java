package com.tp.esbase.event.store;

import com.tp.esbase.event.AggregateId;
import com.tp.esbase.event.AggregateType;
import java.util.List;

public interface EventsRepository {

  <ID extends AggregateId, P> List<SerializedEvent<P>> findById(AggregateType aggregateType, ID id);

  <P> void save(List<SerializedEvent<P>> events);
}
