package com.tp.esbase.event;

/**
 * Marker interface for Projection Executor.
 *
 * @param <R> the type of the result of the projection event handling.
 */
public interface ProjectionExecutor<R> {

  R execute(DomainEvent<?> event);
}
