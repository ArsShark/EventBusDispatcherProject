package com.grechanikovars.eventbus.testfixtures;

import com.grechanikovars.eventbus.annotation.Subscribe;

public class FailingListener {

  @Subscribe
  public void onBaseEvent(BaseEvent event) {
    throw new IllegalStateException("Simulated subscriber failure");
  }
}
