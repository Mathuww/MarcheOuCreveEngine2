package com.walk.or.die.engine.cameras;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.walk.or.die.engine.MCEventBus;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.exceptions.UnexistingBehaviorException;

public class MCCameraManager {
    public static enum CameraMode {
        FOLLOW,
        ARROWS
    }

    private static MCCameraManager instance;

    public static MCCameraManager get() {
        if (instance == null) instance = new MCCameraManager();
        return instance;
    }

    private OrthographicCamera gdxCam;

    private Map<CameraMode, MCCameraBehavior> behaviors = new HashMap<>();
    private CameraMode mode;

    private float limitX;
    private float limitY;

    private MCEntity target;

    private MCCameraManager() {
        register(CameraMode.FOLLOW, new MCFollowCamBehavior());
        register(CameraMode.ARROWS, new MCArrowsCamBehavior());
    }

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

    public Vector2 getPosition() {
        return new Vector2(gdxCam.position.x, gdxCam.position.y);
    }

    public void setPosition(Vector2 pos) {
        gdxCam.position.x = pos.x;
        gdxCam.position.y = pos.y;
    }

    public void register(CameraMode mode, MCCameraBehavior behavior) {
        behaviors.put(mode, behavior);
    }

    public CameraMode getMode() {
        return this.mode;
    }

    public void setMode(CameraMode mode) {
        if (this.mode != null) behaviors.get(this.mode).exit();
        this.mode = mode;
        behaviors.get(this.mode).enter();
    }

    public void init(float viewportWidth, float viewportHeight, CameraMode mode) {
        gdxCam = new OrthographicCamera(viewportWidth, viewportHeight);
        gdxCam.position.set(viewportWidth / 2, viewportHeight / 2, 0);
        limitX = 0f;
        limitY = 0f;
        setMode(mode);
    }

    public OrthographicCamera getGdxCam() {
        return gdxCam;
    }

    public MCEntity getFollowTarget() {
        return this.target;
    }

    public void setFollowTarget(MCEntity target) {
        this.target = target;
    }

    public void update(float delta) throws UnexistingBehaviorException {
        if (mode == null) throw new UnexistingBehaviorException("camera update : need a behavior to update");
        behaviors.get(mode).update(gdxCam, delta);
        gdxCam.update();
    }
}
