package com.tp.esbase.event.store;

import com.tp.esbase.event.AggregateId;
import com.tp.esbase.event.AggregateType;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class InMemoryEventsRepository implements EventsRepository<Object> {

  private final Map<String, List<SerializedEvent<?>>> store = new HashMap<>();

  @Override
  public <ID extends AggregateId> List<SerializedEvent<Object>> findById(
      AggregateType aggregateType,
      ID id
  ) {
    return store.get(id.value()).stream()
        .map(event -> {
          @SuppressWarnings("unchecked")
          var typedEvent = (SerializedEvent<Object>) event;
          return typedEvent;
        })
        .toList();
  }

  @Override
  public void save(List<SerializedEvent<Object>> serializedEvents) {
    serializedEvents.forEach(event ->
        store.merge(event.aggregateId(), newList(event), (oldValue, newValue) -> {
          oldValue.addAll(newValue);
          return oldValue;
        })
    );
  }

  private List<SerializedEvent<?>> newList(SerializedEvent<?> event) {
    var list = new LinkedList<SerializedEvent<?>>();
    list.add(event);
    return list;
  }
}
