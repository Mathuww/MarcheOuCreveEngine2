package com.walk.or.die.engine.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.exceptions.VoluntaryCrashException;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.sm.game.MCGameState;
import com.walk.or.die.engine.vehicles.MCVehicle;

/**
 * The singleton which creates, handles and manages events through the game.
 * @see MCVehicle
 */
public class MCEventBus implements MCVehicle {
    private static MCEventBus instance;

    /**
     * Get the singleton instance.
     * @return
     */
    public static MCEventBus get() {
        if (instance == null) instance = new MCEventBus();
        return instance;
    }

    /**
     * A class wich represents a subscription at an event.
     */
    public static class Subscription {
        Class cl;
        int id;
        String eventName;
        Consumer<?> listener;

        /**
         * The constructor.
         * @param obj
         * @param eventName
         * @param listener
         */
        public Subscription(Object obj, String eventName, Consumer<?> listener) {
            this.cl = obj.getClass();
            this.id = System.identityHashCode(obj);
            this.eventName = eventName;
            this.listener = listener;
        }

        /**
         * Return if the subscription concerns the given object and event.
         * @param obj
         * @param eventName
         * @return 
         */
        public boolean check(Object obj, String eventName) {
            return (obj.getClass() == cl && id == System.identityHashCode(obj) && eventName == this.eventName);
        }

        /**
         * End a subscription.
         */
        public void unsubscribe() {
            MCEventBus.get().unsubscribe(this);
        }
    
    }

    /**
     * The constructor.
     */
    private MCEventBus() {
        this.listeners = new HashMap<>();
        this.eventTypes = new HashMap<>();
        this.subscriptions = new ArrayList<>();
        addEvent("InputPressed", MCInputManager.Command.class);
        addEvent("InputReleased", MCInputManager.Command.class);
        addEvent("EntityTileReached", MCEntity.TileReachedArgs.class);
        addEvent("connectMouseMoved", MCInputManager.MouseListener.class);
        addEvent("disconnectMouseMoved", MCInputManager.MouseListener.class);
        addEvent("GameStateChanged", MCGameState.class);
        //addEvent("ChangedFocus", MCEntity.class);
        //addEvent("ChangeState", MCStateMachine.TransitionArgs.class);
    }

    /**
     * The performative deconstructor.
     */
    private void MCDeconstructor() {
        System.out.println("Je suis un homme déconstruit");
    }

    private Map<String, List<Consumer<?>>> listeners;
    private Map<String, Class<?>> eventTypes;
    private List<Subscription> subscriptions;
    
    private int lineNumber = 62;
    private String destination = "Aspremont";

    /**
     * Create a new event, with a name and the type of argument it needs.
     * @param <T>
     * @param eventName
     * @param argType
     */
    public <T> void addEvent(String eventName, Class<T> argType) {
        eventTypes.put(eventName, argType);
        listeners.putIfAbsent(eventName, new ArrayList<>());
    }

    /**
     * Connect an object's function at an event. 
     * @param <T>
     * @param obj
     * @param eventName
     * @param listener
     */
    public <T> void on(Object obj, String eventName, Consumer<T> listener) {
        Class<?> argType = eventTypes.get(eventName);
        if (argType == null) {
            throw new IllegalArgumentException("event bus : trying to subscribe to unregistered event " + eventName);
        }
        listeners.get(eventName).add(listener);
        subscriptions.add(new Subscription(obj, eventName, listener));
    }

    private void unsubscribe(Subscription sub) {
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

    /**
     * Disconnect an object's function from an event.
     * @param obj
     * @param eventName
     */
    public void off(Object obj, String eventName) {
        List list = new ArrayList<>();
        for (Subscription sub : new ArrayList<>(subscriptions)) {
            if (sub.check(obj, eventName)) {
                sub.unsubscribe();
                list.add(sub);
            }
        }
        clear(list);
    }

    /**
     * Call each functions linked to the event.
     * @param <T>
     * @param eventName
     * @param data
     */
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

    /**
     * Call each functions linked to the event.
     * @param eventName
     */
    public void emit(String eventName) {
        emit(eventName, null);
    }


    public void start() {
        System.out.println("le bus demarre !");
    }

    /**
     * NO ONE STOP US, IDIOT
     */
    public void stop() throws VoluntaryCrashException {
        System.out.println("We never stop idiot");
        crash();
    }

    /**
     * CRASH THE BUS IN YOUR FACE
     */
    public void crash() throws VoluntaryCrashException {
        throw new VoluntaryCrashException("EXPLOSION !!!!!! over. *Fermeture des rideaux*");
    }
}
