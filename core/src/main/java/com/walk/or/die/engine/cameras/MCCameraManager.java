package com.walk.or.die.engine.cameras;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.exceptions.UnexistingBehaviorException;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.shared.MCEventBus;

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
    float oldX, oldY;

    private Map<CameraMode, MCCameraBehavior> behaviors = new HashMap<>();
    private CameraMode mode;

    private Vector2 lowerLimit = new Vector2(0f, 0f);
    private Vector2 upperLimit = new Vector2(16f, 16f); // arbitraire

    private boolean movedThisFrame = false;

    private MCEntity target;

    private float shakeDuration = 0f;  
    private float shakeStateTime = 0f;   
    private float shakeIntensity = 0f;

    private MCCameraManager() {
        register(CameraMode.FOLLOW, new MCFollowCamBehavior());
        register(CameraMode.ARROWS, new MCArrowsCamBehavior());
    }

    public Vector2 getGlobalLowerLimit() {
        return this.lowerLimit;
    }

    public void setLowerLimit(Vector2 limit) {
        this.lowerLimit = limit;
    }

    public Vector2 getGlobalUpperLimit() {
        return this.upperLimit;
    }

    public void setUpperLimit(Vector2 limit) {
        this.upperLimit = limit;
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
        oldX = gdxCam.position.x;
        oldY = gdxCam.position.y;
        setMode(mode);
    }

    public float getCurrentUpperLimitX() {
        return gdxCam.position.x + gdxCam.viewportWidth / 2;
    }

    public float getCurrentUpperLimitY() {
        return gdxCam.position.y + gdxCam.viewportHeight / 2;
    }

    /* à enlever si on s'en est pas servi */
    public float getCurrentLowerLimitX() {
        return gdxCam.position.x - gdxCam.viewportWidth / 2;
    }

    public float getCurrentLowerLimitY() {
        return gdxCam.position.y - gdxCam.viewportHeight / 2;
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

    public void shake(float intensity, float duration) {
        this.shakeIntensity = intensity;
        this.shakeDuration = duration;
        this.shakeStateTime = 0f;
    }

    public void update(float delta) throws UnexistingBehaviorException {
        movedThisFrame = false;
        if (mode == null) throw new UnexistingBehaviorException("camera update : need a behavior to update");
        Vector2 newPos = behaviors.get(mode).update(gdxCam, delta);

        if (newPos.x != oldX || newPos.y != oldY)
            movedThisFrame = true;
        gdxCam.position.x = newPos.x;
        gdxCam.position.y = newPos.y;
        oldX = gdxCam.position.x;
        oldY = gdxCam.position.y;

        float offsetX = 0f, offsetY = 0f;
        if (shakeStateTime <= shakeDuration) {
            shakeStateTime += delta;
            
            float currentPower = shakeIntensity * ((shakeDuration - shakeStateTime) / shakeDuration);
        
            offsetX = MathUtils.randomTriangular(-1f, 1f, 0f) * currentPower;
            offsetY = MathUtils.randomTriangular(-1f, 1f, 0f) * currentPower;

            gdxCam.translate(offsetX, offsetY);
        }

        gdxCam.update();
    }

    public boolean hasMovedThisFrame() {
        return this.movedThisFrame;
    }
}
