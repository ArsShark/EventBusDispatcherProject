package com.grechanikovars.eventbus.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as an event handler. The method must declare exactly one parameter — the type
 * of event it wants to receive. The {@code EventBus} delivers a posted event to every
 * {@code @Subscribe} method whose parameter type is the event's class, one of its superclasses,
 * or one of its interfaces.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Subscribe {
}
