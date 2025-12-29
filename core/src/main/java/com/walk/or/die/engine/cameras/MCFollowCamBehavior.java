package com.walk.or.die.engine.cameras;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.input.MCInputManager.CameraZoomCommand;
import com.walk.or.die.engine.input.MCInputManager.Command;
import com.walk.or.die.engine.shared.MCIntVector2;

/**
 * Behavior class which causes the camera to follow a target.
 */
public class MCFollowCamBehavior extends MCCameraBehavior {
    private MCEntity target;
    
    // Camera
    private final float CAM_MARGIN_X = 4f;
    private final float CAM_MARGIN_Y = 4f;
    private final float CAM_LERP = 3f;

    private Float targetZoom;

    public MCFollowCamBehavior() {}

    @Override
    public void enter() {}

    @Override
    public void exit() {}
    
    @Override
    public void update(OrthographicCamera gdxCam, float delta) {
        MCCameraManager camManager = MCCameraManager.get();

        if (targetZoom != null && Math.abs(targetZoom - gdxCam.zoom) > 0.001f) {
            gdxCam.zoom += (targetZoom - gdxCam.zoom) * delta * camManager.ZOOM_LERP;
            gdxCam.update();
        }

        MCEntity target = camManager.getFollowTarget();

        if (target == null) 
            return; // journée de repos bien méritée

        float maxLeft = gdxCam.position.x - gdxCam.viewportWidth / 2 + CAM_MARGIN_X;
        float maxRight = gdxCam.position.x + gdxCam.viewportWidth / 2 - CAM_MARGIN_X;
        float maxBottom = gdxCam.position.y - gdxCam.viewportHeight / 2 + CAM_MARGIN_Y;
        float maxTop = gdxCam.position.y + gdxCam.viewportHeight / 2 - CAM_MARGIN_Y;

        float px = target.getX() + target.getSize() / 2;
        float py = target.getY() + target.getSize() / 2;

        float targetX = gdxCam.position.x;
        float targetY = gdxCam.position.y;

        float camHalfWidth = gdxCam.viewportWidth / 2;
        float camHalfHeight = gdxCam.viewportHeight / 2;

        if (px < maxLeft) targetX = px + camHalfWidth - CAM_MARGIN_X;
        else if (px > maxRight) targetX = px - camHalfWidth + CAM_MARGIN_X;

        if (py < maxBottom) targetY = py + camHalfHeight - CAM_MARGIN_Y; // eloi
        else if (py > maxTop) targetY = py - camHalfHeight + CAM_MARGIN_Y; // matheo

        Vector2 lowerLimit = camManager.getGlobalLowerLimit();
        Vector2 upperLimit = camManager.getGlobalUpperLimit();

        float minX = lowerLimit.x + camHalfWidth;
        float maxX = upperLimit.x - camHalfWidth;
        
        if (minX > maxX) {
            targetX = (lowerLimit.x + upperLimit.x) / 2f;
        } else {
            targetX = MathUtils.clamp(targetX, minX, maxX);
        }

        float minY = lowerLimit.y + camHalfHeight;
        float maxY = upperLimit.y - camHalfHeight;

        if (minY > maxY) {
            targetY = (lowerLimit.y + upperLimit.y) / 2f;
        } else {
            targetY = MathUtils.clamp(targetY, minY, maxY);
        }


        float newX = gdxCam.position.x + ((targetX - gdxCam.position.x) * CAM_LERP * delta);
        float newY = gdxCam.position.y + ((targetY - gdxCam.position.y) * CAM_LERP * delta);
        gdxCam.position.x = newX;
        gdxCam.position.y = newY;
    }

    @Override
    public void handleInputPressed(OrthographicCamera gdxCam, Command cmd) {
        MCCameraManager camManager = MCCameraManager.get();
        if (cmd instanceof CameraZoomCommand zoomCmd) {
            System.out.println("received zoom cdm");
            targetZoom = gdxCam.zoom + camManager.ZOOM_STEP * zoomCmd.scrollDelta;
            targetZoom = MathUtils.clamp(targetZoom, camManager.ZOOM_MIN, camManager.ZOOM_MAX);
        }
    }

    @Override
    public void handleInputReleased(Command cmd) {}
}