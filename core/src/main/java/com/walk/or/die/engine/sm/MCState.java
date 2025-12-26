package com.walk.or.die.engine.sm;

import java.util.function.Consumer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.walk.or.die.engine.cameras.MCCameraManager;
import com.walk.or.die.engine.shared.MCEventBus;
import com.walk.or.die.engine.ui.MCHUDManager;

/**
 * Abstract class for state.
 * @param <T> type of arguments' state
 */
public abstract class MCState<T extends MCState.StateArgs> {

    /**
     * Class of the args needed by the state to start.
     */
    public static class StateArgs {}

    /**
     * The MCCameraManager.
     * @see MCCameraManager
     */
    protected final MCCameraManager camManager = MCCameraManager.get();

    /**
     * The MCHUDManager.
     * @see MCHUDManager
     */
    protected final MCHUDManager hudManager = MCHUDManager.get();

    /**
     * Name of the state.
     */
    protected String name;

    /**
     * The EventBus.
     * @see MCEventBus
     */
    protected MCEventBus bus;

    /**
     * Constructor
     */
    public MCState() {
        bus = MCEventBus.get();
    }

    /**
     * Getter name of state
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @see MCStateMachine
     * @param delta
     */
    public abstract void update(float delta);

    /**
     * @see MCStateMachine
     * @param batch
     */
    public abstract void render(SpriteBatch batch);

    /**
     * @see MCStateMachine
     * @param batch
     */
    public abstract void renderEffects(SpriteBatch batch);

    /**
     * Call when the state starts, become the current state.
     * @param args
     */
    public abstract void enter(T args);

    /**
     * Call when the state ends, loses the current state.
     */
    public abstract void exit();

    /**
     * Connect easely a method to a event.
     * @param <U>
     * @param eventName
     * @param listener
     */
    protected <U> void listen(String eventName, Consumer<U> listener) {
        bus.on(this, eventName, listener);
    }

    /**
     * Change the state, and call a new one with its needed argument.
     * @param newState
     * @param args
     */
    protected abstract void changeState(String newState, StateArgs args);
}