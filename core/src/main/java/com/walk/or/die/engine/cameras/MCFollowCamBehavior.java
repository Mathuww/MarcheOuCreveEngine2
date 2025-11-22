package com.walk.or.die.engine.cameras;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.entities.MCEntity;

public class MCFollowCamBehavior extends MCCameraBehavior {
    private MCEntity target;
    
    // Camera
    private final float CAM_MARGIN_X = 2f;
    private final float CAM_MARGIN_Y = 2f;
    private final float CAM_LERP = 3f;

    public MCFollowCamBehavior() {}

    @Override
    public void update(OrthographicCamera gdxCam, float delta) {
        MCCameraManager camManager = MCCameraManager.get();
        MCEntity target = camManager.getFollowTarget();

        if (target == null) return;

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

        if (camManager.getLimitX() != 0f) {
            targetX = MathUtils.clamp(targetX, camHalfWidth, camManager.getLimitX() - camHalfWidth);
        }
        if (camManager.getLimitY() != 0f) {
            targetY = MathUtils.clamp(targetY, camHalfHeight, camManager.getLimitY() - camHalfHeight);
        }

        gdxCam.position.x += (targetX - gdxCam.position.x) * CAM_LERP * delta;
        gdxCam.position.y += (targetY - gdxCam.position.y) * CAM_LERP * delta;
    }
}