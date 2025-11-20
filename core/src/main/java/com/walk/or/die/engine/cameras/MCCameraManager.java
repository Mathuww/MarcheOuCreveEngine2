package com.walk.or.die.engine.cameras;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.walk.or.die.engine.exceptions.UndefinedBehaviorException;

public class MCCameraManager {
    private static MCCameraManager instance;

    public static MCCameraManager get() {
        if (instance == null) instance = new MCCameraManager();
        return instance;
    }

    private OrthographicCamera gdxCam;

    private Map<MCCameraMode, MCCameraBehavior> behaviors = new HashMap<>();
    private MCCameraMode mode;

    private float limitX;
    private float limitY;

    public float getLimitX() {
        return this.limitX;
    }

    public void setLimitX(float limitX) {
        this.limitX = limitX;
    }

    public float getLimitY() {
        return this.limitY;
    }

    public void setLimitY(float limitY) {
        this.limitY = limitY;
    }

    private MCCameraManager() {}

    public void register(MCCameraMode mode, MCCameraBehavior behavior) {
        behaviors.put(mode, behavior);
    }

    public void setMode(MCCameraMode mode) {
        this.mode = mode;
    }

    public void init(float viewportWidth, float viewportHeight) {
        gdxCam = new OrthographicCamera(viewportWidth, viewportHeight);
        gdxCam.position.set(viewportWidth / 2, viewportHeight / 2, 0);
        limitX = 0f;
        limitY = 0f;
    }

    public OrthographicCamera getGdxCam() {
        return gdxCam;
    }

    public void update(float delta) throws UndefinedBehaviorException {
        if (mode == null) throw new UndefinedBehaviorException("camera update : need a behavior to update");
        behaviors.get(mode).update(gdxCam, delta);
        gdxCam.update();
    }
}
