package com.walk.or.die.engine.sm;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;


/**
 * Represents the state machine, which manages states and transitions between them.
 * @param <T> Type of the state.
 * @param <U> Parent object that owns this state machine.
 */
public class MCStateMachine<T extends MCState, U> {
    
    /**
     * A class that permits clean transitions between the previous and next state.
     * @param <T> The next state.
     */
    public static class TransitionArgs<T extends MCState.StateArgs> {
        /**
         * Name of state before the transition (previous state).
         */
        public String prevState;

        /**
         * Name of state after transition (next state).
         */
        public String nextState;

        /**
         * Special arguments of the next state.
         */
        public T args;

        /**
         * Constructor.
         * @param prevState The previous state.
         * @param nextState The next state.
         * @param args The arguments.
         */
        public TransitionArgs (String prevState, String nextState, T args) {
            this.prevState = prevState;
            this.nextState = nextState;
            this.args = args;
        }
    }

    /**
     * The parent object that owns this state machine.
     */
    protected U parent;

    /**
     * A list of the possible states.
     */
    private List<T> states;

    /**
     * The actual state, which is updated and rendered each frame.
     */
    private T currentState;

    /**
     * The callback invoked when the state changes.
     */
    private BiConsumer<T, T> callback;


    /**
     * Constructs a new MCStateMachine with the specified parent.
     * @param parent The parent object.
     */
    public MCStateMachine(U parent) {
        this.parent = parent;
        states = new ArrayList<T>();
    }

    /**
     * Gets the current state.
     * @return The current state.
     */
    public T getCurrentState() {
        return currentState;
    }

    /**
     * Sets the current state.
     * @param name The name of the state.
     * @param args The arguments for the state.
     */
    public void setCurrentState(String name, T.StateArgs args) {
        if (currentState != null) {
            currentState.exit();
        }
        currentState = getState(name);
        currentState.enter(args); 
    }

    /**
     * Checks if the current state has the given name.
     * @param name The name to check.
     * @return True if the current state has the given name, false otherwise.
     */
    public boolean isIn(String name) {
        return currentState.getName().equals(name);
    }

    /**
     * Adds a possible state to the state machine.
     * @param state The state to add.
     */
    public void addState(T state) {
        states.add(state);
    }

    /**
     * Called on each frame.
     * @param delta The time in seconds since the last frame.
     */
    public void update(float delta) {
        if (currentState == null) return ;
        currentState.update(delta);
    }
    
    /**
     * Called on each frame.
     * @param batch The sprite batch.
     */
    public void render(SpriteBatch batch) {
        if (currentState == null) return;
        currentState.render(batch);
    }

    /**
     * Renders visual effects after the main render call.
     * @param batch The sprite batch.
     */
    public void renderEffects(SpriteBatch batch) {
        if (currentState == null) return;
        currentState.renderEffects(batch);
    }

    /**
     * Checks whether a state transition is allowed.
     * @param args The transition arguments.
     */
    public void stateTransitionCheck(TransitionArgs<?> args) {
        if (!args.prevState.equals(currentState.getName()) || !stateExists(args.nextState)) {
            return ;
        }

        // System.out.println("Transition from " + args.prevState + " to " + args.nextState);
        stateTransition(currentState, getState(args.nextState), args.args);
        
    }

    /**
     * Checks if a possible state has the given name.
     * @param name The name to check.
     * @return True if a state with the given name exists, false otherwise.
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
     * Gets a state based on its name.
     * @param name The name of the state.
     * @return The state (or null).
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
     * Transitions between two states.
     * @param <V> The type of state arguments.
     * @param prevState The previous state.
     * @param nextState The next state.
     * @param args The transition arguments.
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
     * Sets the callback.
     * @param callback The callback to set.
     */
    public void setCallback(BiConsumer<T, T> callback) {
        this.callback = callback;
    }

}