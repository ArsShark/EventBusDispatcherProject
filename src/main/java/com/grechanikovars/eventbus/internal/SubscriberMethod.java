package com.grechanikovars.eventbus.internal;

import com.grechanikovars.eventbus.exception.EventBusException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Immutable pairing of a listener instance, one of its {@code @Subscribe} methods, and the event
 * type declared by that method's single parameter. Holds everything required to deliver a
 * posted event to this specific handler.
 */
public record SubscriberMethod(Object listener, Method method, Class<?> eventType) {

  /**
   * Invokes the underlying method on the listener with the given event.
   *
   * @throws EventBusException if the method cannot be accessed or throws while executing
   */
  public void invoke(Object event) {
    try {
      method.invoke(listener, event);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new EventBusException(
          String.format("Failed to invoke subscriber method '%s' on listener '%s' for event '%s'",
              method.getName(), listener.getClass().getName(), event.getClass().getName()),
          e);
    }
  }
}
