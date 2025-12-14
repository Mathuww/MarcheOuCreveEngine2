package com.walk.or.die.engine.cameras;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.exceptions.MissingDataException;
import com.walk.or.die.engine.exceptions.UnexistingBehaviorException;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.input.MCInputManager.CameraPanCommand;
import com.walk.or.die.engine.input.MCInputManager.CameraZoomCommand;
import com.walk.or.die.engine.input.MCInputManager.Command;
import com.walk.or.die.engine.shared.MCEventBus;

/**
 * The singleton class which manages the camera.
 */
public class MCCameraManager {
    /**
     * An enum for camera mode.
     */
    public static enum CameraMode {
        FOLLOW,
        ARROWS
    }

    private static MCCameraManager instance;

    /**
     * The getter.
     * @return
     */
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
    private boolean shaking = false;

    public final float ZOOM_LERP = 8f;
    public final float ZOOM_STEP = 0.085f;
    public final float ZOOM_MIN = 0.75f;
    public final float ZOOM_MAX = 1.25f;
    private Vector3 zoomTargetPos = new Vector3();
    private float zoomTarget;

    private final MCEventBus bus = MCEventBus.get();

    private MCCameraManager() {
        register(CameraMode.FOLLOW, new MCFollowCamBehavior());
        register(CameraMode.ARROWS, new MCArrowsCamBehavior());
    }

    /**
     * Get the lower limit of the camera.
     * @return 
     */
    public Vector2 getGlobalLowerLimit() {
        return this.lowerLimit;
    }

    /**
     * Set the lower limit of the camera.
     * @param limit
     */
    public void setLowerLimit(Vector2 limit) {
        this.lowerLimit = limit;
    }

    /**
     * Get the upper limit of the camera.
     * @return 
     */
    public Vector2 getGlobalUpperLimit() {
        return this.upperLimit;
    }

    /**
     * Set the upper limit of the camera.
     * @param limit
     */
    public void setUpperLimit(Vector2 limit) {
        this.upperLimit = limit;
    }

    /**
     * Get the current camera's position
     * @return
     */
    public Vector2 getPosition() {
        return new Vector2(gdxCam.position.x, gdxCam.position.y);
    }

    /**
     * Set the camera's position.
     * @param pos
     */
    public void setPosition(Vector2 pos) {
        gdxCam.position.x = pos.x;
        gdxCam.position.y = pos.y;
    }

    /**
     * Add a behavior and the associated mode in the manager.
     * @param mode
     * @param behavior
     */
    public void register(CameraMode mode, MCCameraBehavior behavior) {
        behaviors.put(mode, behavior);
    }

    /**
     * Get the current camera's mode.
     * @return 
     */
    public CameraMode getMode() {
        return this.mode;
    }

    /**
     * Set the camera's mode.
     * @param mode
     */
    public void setMode(CameraMode mode) {
        if (this.mode != null) behaviors.get(this.mode).exit();
        this.mode = mode;
        behaviors.get(this.mode).enter();
    }

    /**
     * Init the manager.
     * @param viewportWidth
     * @param viewportHeight
     * @param mode
     */
    public void init(float viewportWidth, float viewportHeight, CameraMode mode) {
        gdxCam = new OrthographicCamera(viewportWidth, viewportHeight);
        gdxCam.position.set(viewportWidth / 2, viewportHeight / 2, 0);
        oldX = gdxCam.position.x;
        oldY = gdxCam.position.y;
        setMode(mode);
        bus.on(this, "InputPressed", this::inputPressed);
        bus.on(this, "InputReleased", this::inputReleased);
    }

    /**
     * Get the x-coordinate of the top-right point.
     * @return
     */
    public float getCurrentUpperLimitX() {
        return gdxCam.position.x + gdxCam.viewportWidth / 2;
    }

    /**
     * Get the y-coordinate of the top-right point.
     * @return 
     */
    public float getCurrentUpperLimitY() {
        return gdxCam.position.y + gdxCam.viewportHeight / 2;
    }

    /**
     * Get the x-coordinate of the bottom-left point.
     * @return 
     */
    public float getCurrentLowerLimitX() {
        return gdxCam.position.x - gdxCam.viewportWidth / 2;
    }

    /**
     * Get the y-coordinate of the bottom-left point.
     * @return 
     */
    public float getCurrentLowerLimitY() {
        return gdxCam.position.y - gdxCam.viewportHeight / 2;
    }

    /**
     * Get the camera.
     * @return 
     */
    public OrthographicCamera getGdxCam() {
        return gdxCam;
    }

    /**
     * Get the camera's target.
     * @return 
     */
    public MCEntity getFollowTarget() {
        return this.target;
    }

    /**
     * Set the camera's target.
     * @param target
     * @throws MissingDataException
     */
    public void setFollowTarget(MCEntity target) throws MissingDataException{
        if (target == null) {
            throw new MissingDataException("Missing Entity");
        }
        this.target = target;
    }

    /**
     * Useful function to shake, use it without moderation.
     * @param intensity
     * @param duration
     */
    public void shake(float intensity, float duration) {
        shaking = true;
        this.shakeIntensity = intensity;
        this.shakeDuration = duration;
        this.shakeStateTime = 0f;
    }

    /**
     * Call each frame.
     * @param delta
     * @throws UnexistingBehaviorException
     */
    public void update(float delta) throws UnexistingBehaviorException {
        movedThisFrame = false;
        if (mode == null) 
            throw new UnexistingBehaviorException("camera update : need a behavior to update");

        float offsetX = 0f, offsetY = 0f;
        if (shaking) {
            if (shakeStateTime < shakeDuration) {
                shakeStateTime += delta;
                
                float currentPower = shakeIntensity * ((shakeDuration - shakeStateTime) / shakeDuration);
            
                offsetX = MathUtils.randomTriangular(-1f, 1f, 0f) * currentPower;
                offsetY = MathUtils.randomTriangular(-1f, 1f, 0f) * currentPower;

                gdxCam.translate(offsetX, offsetY);
            } else {
                gdxCam.position.x = oldX;
                gdxCam.position.y = oldY;
                shaking = false;
            }
        } else {
            behaviors.get(mode).update(gdxCam, delta);

            if (gdxCam.position.x != oldX || gdxCam.position.y != oldY)
                movedThisFrame = true;
            
            oldX = gdxCam.position.x;
            oldY = gdxCam.position.y;
        }

        gdxCam.update();
    }

    /**
     * Return if the camera has moved.
     * @return 
     */
    public boolean hasMovedThisFrame() {
        return this.movedThisFrame;
    }

    public void inputPressed(Command cmd) {
        if (mode != null)
            behaviors.get(mode).handleInputPressed(gdxCam, cmd);
    }

    public void inputReleased(Command cmd) {
        if (mode != null)
            behaviors.get(mode).handleInputReleased(cmd);
    }
}
