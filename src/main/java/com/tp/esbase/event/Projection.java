package com.tp.esbase.event;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface Projection {

  List<Class<? extends DomainEvent<?>>> supportedEvents();

  CompletableFuture<Void> handle(DomainEvent<?> event);
}
