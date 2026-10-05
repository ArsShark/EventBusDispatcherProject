package com.grechanikovars.eventbus.core;

import com.grechanikovars.eventbus.exception.EventBusException;
import com.grechanikovars.eventbus.exception.EventDispatchException;
import com.grechanikovars.eventbus.internal.EventHierarchyResolver;
import com.grechanikovars.eventbus.internal.SubscribeMethodCache;
import com.grechanikovars.eventbus.internal.SubscriberMethod;
import com.grechanikovars.eventbus.internal.SubscriberRegistry;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Default {@link EventBus} implementation. Delegates method scanning/caching to
 * {@link SubscribeMethodCache}, event-type matching to {@link EventHierarchyResolver}, and
 * subscriber storage to {@link SubscriberRegistry}.
 */
public class SimpleEventBus implements EventBus {

  private static final Logger LOGGER = LogManager.getLogger(SimpleEventBus.class);

  private final SubscribeMethodCache methodCache;
  private final EventHierarchyResolver hierarchyResolver;
  private final SubscriberRegistry registry;

  public SimpleEventBus() {
    this(new SubscribeMethodCache(), new EventHierarchyResolver(), new SubscriberRegistry());
  }

  SimpleEventBus(SubscribeMethodCache methodCache, EventHierarchyResolver hierarchyResolver,
      SubscriberRegistry registry) {
    this.methodCache = methodCache;
    this.hierarchyResolver = hierarchyResolver;
    this.registry = registry;
  }

  @Override
  public void register(Object listener) {
    List<Method> subscribeMethods = methodCache.getSubscribeMethods(listener.getClass());
    for (Method method : subscribeMethods) {
      Class<?> eventType = method.getParameterTypes()[0];
      registry.addSubscriber(eventType, new SubscriberMethod(listener, method, eventType));
    }
    LOGGER.info("Registered listener '{}' with {} subscribe method(s)",
        listener.getClass().getName(), subscribeMethods.size());
  }

  @Override
  public void unregister(Object listener) {
    List<Method> subscribeMethods = methodCache.getSubscribeMethods(listener.getClass());
    for (Method method : subscribeMethods) {
      Class<?> eventType = method.getParameterTypes()[0];
      registry.removeSubscriber(eventType, listener);
    }
    LOGGER.info("Unregistered listener '{}'", listener.getClass().getName());
  }

  @Override
  public void post(Object event) {
    List<Class<?>> eventHierarchy = hierarchyResolver.resolve(event.getClass());
    List<SubscriberMethod> matchingSubscribers = new ArrayList<>();
    for (Class<?> type : eventHierarchy) {
      matchingSubscribers.addAll(registry.getSubscribers(type));
    }

    List<EventBusException> failures = new ArrayList<>();
    for (SubscriberMethod subscriberMethod : matchingSubscribers) {
      try {
        subscriberMethod.invoke(event);
      } catch (EventBusException e) {
        LOGGER.error("Subscriber invocation failed for event '{}'", event.getClass().getName(), e);
        failures.add(e);
      }
    }

    if (!failures.isEmpty()) {
      throw new EventDispatchException(
          String.format("%d subscriber(s) failed while dispatching event '%s'",
              failures.size(), event.getClass().getName()),
          failures);
    }
  }
}
