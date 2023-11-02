package com.tp.esbase.event.testdomain;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toUnmodifiableList;

import com.tp.esbase.event.DomainEvent;
import com.tp.esbase.event.Projection;
import com.tp.esbase.event.ProjectionExecutor;
import com.tp.esbase.event.ProjectionResult;
import com.tp.esbase.event.ProjectionResult.ProjectionExecutorResult;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleProjectionExecutor implements ProjectionExecutor<ProjectionExecutorResult> {

  private final Map<Class<? extends DomainEvent<?>>, List<Projection<ProjectionResult>>> projectionsByEvent;

  public SimpleProjectionExecutor(List<Projection<ProjectionResult>> projections) {
    this.projectionsByEvent = projections.stream()
        .flatMap(projection ->
            projection.supportedEvents().stream()
                .map(event -> Map.entry(event, projection))
        )
        .peek(
            projection -> log.info("Registering projection handler for type: {}", projection.getKey().getSimpleName()))
        .collect(groupingBy(
            Map.Entry::getKey,
            mapping(Map.Entry::getValue, toUnmodifiableList())
        ));
    if (this.projectionsByEvent.isEmpty()) {
      log.warn("No projection handlers defined");
    }
  }

  @Override
  public ProjectionExecutorResult execute(DomainEvent<?> event) {
    var projections = projectionsByEvent.get(event.getClass());
    var eventType = event.getClass().getSimpleName();
    if (isNull(projections)) {
      log.warn("Missing projections for event {}", eventType);
      return null;
    }
    return projections.stream()
        .map(projection -> projection.handle(event))
        .collect(collectingAndThen(toUnmodifiableList(), ProjectionExecutorResult::new));
  }
}
