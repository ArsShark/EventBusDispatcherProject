package com.grechanikovars.eventbus.testfixtures;

import com.grechanikovars.eventbus.annotation.Subscribe;

public class InvalidListener {

  @Subscribe
  public void badMethod() {

  }
}
