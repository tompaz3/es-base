package com.tp.esbase.event;

import java.util.concurrent.CompletableFuture;

public interface ProjectionExecutor {

  CompletableFuture<Void> execute(DomainEvent<?> event);
}
