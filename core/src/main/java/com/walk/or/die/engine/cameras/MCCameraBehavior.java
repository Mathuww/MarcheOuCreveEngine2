package com.walk.or.die.engine.cameras;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

/**
 * The abstract behavior class.
 */
public abstract class MCCameraBehavior {
    /**
     * Call each frame.
     * @param gdxCam
     * @param delta
     * @return 
     */
    public abstract Vector2 update(OrthographicCamera gdxCam, float delta);

    /**
     * Call when we start the behavior.
     */
    public abstract void enter();

    /**
     * Call when we quit this behavior.
     */
    public abstract void exit();
}
