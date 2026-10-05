package com.grechanikovars.eventbus.exception;

import java.util.Collections;
import java.util.List;

/**
 * Thrown from {@code EventBus.post(Object)} once every matching subscriber has been given a
 * chance to handle the event. Aggregates the failures of all subscribers that threw during that
 * single dispatch, rather than aborting delivery after the first failure or silently swallowing
 * exceptions.
 */
public class EventDispatchException extends RuntimeException {

  private final List<EventBusException> failures;

  public EventDispatchException(String message, List<EventBusException> failures) {
    super(message);
    this.failures = Collections.unmodifiableList(failures);
  }

  public List<EventBusException> getFailures() {
    return failures;
  }
}
