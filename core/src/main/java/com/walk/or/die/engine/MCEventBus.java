package com.walk.or.die.engine;

import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.states.MCState;
import com.walk.or.die.engine.states.MCStateMachine;

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
        this.eventTypes = new HashMap<>();
        addEvent("InputPressed", MCInputManager.Command.class);
        addEvent("InputReleased", MCInputManager.Command.class);
        addEvent("ChangeState", MCStateMachine.TransitionArgs.class);

    }

    private void MCDeconstructor() {
        System.out.println("Je suis un homme déconstruit");
    }

    private Map<String, List<Consumer<?>>> listeners;
    private Map<String, Class<?>> eventTypes;

    public <T> void addEvent(String eventName, Class<T> argType) {
        eventTypes.put(eventName, argType);
        listeners.putIfAbsent(eventName, new ArrayList<>());
    }

    public <T> void on(String eventName, Consumer<T> listener) {
        Class<?> argType = eventTypes.get(eventName);
        if (argType == null) {
            throw new IllegalArgumentException("event bus : trying to subscribe to unregistrevent event " + eventName);
        }
        listeners.get(eventName).add(listener);
    }

    public <T> void off(String eventName, Consumer<T> listener) {
        List<Consumer<?>> listenersList = listeners.get(eventName);
        if (listenersList == null) return;
        boolean debug = listenersList.remove(listener);
        if (debug) {
            System.out.println("removed from event lsitenre");
        } else {
            System.out.println("nothing to remvoe");
        }
    }

    public <T> void emit(String eventName, T data) {
        Class<?> argType = eventTypes.get(eventName);
        if (argType == null) {
            throw new IllegalArgumentException("event bus : trying to emit event " + eventName + " that is not registered yet");
        }
        if (data != null && !argType.isInstance(data)) {
            throw new IllegalArgumentException("event bus : event " + eventName + " excepts to have a listener of type " + argType.getSimpleName() + " but got " + data.getClass().getSimpleName());
        }

        List<Consumer<?>> listenersList = listeners.get(eventName);
        if (listenersList == null) return;
        List<Consumer<?>> listenersListCopy = new ArrayList<>(listenersList);
        //List<Consumer<Object>> copy = new ArrayList<>(listenersList);
        for (Consumer<?> genericListener : listenersListCopy) {
            Consumer<T> listener = (Consumer<T>) genericListener;
            listener.accept(data);
        }
    }

    public void emit(String eventName) {
        emit(eventName, null);
    }
}
