package com.walk.or.die.engine.cameras;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.entities.MCEntity;

/**
 * Behavior class which causes the camera to follow a target.
 */
public class MCFollowCamBehavior extends MCCameraBehavior {
    private MCEntity target;
    
    // Camera
    private final float CAM_MARGIN_X = 2f;
    private final float CAM_MARGIN_Y = 2f;
    private final float CAM_LERP = 3f;

    public MCFollowCamBehavior() {}

    @Override
    public void enter() {}

    @Override
    public void exit() {}
    
    @Override
    public Vector2 update(OrthographicCamera gdxCam, float delta) {
        MCCameraManager camManager = MCCameraManager.get();
        MCEntity target = camManager.getFollowTarget();

        if (target == null) return new Vector2(gdxCam.position.x, gdxCam.position.y);

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


        float newX = gdxCam.position.x + ((targetX - gdxCam.position.x) * CAM_LERP * delta);
        float newY = gdxCam.position.y + ((targetY - gdxCam.position.y) * CAM_LERP * delta);
        return new Vector2(newX, newY);
    }
}