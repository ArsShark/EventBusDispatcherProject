package com.grechanikovars.eventbus.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grechanikovars.eventbus.exception.EventBusException;
import com.grechanikovars.eventbus.testfixtures.InvalidListener;
import com.grechanikovars.eventbus.testfixtures.RecordingListener;
import java.lang.reflect.Method;
import java.util.List;
import org.junit.jupiter.api.Test;

class SubscribeMethodCacheTest {

  private final SubscribeMethodCache cache = new SubscribeMethodCache();

  @Test
  void getSubscribeMethods_shouldFindAnnotatedMethodAndMakeItAccessible() {
    List<Method> methods = cache.getSubscribeMethods(RecordingListener.class);

    assertEquals(1, methods.size());
    assertTrue(methods.get(0).canAccess(new RecordingListener()));
  }

  @Test
  void getSubscribeMethods_shouldThrowForMethodWithWrongParameterCount() {
    assertThrows(EventBusException.class, () -> cache.getSubscribeMethods(InvalidListener.class));
  }

  @Test
  void getSubscribeMethods_shouldReturnCachedListOnSecondCall() {
    List<Method> first = cache.getSubscribeMethods(RecordingListener.class);
    List<Method> second = cache.getSubscribeMethods(RecordingListener.class);

    assertSame(first, second);
  }
}
