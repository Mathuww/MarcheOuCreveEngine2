package com.walk.or.die.engine.cameras;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

public abstract class MCCameraBehavior {
    public abstract Vector2 update(OrthographicCamera gdxCam, float delta);
    public abstract void enter();
    public abstract void exit();
}
