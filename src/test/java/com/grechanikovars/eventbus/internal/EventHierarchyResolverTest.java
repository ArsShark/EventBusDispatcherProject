package com.grechanikovars.eventbus.internal;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grechanikovars.eventbus.testfixtures.BaseEvent;
import com.grechanikovars.eventbus.testfixtures.ChildEvent;
import java.util.List;
import org.junit.jupiter.api.Test;

class EventHierarchyResolverTest {

  private final EventHierarchyResolver resolver = new EventHierarchyResolver();

  @Test
  void resolve_shouldIncludeEventClassSuperclassesAndObject() {
    List<Class<?>> hierarchy = resolver.resolve(ChildEvent.class);

    assertAll(
        () -> assertTrue(hierarchy.contains(ChildEvent.class)),
        () -> assertTrue(hierarchy.contains(BaseEvent.class)),
        () -> assertTrue(hierarchy.contains(Object.class))
    );
  }

  @Test
  void resolve_shouldNotIncludeUnrelatedClasses() {
    List<Class<?>> hierarchy = resolver.resolve(BaseEvent.class);

    assertTrue(hierarchy.contains(BaseEvent.class) && hierarchy.contains(Object.class)
        && !hierarchy.contains(ChildEvent.class));
  }

  @Test
  void resolve_shouldReturnCachedInstanceOnSecondCallForSameClass() {
    List<Class<?>> first = resolver.resolve(ChildEvent.class);
    List<Class<?>> second = resolver.resolve(ChildEvent.class);

    assertSame(first, second);
  }
}
