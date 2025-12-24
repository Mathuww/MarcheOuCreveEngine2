package com.walk.or.die.engine.sm;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;


/**
 * Class to represents the state machine, which manages states and transitions between them.
 * @param <T> type of state
 * @param <U> parent object owning this state machine
 */
public class MCStateMachine<T extends MCState, U> {
    
    /**
     * Class intern to permit clean transitions between the previous and the next state.
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
     * Parent object owning this state machine.
     */
    protected U parent;

    /**
     * List of the possible states.
     */
    private List<T> states;

    /**
     * Actual state, wich is update an render each frame.
     */
    private T currentState;

    /**
     * Call when the state changes.
     */
    private BiConsumer<T, T> callback;


    /**
     * The constructor.
     * @param parent
     */
    public MCStateMachine(U parent) {
        this.parent = parent;
        states = new ArrayList<T>();
    }

    /**
     * Getter of currentState.
     * @return currentState
     */
    public T getCurrentState() {
        return currentState;
    }

    /**
     * Setter of currentState.
     * @param name
     * @param args
     */
    public void setCurrentState(String name, T.StateArgs args) {
        if (currentState != null) {
            currentState.exit();
        }
        currentState = getState(name);
        currentState.enter(args); 
    }

    /**
     * Check if the currentState have the given name.
     * @param name
     * @return 
     */
    public boolean isIn(String name) {
        return currentState.getName().equals(name);
    }

    /**
     * Add a possibe state at the state machine.
     * @param state
     */
    public void addState(T state) {
        states.add(state);
    }

    /**
     * Call each frame (used to call update in the current state).
     * @param delta
     * @return
     */
    public void update(float delta) {
        if (currentState == null) return ;
        currentState.update(delta);
    }
    
    /**
     * Render (used to call render in the current state). 
     * @param batch
     * @return exceptionally if the current state doesn't exist
     */
    public void render(SpriteBatch batch) {
        if (currentState == null) return;
        currentState.render(batch);
    }

    /**
     * Render call after the main render (used for visual effects).
     * @param batch
     */
    public void renderEffects(SpriteBatch batch) {
        if (currentState == null) return;
        currentState.renderEffects(batch);
    }

    /**
     * Allow or not the transition.
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
     * Check if a possible state have the given name.
     * @param name
     * @return
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
     * Get a state based on his name.
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
     * Transition between two states.
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