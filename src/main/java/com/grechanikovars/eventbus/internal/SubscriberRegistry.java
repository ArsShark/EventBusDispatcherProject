package com.grechanikovars.eventbus.internal;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Live, thread-safe storage of subscribers keyed by the exact event type they were registered
 * for. {@code post()} reads these buckets far more often than {@code register()}/
 * {@code unregister()} write to them, so each bucket is a {@link CopyOnWriteArrayList}.
 */
public class SubscriberRegistry {

  private final ConcurrentHashMap<Class<?>, CopyOnWriteArrayList<SubscriberMethod>> subscribersByType =
      new ConcurrentHashMap<>();

  public void addSubscriber(Class<?> eventType, SubscriberMethod subscriberMethod) {
    subscribersByType.computeIfAbsent(eventType, type -> new CopyOnWriteArrayList<>())
        .add(subscriberMethod);
  }

  public void removeSubscriber(Class<?> eventType, Object listener) {
    CopyOnWriteArrayList<SubscriberMethod> subscribers = subscribersByType.get(eventType);
    if (subscribers == null) {
      return;
    }
    subscribers.removeIf(subscriberMethod -> subscriberMethod.listener() == listener);
  }

  public List<SubscriberMethod> getSubscribers(Class<?> eventType) {
    CopyOnWriteArrayList<SubscriberMethod> subscribers = subscribersByType.get(eventType);
    return subscribers == null ? List.of() : subscribers;
  }
}
