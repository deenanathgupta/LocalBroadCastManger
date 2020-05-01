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


public class LocalBroadCastManager {

    private static LocalBroadCastManager INSTANCE;

    private final Map<Class<?>, Set<Consumer>> subscribers = new ConcurrentHashMap<>();

    private LocalBroadCastManager() {
    }


    public static LocalBroadCastManager getInstance() {
        if (INSTANCE == null) {
            synchronized (LocalBroadCastManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LocalBroadCastManager();
                }
            }
        }
        return INSTANCE;
    }


    public <T> void subscribe(Class<? extends T> eventType, Consumer<T> subscriber) throws NullPointerException {
        if (eventType == null) {
            throw new NullPointerException("eventType is null");
        } else if (subscriber == null) {
            throw new NullPointerException("subscriber is null");
        }
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


    public void unsubscribe(Consumer<?> subscriber) {
        Objects.requireNonNull(subscriber, "subscriber");

        subscribers.values().forEach(eventSubscribers -> eventSubscribers.remove(subscriber));
    }


    public <T> void unsubscribe(Class<? extends T> eventType, Consumer<T> subscriber) {
        Objects.requireNonNull(eventType, "eventType");
        Objects.requireNonNull(subscriber, "subscriber");

        subscribers.keySet().stream()
                .filter(type -> eventType.isAssignableFrom(type))
                .map(type -> subscribers.get(type))
                .forEach(eventSubscribers -> eventSubscribers.remove(subscriber));
    }


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
