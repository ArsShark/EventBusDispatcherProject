package com.grechanikovars.eventbus.internal;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Resolves and caches the full set of types a posted event should be matched against: the
 * event's own class, every superclass up to and including {@link Object}, and every interface
 * implemented at any level of that hierarchy. Because {@code Object} is always included,
 * a listener subscribed to {@code Object} receives every event posted through the bus.
 *
 * <p>The hierarchy for a given event class never changes at runtime, so it is computed once and
 * cached. This cache is intentionally separate from subscriber storage: which listeners are
 * currently registered for a type can change at any moment, so that lookup is never cached here.
 */
public class EventHierarchyResolver {

  private final ConcurrentHashMap<Class<?>, List<Class<?>>> hierarchyCache = new ConcurrentHashMap<>();

  public List<Class<?>> resolve(Class<?> eventClass) {
    return hierarchyCache.computeIfAbsent(eventClass, this::buildHierarchy);
  }

  private List<Class<?>> buildHierarchy(Class<?> eventClass) {
    Set<Class<?>> visited = new LinkedHashSet<>();
    Deque<Class<?>> toVisit = new ArrayDeque<>();
    toVisit.add(eventClass);

    while (!toVisit.isEmpty()) {
      Class<?> current = toVisit.poll();
      if (current == null || !visited.add(current)) {
        continue;
      }
      Collections.addAll(toVisit, current.getInterfaces());
      if (current.getSuperclass() != null) {
        toVisit.add(current.getSuperclass());
      }
    }
    return List.copyOf(visited);
  }
}
