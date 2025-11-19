package com.walk.or.die.engine;

import java.util.Map;
import java.util.function.Consumer;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class MCEventBus {

    private static MCEventBus instance;

    public static MCEventBus get() {
        if (instance == null) instance = new MCEventBus();
        return instance;
    }

    private MCEventBus() {
        this.listeners = new HashMap<>();
    }

    private void MCDeconstructor() {
        System.out.println("Je suis un homme déconstruit");
    }

    private Map<String, List<Consumer<Object>>> listeners;

    public void on(String eventName, Consumer<Object> listener) {
        listeners.computeIfAbsent(eventName, k -> new ArrayList<>()).add(listener);
    }

    public void off(String eventName, Consumer<Object> listener) {
        List<Consumer<Object>> listenersList = listeners.get(eventName);
        if (listenersList == null) return;
        listenersList.remove(listener);
        if (listenersList.isEmpty()) {
            listeners.remove(eventName);    
        }
    }

    public void emit(String eventName, Object data) {
        List<Consumer<Object>> listenersList = listeners.get(eventName);
        if (listenersList == null) return;
        List<Consumer<Object>> copy = new ArrayList<>(listenersList);
        for (Consumer<Object> listener : copy) {
            listener.accept(data);
        }
    }

    public void emit(String eventName) {
        emit(eventName, null);
    }
}
