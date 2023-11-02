package com.tp.esbase.event;

/**
 * Marker interface for Projection Executor.
 *
 * @param <R> the type of the result of the projection event handling.
 */
public interface ProjectionExecutor<R> {

  R execute(DomainEvent<?> event);

  interface NoResultProjectionExecutor extends ProjectionExecutor<Void> {

    @Override
    default Void execute(DomainEvent<?> event) {
      handleEvent(event);
      return null;
    }

    void handleEvent(DomainEvent<?> event);
  }
}
