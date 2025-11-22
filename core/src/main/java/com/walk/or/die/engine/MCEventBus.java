package com.walk.or.die.engine;

import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.states.MCState;
import com.walk.or.die.engine.states.MCStateMachine;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

public class MCEventBus {
    private static MCEventBus instance;

    public static MCEventBus get() {
        if (instance == null) instance = new MCEventBus();
        return instance;
    }

    public static class Subscription {
        String eventName;
        Consumer<?> listener;

        public Subscription(String eventName, Consumer<?> listener) {
            this.eventName = eventName;
            this.listener = listener;
        }

        public void unsubscribe() {
            MCEventBus.get().off(this);
        }
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

    public <T> Subscription on(String eventName, Consumer<T> listener) {
        Class<?> argType = eventTypes.get(eventName);
        if (argType == null) {
            throw new IllegalArgumentException("event bus : trying to subscribe to unregistered event " + eventName);
        }
        listeners.get(eventName).add(listener);
        return new Subscription(eventName, listener);
    }

    public <T> void off(Subscription sub) {
        List<Consumer<?>> listenersList = listeners.get(sub.eventName);

        /* 
        try {
            String methodId = getMethodIdentifier(listener);
            // int instanceId = System.identityHashCode(stateInstance);
            // UUID uuid = UUID.nameUUIDFromBytes((instanceId + methodId).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            System.err.println("getMethodIdnetifier a planté");
        }
        */

        // Il faut faire en sorte de mettre ça dans le try et de vérifier avec le getMethodIdentifier
        // Soit inclure l'instance qui se register dans on et off, soit une méthode qui compile l'instance + la méthode accessible hors de event bus,
        // qui permet de créer l'identifiant qu'on passe soit même en paramètres

        /* je me demandais si c'était néceessaire d'ajouter une exception si on n'a pas enregistré de listener en vrai jsp */
        if (listenersList == null) return;

        listenersList.remove(sub.listener);
    }

    /*
    public static String getMethodIdentifier(Consumer<?> c) throws Exception {
        Method writeReplace = c.getClass().getDeclaredMethod("writeReplace");
        writeReplace.setAccessible(true);
        SerializedLambda lambda = (SerializedLambda) writeReplace.invoke(c);
        return lambda.getImplClass() + "::" + lambda.getImplMethodName();
    }
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
