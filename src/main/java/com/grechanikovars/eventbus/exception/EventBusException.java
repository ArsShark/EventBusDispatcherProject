package com.grechanikovars.eventbus.exception;

/**
 * Thrown when a single {@code @Subscribe} method is declared incorrectly or cannot be invoked
 * reflectively.
 */
public class EventBusException extends RuntimeException {

  public EventBusException(String message) {
    super(message);
  }

  public EventBusException(String message, Throwable cause) {
    super(message, cause);
  }
}
