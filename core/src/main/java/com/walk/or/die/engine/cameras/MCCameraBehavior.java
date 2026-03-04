package com.walk.or.die.engine.cameras;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.input.MCInputManager.Command;
import com.walk.or.die.engine.shared.MCEventBus;
import com.walk.or.die.engine.shared.MCIntVector2;

/**
 * Represents the abstract base class for camera behaviors.
 */
public abstract class MCCameraBehavior {
    /**
     * Represents the event bus instance.
     */
    private final MCEventBus bus = MCEventBus.get();

    /**
     * Updates the camera behavior. Called each frame.
     * @param gdxCam The LibGDX OrthographicCamera.
     * @param delta The time elapsed since the last frame.
     */
    public abstract void update(OrthographicCamera gdxCam, float delta);

    /**
     * Called at state entrance.
     */
    public abstract void enter();

    /**
     * Called at state exit.
     */
    public abstract void exit();

    /**
     * Updates the camera based on the provided input pressed command.
     * @param gdxCam The camera to update.
     * @param cmd The input command.
     */
    public abstract void handleInputPressed(OrthographicCamera gdxCam, Command cmd);
    /**
     * Updates the camera based on the provided input released command.
     * @param cmd The input command.
     */
    public abstract void handleInputReleased(Command cmd);

    /**
     * Moves to the target position by interpolating.
     * @param pos The target position.
     */
    public void interpolateTo(Vector2 pos) {
    }
}