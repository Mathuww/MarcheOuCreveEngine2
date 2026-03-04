package com.walk.or.die.engine.sm;

import java.util.function.Consumer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.walk.or.die.engine.cameras.MCCameraManager;
import com.walk.or.die.engine.shared.MCEventBus;
import com.walk.or.die.engine.ui.MCHUDManager;

/**
 * Abstract class for a state.
 * @param <T> The type of arguments for the state.
 */
public abstract class MCState<T extends MCState.StateArgs> {

    /**
     * Class of the arguments needed by the state to start.
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
     * The name of the state.
     */
    protected String name;

    /**
     * The EventBus.
     * @see MCEventBus
     */
    protected MCEventBus bus;

    /**
     * Initializes a new instance of the {@code MCState} class.
     */
    public MCState() {
        bus = MCEventBus.get();
    }

    /**
     * Gets the name of the state.
     * @return The name of the state.
     */
    public String getName() {
        return name;
    }

    /**
     * Called on each frame.
     * @param delta The time delta.
     */
    public abstract void update(float delta);

    /**
     * Called on each frame.
     * @param batch The sprite batch.
     */
    public abstract void render(SpriteBatch batch);

    /**
     * Renders visual effects.
     * @param batch The sprite batch.
     */
    public abstract void renderEffects(SpriteBatch batch);

    /**
     * Called at state entrance.
     * @param args The arguments.
     */
    public abstract void enter(T args);

    /**
     * Called at state exit.
     */
    public abstract void exit();

    /**
     * Connects easily a method to an event.
     * @param <U> The type of the listener.
     * @param eventName The name of the event.
     * @param listener The listener.
     */
    protected <U> void listen(String eventName, Consumer<U> listener) {
        bus.on(this, eventName, listener);
    }

    /**
     * Changes the state, and calls a new one with its needed argument.
     * @param newState The new state.
     * @param args The state arguments.
     */
    protected abstract void changeState(String newState, StateArgs args);
}