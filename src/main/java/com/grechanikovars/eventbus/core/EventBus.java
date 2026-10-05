package com.grechanikovars.eventbus.core;

/**
 * A publish-subscribe event dispatcher. Listeners register objects that declare one or more
 * {@code @Subscribe} methods; posted events are delivered to every registered method whose
 * declared parameter type matches the event's class, one of its superclasses, or one of its
 * interfaces.
 */
public interface EventBus {

  /**
   * Scans {@code listener} for {@code @Subscribe} methods and registers each of them to receive
   * matching events.
   */
  void register(Object listener);

  /**
   * Removes every {@code @Subscribe} method previously registered for this exact listener
   * instance.
   */
  void unregister(Object listener);

  /**
   * Delivers {@code event} to every subscriber whose declared parameter type matches
   * {@code event}'s class or any of its superclasses/interfaces.
   *
   * @throws com.grechanikovars.eventbus.exception.EventDispatchException if one or more
   *     subscribers threw while handling the event; all matching subscribers are still given a
   *     chance to run before this is thrown
   */
  void post(Object event);
}
