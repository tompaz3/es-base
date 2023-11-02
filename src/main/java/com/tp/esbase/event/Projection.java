package com.tp.esbase.event;

import java.util.List;

/**
 * Marker interface for a projection.
 *
 * @param <R> the type of the result of the projection event handling.
 */
public interface Projection<R> {

  List<Class<? extends DomainEvent<?>>> supportedEvents();

  R handle(DomainEvent<?> event);


}
