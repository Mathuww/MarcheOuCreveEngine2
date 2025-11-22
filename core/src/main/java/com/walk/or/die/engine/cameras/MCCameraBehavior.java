package com.walk.or.die.engine.cameras;

import com.badlogic.gdx.graphics.OrthographicCamera;

public abstract class MCCameraBehavior {
    public abstract void update(OrthographicCamera gdxCam, float delta);
    public abstract void enter();
    public abstract void exit();
}
