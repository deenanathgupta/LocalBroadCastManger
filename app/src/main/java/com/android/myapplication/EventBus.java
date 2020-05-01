/*
 * Copyright (c) 2017 Falko Schumann
 * Released under the terms of the MIT License.
 */

package com.android.myapplication;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

/**
 * Simple event bus implementation.
 * <p>
 * Subscribe and publish events. Events are published in channels distinguished by event type. Channels can be grouped
 * using an event type hierarchy.
 * <p>
 * You can use the default event bus instance {@link #getInstance}, which is a singleton or you can create one or multiple
 * instances of {@link EventBus}.
 */
public class EventBus {

    private static EventBus INSTANCE;

    private final Map<Class<?>, Set<Consumer>> subscribers = new ConcurrentHashMap<>();

    private EventBus() {
    }

    /**
     * Get the default event bus, can be used as application wide singleton.
     *
     * @return the only instance of the default event bus.
     */
    public static EventBus getInstance() {
        if (INSTANCE == null) {
            synchronized (EventBus.class) {
                if (INSTANCE == null) {
                    INSTANCE = new EventBus();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Subscribe to an event type
     *
     * @param eventType  the event type, can be a super class of all events to subscribe.
     * @param subscriber the subscriber which will consume the events.
     * @param <T>        the event type class.
     */
    public <T> void subscribe(Class<? extends T> eventType, Consumer<T> subscriber) {
        Objects.requireNonNull(eventType, "eventType");
        Objects.requireNonNull(subscriber, "subscriber");

        Set<Consumer> eventSubscribers = getOrCreateSubscribers(eventType);
        eventSubscribers.add(subscriber);
    }

    private <T> Set<Consumer> getOrCreateSubscribers(Class<T> eventType) {
        Set<Consumer> eventSubscribers = subscribers.get(eventType);
        if (eventSubscribers == null) {
            eventSubscribers = new CopyOnWriteArraySet<>();
            subscribers.put(eventType, eventSubscribers);
        }
        return eventSubscribers;
    }

    /**
     * Unsubscribe from all event types.
     *
     * @param subscriber the subscriber to unsubscribe.
     */
    public void unsubscribe(Consumer<?> subscriber) {
        Objects.requireNonNull(subscriber, "subscriber");

        subscribers.values().forEach(eventSubscribers -> eventSubscribers.remove(subscriber));
    }

    /**
     * Unsubscribe from an event type.
     *
     * @param eventType  the event type, can be a super class of all events to unsubscribe.
     * @param subscriber the subscriber to unsubscribe.
     * @param <T>        the event type class.
     */
    public <T> void unsubscribe(Class<? extends T> eventType, Consumer<T> subscriber) {
        Objects.requireNonNull(eventType, "eventType");
        Objects.requireNonNull(subscriber, "subscriber");

        subscribers.keySet().stream()
                .filter(type -> eventType.isAssignableFrom(type))
                .map(type -> subscribers.get(type))
                .forEach(eventSubscribers -> eventSubscribers.remove(subscriber));
    }

    /**
     * Publish an event to all subscribers.
     * <p>
     * The event type is the class of <code>event</code>. The event is published to all consumers which subscribed to
     * this event type or any super class.
     *
     * @param event the event.
     */
    public void publish(Object event) {
        Objects.requireNonNull(event, "event");

        Class<?> eventType = event.getClass();
        subscribers.keySet().stream()
                .filter(type -> type.isAssignableFrom(eventType))
                .flatMap(type -> subscribers.get(type).stream())
                .forEach(subscriber -> publish(event, subscriber));
    }

    private static void publish(Object event, Consumer subscriber) {
        try {
            subscriber.accept(event);
        } catch (Exception e) {
            Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
        }
    }

}