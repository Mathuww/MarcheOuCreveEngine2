package com.walk.or.die.engine.cameras;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.entities.MCEntity;

public class MCFollowCamera {
    public OrthographicCamera camera;
    private MCEntity target;

    private float limitX;
    private float limitY;

    // Camera
    private final float CAM_MARGIN_X = 2f;
    private final float CAM_MARGIN_Y = 2f;
    private final float CAM_LERP = 3f;

    public MCFollowCamera(float viewportWidth, float viewportHeight) {
        camera = new OrthographicCamera(viewportWidth, viewportHeight);
        camera.position.set(viewportWidth / 2, viewportHeight / 2, 0);
        this.limitX = 0f;
        this.limitY = 0f;
    }

    public void follow(MCEntity target) {
        this.target = target;
    }

    public void setLimitX(float limitX) {
        this.limitX = limitX;
    }

    public void setLimitY(float limitY) {
        this.limitY = limitY;
    }

    public void update(float delta) {
        if (target == null) return;

        float maxLeft = camera.position.x - camera.viewportWidth / 2 + CAM_MARGIN_X;
        float maxRight = camera.position.x + camera.viewportWidth / 2 - CAM_MARGIN_X;
        float maxBottom = camera.position.y - camera.viewportHeight / 2 + CAM_MARGIN_Y;
        float maxTop = camera.position.y + camera.viewportHeight / 2 - CAM_MARGIN_Y;

        float px = target.getX() + target.getSize() / 2;
        float py = target.getY() + target.getSize() / 2;

        float targetX = camera.position.x;
        float targetY = camera.position.y;

        float camHalfWidth = camera.viewportWidth / 2;
        float camHalfHeight = camera.viewportHeight / 2;

        if (px < maxLeft) targetX = px + camHalfWidth - CAM_MARGIN_X;
        else if (px > maxRight) targetX = px - camHalfWidth + CAM_MARGIN_X;

        if (py < maxBottom) targetY = py + camHalfHeight - CAM_MARGIN_Y; // eloi
        else if (py > maxTop) targetY = py - camHalfHeight + CAM_MARGIN_Y; // matheo

        if (limitX != 0f) {
            targetX = MathUtils.clamp(targetX, camHalfWidth, limitX - camHalfWidth);
        }
        if (limitY != 0f) {
            targetY = MathUtils.clamp(targetY, camHalfHeight, limitY - camHalfHeight);
        }

        camera.position.x += (targetX - camera.position.x) * CAM_LERP * delta;
        camera.position.y += (targetY - camera.position.y) * CAM_LERP * delta;

        //System.out.println("camera position : " + camera.position.x + " , " + camera.position.y);
        camera.update();
    }
}