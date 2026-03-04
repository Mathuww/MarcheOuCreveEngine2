package com.walk.or.die.engine.cameras;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.input.MCInputManager.CameraPanCommand;
import com.walk.or.die.engine.input.MCInputManager.CameraZoomCommand;
import com.walk.or.die.engine.input.MCInputManager.Command;
import com.walk.or.die.engine.input.MCInputManager.DirectionalCommand;
import com.walk.or.die.engine.shared.MCEventBus;
import com.walk.or.die.engine.shared.MCIntVector2;

/**
 * Represents a behavioral class that allows the player to control the camera with the arrows.
 */
public class MCArrowsCamBehavior extends MCCameraBehavior {
    /** The camera movement speed. */
    private final float CAM_MOVE_SPEED = 0.05f;
    /** The camera linear interpolation factor. */
    private final float CAM_LERP = 3f;
    /** The target X coordinate for camera interpolation. */
    private float targetX = 0f;
    /** The target Y coordinate for camera interpolation. */
    private float targetY = 0f;
    /** Indicates if the camera is currently translating. */
    private boolean translating = false;

    /** The entity the camera targets. */
    private MCEntity target;
    /** The target zoom level for the camera. */
    private Float targetZoom;

    /** A map storing the current state of directional inputs. */
    private Map<MCIntVector2, Boolean> currentInput;

    /**
     * Constructs a new instance of the arrow camera behavior.
     */
    public MCArrowsCamBehavior() {
        currentInput = new HashMap<>();
    }

    /**
     * Called at entrance.
     */
    @Override
    public void enter() {
        int[][] directions = {
            {0, +1}, {0, -1},
            {+1, 0}, {-1, 0}
        };
        for (int[] dir : directions) {
            currentInput.put(new MCIntVector2(dir[0], dir[1]), false);
        }
        MCEventBus bus = MCEventBus.get();
    }

    /**
     * Called at exit.
     */
    @Override
    public void exit() {}

    /**
     * Handles an input press event.
     * @param gdxCam The LibGDX OrthographicCamera.
     * @param data The input command data.
     */
    @Override
    public void handleInputPressed(OrthographicCamera gdxCam, Command data) {
        if (translating)
            return;

        MCCameraManager camManager = MCCameraManager.get();
        if(data instanceof DirectionalCommand cmd) {
            currentInput.put(cmd.getIntVect(), true);
        } else if (data instanceof CameraZoomCommand zoomCmd) {
            //System.out.println("received zoom cdm");
            targetZoom = gdxCam.zoom + camManager.ZOOM_STEP * zoomCmd.scrollDelta;
            targetZoom = MathUtils.clamp(targetZoom, camManager.ZOOM_MIN, camManager.ZOOM_MAX);
        } else if (data instanceof CameraPanCommand panCmd) {
            //System.out.println("received pan cdm");
            float targetX = gdxCam.position.x - panCmd.deltaX;
            float targetY = gdxCam.position.y - panCmd.deltaY;
            Vector2 lowerLimit = camManager.getGlobalLowerLimit();
            Vector2 upperLimit = camManager.getGlobalUpperLimit();

            gdxCam.position.x = MathUtils.clamp(
                targetX,
                lowerLimit.x,
                upperLimit.x
            );
            gdxCam.position.y = MathUtils.clamp(
                targetY,
                lowerLimit.y,
                upperLimit.y
            );
            gdxCam.update();
        }
    }

    /**
     * Handles an input release event.
     * @param data The input command data.
     */
    public void handleInputReleased(Command data) {
        if (data instanceof DirectionalCommand cmd) {
            currentInput.put(cmd.getIntVect(), false);
        } 
    }

    /**
     * Moves the camera to the target position by interpolating.
     * @param pos The target position.
     */
    @Override
    public void interpolateTo(Vector2 pos) {
        targetX = pos.x;
        targetY = pos.y;
        translating = true;
    }

