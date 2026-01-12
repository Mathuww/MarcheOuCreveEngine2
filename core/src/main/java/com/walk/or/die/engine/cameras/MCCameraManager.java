package com.walk.or.die.engine.cameras;

import java.text.NumberFormat.Style;
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
import com.walk.or.die.engine.shared.MCIntVector2;
import com.walk.or.die.engine.shared.OpenSimplex2S;

/**
 * The singleton class which manages the camera system.
 */
public class MCCameraManager {
    /**
     * Enumerates the available camera modes.
     */
    public static enum CameraMode {
        FOLLOW,
        ARROWS
    }

    private static MCCameraManager instance;

    /**
     * Gets the singleton instance.
     * @return the singleton instance.
     */
    public static MCCameraManager get() {
        if (instance == null) instance = new MCCameraManager();
        return instance;
    }

    /**
     * The libGDX camera object.
     */
    private OrthographicCamera gdxCam;
    /**
     * Used to check if the camera moved during a frame.
     */
    private float oldX, oldY;

    /**
     * Contains the possible behaviors.
     */
    private Map<CameraMode, MCCameraBehavior> behaviors = new HashMap<>();
    /**
     * Current mode.
     */
    private CameraMode mode;

    /**
     * Contains the current lower left limit.
     */
    private Vector2 lowerLimit = new Vector2(0f, 0f);
    /**
     * Contains the current upper right limit.
     */
    private Vector2 upperLimit = new Vector2(16f, 16f); // arbitraire

    /**
     * True if the camera moved this frame.
     */
    private boolean movedThisFrame = false;

    /**
     * Contains the current camera target. Especially useful for FollowCamBehavior.
     */
    private MCEntity target;

    /**
     * Current camera shake trauma (intensity level), between 0 and 1.
     */
    private float trauma = 0f; 
    /**
     * Used to count time since the camera shake started.
     */
    private float stateTime = 0f;
    /**
     * Trauma decay (trauma units/sec)
     */
    private final float TRAUMA_DECAY = 0.5f;
    /**
     * Max. camera shake rotation angle (degrees)
     */
    private final float SHAKE_MAX_ANGLE = 15f; 
    /**
     * Max. camera shake translation offset (tiles)
     */
    private final float SHAKE_MAX_OFFSET = 1.5f;
    /**
     * Simplex noise speed used for camera shake.
     * Directly affects how intense it will look for all damage taken on screen.
     */
    private final float SHAKE_NOISE_SPEED = 20f;

    /**
     * Zoom interpolation constant
     */
    public final float ZOOM_LERP = 5f;
    /**
     * Zoom level delta per wheel notch.
     */
    public final float ZOOM_STEP = 0.085f;
    /**
     * Min. zoom level
     */
    public final float ZOOM_MIN = 0.25f;
    /**
     * Max. zoom level
     */
    public final float ZOOM_MAX = 1.25f;
    /**
     * Used for "zoom to mouse" effect
     */
    private Vector3 zoomTargetPos = new Vector3();
    /**
     * Used for zoom interpolation.
     */
    private float zoomTarget;

    private final MCEventBus bus = MCEventBus.get();

    private MCCameraManager() {
        register(CameraMode.FOLLOW, new MCFollowCamBehavior());
        register(CameraMode.ARROWS, new MCArrowsCamBehavior());
    }

    /**
     * Gets the global lower limit of the camera.
     * @return the lower limit vector.
     */
    public Vector2 getGlobalLowerLimit() {
        return this.lowerLimit;
    }

    /**
     * Sets the global lower limit of the camera.
     * @param limit the new lower limit coordinates.
     */
    public void setLowerLimit(Vector2 limit) {
        this.lowerLimit = limit;
    }

    /**
     * Gets the global upper limit of the camera.
     * @return the upper limit vector.
     */
    public Vector2 getGlobalUpperLimit() {
        return this.upperLimit;
    }

    /**
     * Sets the global upper limit of the camera.
     * @param limit the new upper limit coordinates.
     */
    public void setUpperLimit(Vector2 limit) {
        this.upperLimit = limit;
    }

    /**
     * Gets the current camera's position.
     * @return the position vector.
     */
    public Vector2 getPosition() {
        return new Vector2(gdxCam.position.x, gdxCam.position.y);
    }

    /**
     * Sets the camera's position.
     * @param pos the new position vector.
     */
    public void setPosition(Vector2 pos) {
        gdxCam.position.x = pos.x;
        gdxCam.position.y = pos.y;
    }

    /**
     * Used to begin interpolating to the desired position.
     * Delegates the logic to the current behavior.
     * @param pos the target position.
     */
    public void interpolateTo(Vector2 pos) {
        if (mode != null)
            behaviors.get(mode).interpolateTo(pos);
    }

    /**
     * Registers a camera behavior associated with a specific mode.
     * @param mode the mode to associate with the behavior.
     * @param behavior the behavior to execute for this mode.
     */
    public void register(CameraMode mode, MCCameraBehavior behavior) {
        behaviors.put(mode, behavior);
    }

