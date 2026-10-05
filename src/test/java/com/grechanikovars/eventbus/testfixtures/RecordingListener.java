package com.grechanikovars.eventbus.testfixtures;

import com.grechanikovars.eventbus.annotation.Subscribe;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RecordingListener {

  private final List<Object> receivedEvents = new CopyOnWriteArrayList<>();

  @Subscribe
  public void onBaseEvent(BaseEvent event) {
    receivedEvents.add(event);
  }

  public List<Object> getReceivedEvents() {
    return receivedEvents;
  }
}