    /**
     * Called on each frame.
     * @param gdxCam The LibGDX OrthographicCamera.
     * @param delta The time since the last frame (in seconds).
     */
    @Override
    public void update(OrthographicCamera gdxCam, float delta) {
        MCCameraManager camManager = MCCameraManager.get();

        if (translating) {
            if (Math.abs(targetX - gdxCam.position.x) > 0.01f
                || Math.abs(targetX - gdxCam.position.x) > 0.01f) {
                gdxCam.position.x += (targetX - gdxCam.position.x) * delta * CAM_LERP;
                gdxCam.position.y += (targetY - gdxCam.position.y) * delta * CAM_LERP;
            } else {
                gdxCam.position.x = targetX;
                gdxCam.position.y = targetY;
                translating = false;
            }

            gdxCam.update();
            return;
        }

        float camHalfWidth = gdxCam.viewportWidth / 2;
        float camHalfHeight = gdxCam.viewportHeight / 2;
        Vector2 lowerLimit = camManager.getGlobalLowerLimit();
        Vector2 upperLimit = camManager.getGlobalUpperLimit();

        // zoom
        if (targetZoom != null && Math.abs(targetZoom - gdxCam.zoom) > 0.001f) {
            Vector3 posBeforeZoom = MCInputManager.get().askWorldMousePos();
            gdxCam.zoom += (targetZoom - gdxCam.zoom) * delta * camManager.ZOOM_LERP;
            gdxCam.update();
            // ici on zoome en restant centré sur la souris, pas comme des gros ploucs au centre de l'écran
            Vector3 posAfterZoom = MCInputManager.get().askWorldMousePos(); // le Input Manager, on lui "get" pas, on lui DEMANDE OH
            float driftX = posBeforeZoom.x - posAfterZoom.x;
            float driftY = posBeforeZoom.y - posAfterZoom.y;
            gdxCam.position.x += driftX;
            gdxCam.position.y += driftY;

            float minX = lowerLimit.x + camHalfWidth;
            float maxX = upperLimit.x - camHalfWidth;
            
            if (minX > maxX) {
                gdxCam.position.x = (lowerLimit.x + upperLimit.x) / 2f;
            } else {
                gdxCam.position.x = MathUtils.clamp(gdxCam.position.x, minX, maxX);
            }

            float minY = lowerLimit.y + camHalfHeight;
            float maxY = upperLimit.y - camHalfHeight;

            if (minY > maxY) {
                gdxCam.position.y = (lowerLimit.y + upperLimit.y) / 2f;
            } else {
                gdxCam.position.y = MathUtils.clamp(gdxCam.position.y, minY, maxY);
            }

            gdxCam.update(); 
        }

        Vector2 relativeMove = new Vector2(0, 0);
            
        for (Map.Entry<MCIntVector2, Boolean> entry : currentInput.entrySet()) {
            if (entry.getValue()) { // true
                MCIntVector2 cmd = entry.getKey();
                relativeMove.x += cmd.x;
                relativeMove.y += cmd.y;
            }
        }
        if (relativeMove.len() > 0) relativeMove.nor();

        relativeMove.x = relativeMove.x * CAM_MOVE_SPEED;
        relativeMove.y = relativeMove.y * CAM_MOVE_SPEED;

        gdxCam.position.x += relativeMove.x;
        gdxCam.position.y += relativeMove.y;

        float minX = lowerLimit.x + camHalfWidth;
        float maxX = upperLimit.x - camHalfWidth;
        
        if (minX > maxX) {
            gdxCam.position.x = (lowerLimit.x + upperLimit.x) / 2f;
        } else {
            gdxCam.position.x = MathUtils.clamp(gdxCam.position.x, minX, maxX);
        }

        float minY = lowerLimit.y + camHalfHeight;
        float maxY = upperLimit.y - camHalfHeight;

        if (minY > maxY) {
            gdxCam.position.y = (lowerLimit.y + upperLimit.y) / 2f;
        } else {
            gdxCam.position.y = MathUtils.clamp(gdxCam.position.y, minY, maxY);
        }
    }
}