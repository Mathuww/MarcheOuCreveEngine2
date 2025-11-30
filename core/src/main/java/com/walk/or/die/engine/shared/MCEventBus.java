package com.walk.or.die.engine.shared;

import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.sm.entity.MCEntityState;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.exceptions.VoluntaryCrashException;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import com.walk.or.die.engine.vehicles.MCVehicle;

public class MCEventBus implements MCVehicle {
    private static MCEventBus instance;

    public static MCEventBus get() {
        if (instance == null) instance = new MCEventBus();
        return instance;
    }

    public static class Subscription {
        Class cl;
        int id;
        String eventName;
        Consumer<?> listener;

        public Subscription(Object obj, String eventName, Consumer<?> listener) {
            this.cl = obj.getClass();
            this.id = System.identityHashCode(obj);
            this.eventName = eventName;
            this.listener = listener;
        }

        public boolean check(Object obj, String eventName) {
            return (obj.getClass() == cl && id == System.identityHashCode(obj) && eventName == this.eventName);
        }

        public void unsubscribe() {
            MCEventBus.get().unsubscribe(this);
        }
    
    }

    private MCEventBus() {
        this.listeners = new HashMap<>();
        this.eventTypes = new HashMap<>();
        this.subscriptions = new ArrayList<>();
        addEvent("InputPressed", MCInputManager.Command.class);
        addEvent("InputReleased", MCInputManager.Command.class);
        addEvent("EntityTileReached", MCEntity.TileReachedArgs.class);
        addEvent("connectMouseMoved", MCInputManager.MouseListener.class);
        addEvent("disconnectMouseMoved", MCInputManager.MouseListener.class);
        //addEvent("ChangedFocus", MCEntity.class);
        //addEvent("ChangeState", MCStateMachine.TransitionArgs.class);
    }

    private void MCDeconstructor() {
        System.out.println("Je suis un homme déconstruit");
    }

    private Map<String, List<Consumer<?>>> listeners;
    private Map<String, Class<?>> eventTypes;
    private List<Subscription> subscriptions;
    
    private int lineNumber = 62;
    private String destination = "Aspremont";

    public <T> void addEvent(String eventName, Class<T> argType) {
        eventTypes.put(eventName, argType);
        listeners.putIfAbsent(eventName, new ArrayList<>());
    }

    public <T> void on(Object obj, String eventName, Consumer<T> listener) {
        Class<?> argType = eventTypes.get(eventName);
        if (argType == null) {
            throw new IllegalArgumentException("event bus : trying to subscribe to unregistered event " + eventName);
        }
        listeners.get(eventName).add(listener);
        subscriptions.add(new Subscription(obj, eventName, listener));
    }

    protected void unsubscribe(Subscription sub) {
        List<Consumer<?>> listenersList = listeners.get(sub.eventName);
        /* je me demandais si c'était néceessaire d'ajouter une exception si on n'a pas enregistré de listener en vrai jsp */
        if (listenersList == null) return;
        listenersList.remove(sub.listener);
        subscriptions.remove(sub);
    }

    private void clear(List list) {
        for (Subscription sub : new ArrayList<>(subscriptions)) {
            subscriptions.remove(sub);
        }
    }

    public <T> void off(Object obj, String eventName) {
        List list = new ArrayList<>();
        for (Subscription sub : new ArrayList<>(subscriptions)) {
            if (sub.check(obj, eventName)) {
                sub.unsubscribe();
                list.add(sub);
            }
        }
        clear(list);
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
        for (Consumer<?> genericListener : listenersListCopy) {
            Consumer<T> listener = (Consumer<T>) genericListener;
            listener.accept(data);
        }
    }

    public void emit(String eventName) {
        emit(eventName, null);
    }

    public void start() {
        System.out.println("le bus demarre !");
    }

    public void stop() throws VoluntaryCrashException {
        System.out.println("We never stop idiot");
        crash();
    }

    public void crash() throws VoluntaryCrashException {
        throw new VoluntaryCrashException("EXPLOSION !!!!!! over. *Fermeture des rideaux*");
    }
}
