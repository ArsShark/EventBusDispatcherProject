package com.grechanikovars.eventbus.testfixtures;

public class BaseEvent {

  private final String message;

  public BaseEvent(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
