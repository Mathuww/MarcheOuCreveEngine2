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
 * Behavior class which allows player to control the camera with the arrows.
 */
public class MCArrowsCamBehavior extends MCCameraBehavior {
    private final float CAM_MOVE_SPEED = 0.05f;
    // pour translation vers point avant enemies playing
    private final float CAM_LERP = 3f;
    private float targetX = 0f, targetY = 0f;
    private boolean translating = false;

    private MCEntity target;
    private Float targetZoom;

    private Map<MCIntVector2, Boolean> currentInput;

    /**
     * The constructor.
     */
    public MCArrowsCamBehavior() {
        currentInput = new HashMap<>();
    }

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

    @Override
    public void exit() {}

    /**
     * Call when a input is pressed.
     * @param data
     */
    @Override
    public void handleInputPressed(OrthographicCamera gdxCam, Command data) {
        if (translating)
            return;

        MCCameraManager camManager = MCCameraManager.get();
        if(data instanceof DirectionalCommand cmd) {
            currentInput.put(cmd.getIntVect(), true);
        } else if (data instanceof CameraZoomCommand zoomCmd) {
            System.out.println("received zoom cdm");
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
     * Call when a input is released.
     * @param data
     */
    public void handleInputReleased(Command data) {
        if (data instanceof DirectionalCommand cmd) {
            currentInput.put(cmd.getIntVect(), false);
        } 
    }

    @Override
    public void interpolateTo(Vector2 pos) {
        targetX = pos.x;
        targetY = pos.y;
        translating = true;
    }

    @Override
    public void update(OrthographicCamera gdxCam, float delta) {
        MCCameraManager camManager = MCCameraManager.get();

        if (translating) {
            if (Math.abs(targetX - gdxCam.position.x) > 0.01f
                || Math.abs(targetX - gdxCam.position.x) > 0.001) {
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

            gdxCam.position.x = MathUtils.clamp(
                gdxCam.position.x, 
                lowerLimit.x + camHalfWidth, 
                upperLimit.x - camHalfWidth
            );
            gdxCam.position.y = MathUtils.clamp(
                gdxCam.position.y, 
                lowerLimit.y + camHalfHeight, 
                upperLimit.y - camHalfHeight
            );

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

        float targetX = gdxCam.position.x + relativeMove.x;
        float targetY = gdxCam.position.y + relativeMove.y;

        targetX = MathUtils.clamp(
            targetX, 
            lowerLimit.x + camHalfWidth, 
            upperLimit.x - camHalfWidth
        );
        targetY = MathUtils.clamp(
            targetY, 
            lowerLimit.y + camHalfHeight, 
            upperLimit.y - camHalfHeight
        );

        gdxCam.position.x = targetX;
        gdxCam.position.y = targetY;
    }
}
