package com.walk.or.die.engine.sm;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;


/**
 * Class with all state methods global
 * @param <T> type of state
 * @param <U> parent object owning this state machine
 */
public class MCStateMachine<T extends MCState, U> {
    
    /**
     * Class intern to permit to clean transition
     * with the previous and the next state
     * @param <T> next State
     */
    public static class TransitionArgs<T extends MCState.StateArgs> {
        /**
         * Name of State before the transition (previous state)
         */
        public String prevState;

        /**
         * Name of State after transition (next state)
         */
        public String nextState;

        /**
         * Specials arguments of the next state
         */
        public T args;

        /**
         * Constructor
         * @param prevState
         * @param nextState
         * @param args
         */
        public TransitionArgs (String prevState, String nextState, T args) {
            this.prevState = prevState;
            this.nextState = nextState;
            this.args = args;
        }
    }

    /**
     * parent object owning this state machine
     */
    protected U parent;

    /**
     * List  possibilities states for parent 
     */
    private List<T> states;

    /**
     * Actual state
     */
    private T currentState;

    /**
     * Receives previous and next state when the state stransition in successful
     */
    private BiConsumer<T, T> callback;


    /**
     * Constructor
     * @param parent
     */
    public MCStateMachine(U parent) {
        this.parent = parent;
        states = new ArrayList<T>();
    }

    /**
     * Getter of currentState
     * @return currentState
     */
    public T getCurrentState() {
        return currentState;
    }

    /**
     * Setter of currentState
     * @param name
     * @param args
     */
    public void setCurrentState(String name, T.StateArgs args) {
        currentState = getState(name);
        currentState.enter(args); 
    }

    /**
     * Verificate if the name of state is equal in current state
     * @param name
     * @return verification of equality
     */
    public boolean isIn(String name) {
        return currentState.getName().equals(name);
    }

    /**
     * Add a possibility state to states
     * @param state
     */
    public void addState(T state) {
        states.add(state);
    }

    /**
     * Global update
     * @param delta
     * @return exceptionally if the current state doesn't exist
     */
    public void update(float delta) {
        if (currentState == null) return ;
        currentState.update(delta);
    }
    
    /**
     * Global render
     * @param batch
     * @return exceptionally if the current state doesn't exist
     */
    public void render(SpriteBatch batch) {
        if (currentState == null) return;
        currentState.render(batch);
    }

    /**
     * !!Je te la laisse lol!! STP s'il te plaît
     * @param batch
     */
    public void renderEffects(SpriteBatch batch) {
        if (currentState == null) return;
        currentState.renderEffects(batch);
    }

    /**
     * Verification if the args are possible for the transition between the previous and the next state
     * if yes, this function execute transition
     * @param args
     */
    public void stateTransitionCheck(TransitionArgs<?> args) {
        if (!args.prevState.equals(currentState.getName()) || !stateExists(args.nextState)) {
            return ;
        }

        // System.out.println("Transition from " + args.prevState + " to " + args.nextState);
        stateTransition(currentState, getState(args.nextState), args.args);
        
    }

    /**
     * Verificate if the name is in the states (the list of parent possibilities states)
     * @param name
     * @return verification if the state exist
     */
    public boolean stateExists(String name) {
        for (T state : states) {
            if (state.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Getteur state (with a name)
     * @param name
     * @return state (or null)
     */
    public T getState(String name) {
        for (T state : states) {
            if (state.getName().equalsIgnoreCase(name)) {
                return state;
            }
        }
        return null;
    }

    /**
     * Clean transition
     * @param <V>
     * @param prevState
     * @param nextState
     * @param args
     */
    private <V extends T.StateArgs> void stateTransition(T prevState, T nextState, V args) {
        if (!prevState.getName().equals(currentState.getName())) {
            return ;
        }

        currentState.exit();
        nextState.enter(args);
        currentState = nextState;
        if (callback != null) callback.accept(prevState, nextState);
    }
    
    /**
     * Setter of callback
     * @param callback
     */
    public void setCallback(BiConsumer<T, T> callback) {
        this.callback = callback;
    }

}