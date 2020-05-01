

package com.android.myapplication;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;


public class LocalBroadCastManager {

    private static LocalBroadCastManager INSTANCE;

    private final Map<String, Set<LocalConsumer>> subscribers = new ConcurrentHashMap<>();

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


    public void subscribe(String eventType, LocalConsumer subscriber) throws NullPointerException {
        if (eventType == null) {
            throw new NullPointerException("eventType is null");
        }
        if (subscriber == null) {
            throw new NullPointerException("subscriber is null");
        }
        Set<LocalConsumer> eventSubscribers = getOrCreateSubscribers(eventType);
        eventSubscribers.add(subscriber);
    }

    private Set<LocalConsumer> getOrCreateSubscribers(String eventType) {
        //It should be thread safe
        Set<LocalConsumer> eventSubscribers = subscribers.get(eventType);
        if (eventSubscribers == null) {
            eventSubscribers = new CopyOnWriteArraySet<>();
            subscribers.put(eventType, eventSubscribers);
        }
        return eventSubscribers;
    }


    public void unsubscribe(LocalConsumer subscriber) {
        Objects.requireNonNull(subscriber, "subscriber");

        subscribers.values().forEach(eventSubscribers -> eventSubscribers.remove(subscriber));
    }


    public <T> void unsubscribe(String eventType, LocalConsumer subscriber) {
        Objects.requireNonNull(eventType, "eventType");
        Objects.requireNonNull(subscriber, "subscriber");
        subscribers.get(eventType).remove(subscriber);
    }


    public void publish(String event) {
        Objects.requireNonNull(event, "event");

        if (subscribers.get(event) != null) {
            subscribers.get(event).forEach(s -> s.accept(event, new Object()));
        }
    }


    public static final String EVENT1 = "EVENT1";
    public static final String EVENT2 = "EVENT2";
    public static final String EVENT3 = "EVENT3";

}
