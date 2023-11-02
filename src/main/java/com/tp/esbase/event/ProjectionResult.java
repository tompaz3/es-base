package com.tp.esbase.event;

import static lombok.AccessLevel.PRIVATE;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Basic implementation for a projection result.
 */
public interface ProjectionResult {

  static ProjectionResultSuccess success() {
    return ProjectionResultSuccess.INSTANCE;
  }

  static ProjectionResultFailure failure(Throwable cause, String message) {
    return new ProjectionResultFailure(cause, message);
  }

  @Accessors(fluent = true)
  @Getter
  @RequiredArgsConstructor(access = PRIVATE)
  final class ProjectionResultFailure implements ProjectionResult {

    private final Throwable cause;
    private final String message;
  }

  @NoArgsConstructor(access = PRIVATE)
  final class ProjectionResultSuccess implements ProjectionResult {

    private static final ProjectionResultSuccess INSTANCE = new ProjectionResultSuccess();
  }

  /**
   * Basic implementation for a projection executor result.
   */
  record ProjectionExecutorResult(List<ProjectionResult> results) {

  }
}
