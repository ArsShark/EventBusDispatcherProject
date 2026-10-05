package com.grechanikovars.eventbus.core;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.grechanikovars.eventbus.exception.EventBusException;
import com.grechanikovars.eventbus.exception.EventDispatchException;
import com.grechanikovars.eventbus.testfixtures.BaseEvent;
import com.grechanikovars.eventbus.testfixtures.CatchAllListener;
import com.grechanikovars.eventbus.testfixtures.ChildEvent;
import com.grechanikovars.eventbus.testfixtures.FailingListener;
import com.grechanikovars.eventbus.testfixtures.InvalidListener;
import com.grechanikovars.eventbus.testfixtures.RecordingListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SimpleEventBusTest {

  private EventBus eventBus;

  @BeforeEach
  void setUp() {
    eventBus = new SimpleEventBus();
  }

  @Test
  void post_shouldInvokeMatchingSubscriberExactlyOnce() {
    RecordingListener listener = new RecordingListener();
    eventBus.register(listener);

    eventBus.post(new BaseEvent("hello"));

    assertEquals(1, listener.getReceivedEvents().size());
  }

  @Test
  void post_shouldDeliverSubclassEventToSuperclassSubscriber() {
    RecordingListener listener = new RecordingListener();
    eventBus.register(listener);

    eventBus.post(new ChildEvent("hi"));

    assertEquals(1, listener.getReceivedEvents().size());
  }

  @Test
  void post_shouldDeliverEveryEventToSubscriberOfObject() {
    CatchAllListener catchAll = new CatchAllListener();
    eventBus.register(catchAll);

    eventBus.post(new BaseEvent("a"));
    eventBus.post(new ChildEvent("b"));
    eventBus.post("plain string event");

    assertEquals(3, catchAll.getReceivedEvents().size());
  }

  @Test
  void post_shouldDeliverToMultipleSubscribersOfSameEvent() {
    RecordingListener first = new RecordingListener();
    RecordingListener second = new RecordingListener();
    eventBus.register(first);
    eventBus.register(second);

    eventBus.post(new BaseEvent("hello"));

    assertAll(
        () -> assertEquals(1, first.getReceivedEvents().size()),
        () -> assertEquals(1, second.getReceivedEvents().size())
    );
  }

  @Test
  void unregister_shouldStopDeliveringEventsToThatListener() {
    RecordingListener listener = new RecordingListener();
    eventBus.register(listener);
    eventBus.unregister(listener);

    eventBus.post(new BaseEvent("hello"));

    assertTrue(listener.getReceivedEvents().isEmpty());
  }

  @Test
  void unregister_shouldNotAffectOtherRegisteredListeners() {
    RecordingListener staysRegistered = new RecordingListener();
    RecordingListener getsRemoved = new RecordingListener();
    eventBus.register(staysRegistered);
    eventBus.register(getsRemoved);

    eventBus.unregister(getsRemoved);
    eventBus.post(new BaseEvent("hello"));

    assertAll(
        () -> assertEquals(1, staysRegistered.getReceivedEvents().size()),
        () -> assertTrue(getsRemoved.getReceivedEvents().isEmpty())
    );
  }

  @Test
  void post_shouldAggregateFailuresButStillInvokeHealthySubscribers() {
    eventBus.register(new FailingListener());
    RecordingListener healthyListener = new RecordingListener();
    eventBus.register(healthyListener);

    EventDispatchException exception = assertThrows(EventDispatchException.class,
        () -> eventBus.post(new BaseEvent("hello")));

    assertAll(
        () -> assertEquals(1, exception.getFailures().size()),
        () -> assertEquals(1, healthyListener.getReceivedEvents().size())
    );
  }

  @Test
  void register_shouldThrowForInvalidSubscribeMethodSignature() {
    assertThrows(EventBusException.class, () -> eventBus.register(new InvalidListener()));
  }

  @Test
  void concurrentRegisterUnregisterAndPost_shouldNotThrowConcurrentModificationException()
      throws InterruptedException {
    int threadCount = 20;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    List<Future<?>> futures = new ArrayList<>();

    for (int i = 0; i < threadCount; i++) {
      futures.add(executor.submit(() -> {
        RecordingListener listener = new RecordingListener();
        eventBus.register(listener);
        eventBus.post(new BaseEvent("concurrent"));
        eventBus.unregister(listener);
      }));
    }

    executor.shutdown();
    assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
    for (Future<?> future : futures) {
      assertDoesNotThrow(() -> future.get(1, TimeUnit.SECONDS));
    }
  }
}
