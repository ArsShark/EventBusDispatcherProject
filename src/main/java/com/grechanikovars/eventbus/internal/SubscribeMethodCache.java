package com.grechanikovars.eventbus.internal;

import com.grechanikovars.eventbus.annotation.Subscribe;
import com.grechanikovars.eventbus.exception.EventBusException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Caches the {@code @Subscribe}-annotated methods declared by each listener class, keyed by
 * {@code Class}, not by instance. Reflective scanning and signature validation therefore happen
 * exactly once per listener class no matter how many instances of that class get registered.
 */
public class SubscribeMethodCache {

  private static final Logger LOGGER = LogManager.getLogger(SubscribeMethodCache.class);

  private final ConcurrentHashMap<Class<?>, List<Method>> cache = new ConcurrentHashMap<>();

  public List<Method> getSubscribeMethods(Class<?> listenerClass) {
    return cache.computeIfAbsent(listenerClass, this::scanMethods);
  }

  private List<Method> scanMethods(Class<?> listenerClass) {
    List<Method> subscribeMethods = new ArrayList<>();
    for (Method method : listenerClass.getDeclaredMethods()) {
      if (!method.isAnnotationPresent(Subscribe.class)) {
        continue;
      }
      validateSignature(listenerClass, method);
      method.setAccessible(true);
      subscribeMethods.add(method);
    }
    LOGGER.debug("Cached {} @Subscribe method(s) for listener class '{}'",
        subscribeMethods.size(), listenerClass.getName());
    return List.copyOf(subscribeMethods);
  }

  private void validateSignature(Class<?> listenerClass, Method method) {
    if (method.getParameterCount() != 1) {
      throw new EventBusException(String.format(
          "Method '%s' in class '%s' is annotated with @Subscribe but does not declare exactly one parameter",
          method.getName(), listenerClass.getName()));
    }
  }
}