    /**
     * Gets the current camera mode.
     * @return the active camera mode.
     */
    public CameraMode getMode() {
        return this.mode;
    }

    /**
     * Sets the active camera mode.
     * @param mode the new mode to activate.
     */
    public void setMode(CameraMode mode) {
        if (this.mode != null) behaviors.get(this.mode).exit();
        this.mode = mode;
        behaviors.get(this.mode).enter();
    }

    /**
     * Initializes the camera manager.
     * @param viewportWidth the width of the camera viewport.
     * @param viewportHeight the height of the camera viewport.
     * @param mode the initial camera mode.
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
     * Gets the x-coordinate of the top-right viewport corner.
     * @return the x-coordinate.
     */
    public float getCurrentUpperLimitX() {
        return gdxCam.position.x + gdxCam.viewportWidth / 2;
    }

    /**
     * Gets the y-coordinate of the top-right viewport corner.
     * @return the y-coordinate.
     */
    public float getCurrentUpperLimitY() {
        return gdxCam.position.y + gdxCam.viewportHeight / 2;
    }

    /**
     * Gets the x-coordinate of the bottom-left viewport corner.
     * @return the x-coordinate.
     */
    public float getCurrentLowerLimitX() {
        return gdxCam.position.x - gdxCam.viewportWidth / 2;
    }

    /**
     * Gets the y-coordinate of the bottom-left viewport corner.
     * @return the y-coordinate.
     */
    public float getCurrentLowerLimitY() {
        return gdxCam.position.y - gdxCam.viewportHeight / 2;
    }

    /**
     * Gets the underlying LibGDX OrthographicCamera.
     * @return the OrthographicCamera instance.
     */
    public OrthographicCamera getGdxCam() {
        return gdxCam;
    }

    /**
     * Gets the entity currently being followed by the camera.
     * @return the target entity.
     */
    public MCEntity getFollowTarget() {
        return this.target;
    }

    /**
     * Sets the entity to be followed by the camera.
     * @param target the entity to follow.
     */
    public void setFollowTarget(MCEntity target) {
        this.target = target;
    }

    /**
     * Adds trauma to the camera to induce shaking.
     * @param traumaAddition the amount of trauma to add.
     */
    public void addTrauma(float traumaAddition) {
        //System.out.println("adding " + traumaAddition + " trauma");
        trauma = MathUtils.clamp(trauma + traumaAddition, 0f, 1f);
    }

    /**
     * Updates the camera logic. Called each frame.
     * @param delta the time elapsed since the last frame.
     * @throws UnexistingBehaviorException if no behavior is associated with the current mode.
     */
    public void update(float delta) throws UnexistingBehaviorException {
        movedThisFrame = false;
        if (mode == null) 
            throw new UnexistingBehaviorException("camera update : need a behavior to update");

        stateTime += delta;

        if (Math.abs(trauma - 0f) > 0.01f) {
            //System.out.println("trauma is now " + trauma);
            // restaurer l'état "stable" de la caméra (0 translation, 0 rotation)
            gdxCam.position.x = oldX;
            gdxCam.position.y = oldY;
            gdxCam.up.set(0, 1, 0); // rotation 0 (on peut pas direct setRotation(0f))
            gdxCam.direction.set(0, 0, -1);
            
            float shake = trauma * trauma; // trauma² (+ réaliste, + kiffant)
            float t = stateTime * SHAKE_NOISE_SPEED;

            float angleNoise = OpenSimplex2S.noise2_ImproveX(0f, t, 0f);
            float noiseX = OpenSimplex2S.noise2_ImproveX(100f, t, 0f);
            float noiseY = OpenSimplex2S.noise2_ImproveX(200f, t, 0f);
        
            gdxCam.rotate(angleNoise * shake * SHAKE_MAX_ANGLE);
            gdxCam.position.x += noiseX * shake * SHAKE_MAX_OFFSET;
            gdxCam.position.y += noiseY * shake * SHAKE_MAX_OFFSET;

            trauma -= delta * TRAUMA_DECAY;
            if (Math.abs(trauma - 0f) < 0.01f)
                trauma = 0f; // snap pour etre sur qu'on retombe à ZERO trauma
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
     * Checks if the camera has moved during the current frame.
     * @return true if the camera has moved, false otherwise.
     */
    public boolean hasMovedThisFrame() {
        return this.movedThisFrame;
    }

    /**
     * Delegates input pressed logic to the current behavior.
     * @param cmd the input command.
     */
    public void inputPressed(Command cmd) {
        if (mode != null)
            behaviors.get(mode).handleInputPressed(gdxCam, cmd);
    }

    /**
     * Delegates input released logic to the current behavior.
     * @param cmd the input command.
     */
    public void inputReleased(Command cmd) {
        if (mode != null)
            behaviors.get(mode).handleInputReleased(cmd);
    }
}