package com.walk.or.die.engine.sm;

import com.walk.or.die.engine.MCEventBus;
import com.walk.or.die.engine.entities.MCEntity;

import java.util.List;
import java.util.ArrayList;

public abstract class MCStateMachine<T extends MCState> {
    
    // Classe pour l'event de transition
    public static class TransitionArgs<T extends MCState.StateArgs> {
        public String prevState;
        public String nextState;
        public T args;

        public TransitionArgs (String prevState, String nextState, T args) {
            this.prevState = prevState;
            this.nextState = nextState;
            this.args = args;
        }
    }

    protected MCEntity parent;
    private List<T> states;
    private T currentState;

    public MCStateMachine() {
        this.parent = null;
        states = new ArrayList<T>();
    }

    public T getCurrentState() {
        return currentState;
    }

    public boolean isIn(String name) {
        return currentState.getName().equals(name);
    }

    public void setCurrentState(String name, T.StateArgs args) {
        currentState = getState(name);
        currentState.enter(args); 
    }

    public void addState(T state) {
        states.add(state);
    }

    public void update(float delta) {
        if (currentState == null) return ;
        currentState.update(delta);
    }

    public void stateTransitionCheck(TransitionArgs<?> args) {
        if (!args.prevState.equals(currentState.getName()) || !stateExists(args.nextState)) {
            return ;
        }

        //System.out.println("Transition from " + args.prevState + " to " + args.nextState);
        stateTransition(currentState, getState(args.nextState), args.args);
        
    }

    public boolean stateExists(String name) {
        for (T state : states) {
            if (state.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public T getState(String name) {
        for (T state : states) {
            if (state.getName().equalsIgnoreCase(name)) {
                return state;
            }
        }
        return null;
    }

    private <U extends T.StateArgs> void stateTransition(T prevState, T nextState, U args) {
        if (!prevState.getName().equals(currentState.getName())) {
            return ;
        }

        currentState.exit();
        nextState.enter(args);
        currentState = nextState;
    }
}