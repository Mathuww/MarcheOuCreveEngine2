package com.walk.or.die.engine.cameras;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.input.MCInputManager.Command;
import com.walk.or.die.engine.shared.MCEventBus;

/**
 * The abstract behavior class.
 */
public abstract class MCCameraBehavior {
    private final MCEventBus bus = MCEventBus.get();

    /**
     * Call each frame.
     * @param gdxCam
     * @param delta
     */
    public abstract void update(OrthographicCamera gdxCam, float delta);

    /**
     * Call when we start the behavior.
     */
    public abstract void enter();

    /**
     * Call when we quit this behavior.
     */
    public abstract void exit();

    public abstract void handleInputPressed(OrthographicCamera gdxCam, Command cmd);
    public abstract void handleInputReleased(Command cmd);
}
