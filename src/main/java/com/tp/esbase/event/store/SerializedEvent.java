package com.tp.esbase.event.store;

public record SerializedEvent<P>(
    Long id,
    String eventType,
    String aggregateId,
    P payload
) {

}
