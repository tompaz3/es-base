package com.tp.esbase.event.store;

import com.tp.esbase.event.AggregateId;
import com.tp.esbase.event.DomainEvent;

public record AggregateDomainEvent<ID extends AggregateId>(
    DomainEvent<ID> event,
    long revision
) {

}
