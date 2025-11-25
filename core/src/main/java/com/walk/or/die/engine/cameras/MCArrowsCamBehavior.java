package com.walk.or.die.engine.cameras;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.walk.or.die.engine.MCEventBus;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.input.MCInputManager.Command;
import com.walk.or.die.engine.input.MCInputManager.DirectionalCommand;

public class MCArrowsCamBehavior extends MCCameraBehavior {
    private final float CAM_MOVE_SPEED = 0.025f;
    private final float CAM_LERP = 3f;

    private Map<DirectionalCommand, Boolean> currentInput;
    // private List<MCEventBus.Subscription> subscriptions = new ArrayList<>();

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
            currentInput.put(new DirectionalCommand(dir[0], dir[1]), false);
        }
        MCEventBus bus = MCEventBus.get();
        bus.on(this, "InputPressed", this::inputPressed);
        bus.on(this, "InputReleased", this::inputReleased);
        //subscriptions.add(bus.on(this, "InputPressed", this::inputPressed));
        //subscriptions.add(bus.on(this, "InputReleased", this::inputReleased));
    }

    @Override
    public void exit() {
        MCEventBus bus = MCEventBus.get();
        bus.off(this, "InputPressed");
        bus.off(this, "InputReleased");
        //for (MCEventBus.Subscription s : subscriptions) s.unsubscribe();
        //subscriptions.clear();
    }

    public void inputPressed(Command data) {
        if(data instanceof DirectionalCommand cmd) {
            currentInput.put(cmd, true);
        }
    }

    public void inputReleased(Command data) {
        if (data instanceof DirectionalCommand cmd) {
            currentInput.put(cmd, false);
        }
    }

    public void update(OrthographicCamera gdxCam, float delta) {
        MCCameraManager camManager = MCCameraManager.get();

        Vector2 relativeMove = new Vector2(0, 0);
            
        for (Map.Entry<DirectionalCommand, Boolean> entry : currentInput.entrySet()) {
            if (entry.getValue()) { // true
                DirectionalCommand cmd = entry.getKey();
                relativeMove.x += cmd.dx;
                relativeMove.y += cmd.dy;
            }
        }
        if (relativeMove.len() > 0) relativeMove.nor();

        relativeMove.x = relativeMove.x * CAM_MOVE_SPEED;
        relativeMove.y = relativeMove.y * CAM_MOVE_SPEED;

        float targetX = gdxCam.position.x + relativeMove.x;
        float targetY = gdxCam.position.y + relativeMove.y;

        float camHalfWidth = gdxCam.viewportWidth / 2;
        float camHalfHeight = gdxCam.viewportHeight / 2;

        if (camManager.getLimitX() != 0f) {
            targetX = MathUtils.clamp(targetX, camHalfWidth, camManager.getLimitX() - camHalfWidth);
        }
        if (camManager.getLimitY() != 0f) {
            targetY = MathUtils.clamp(targetY, camHalfHeight, camManager.getLimitY() - camHalfHeight);
        }

        gdxCam.position.x += (targetX - gdxCam.position.x); // * CAM_LERP * delta;
        gdxCam.position.y += (targetY - gdxCam.position.y); // * CAM_LERP * delta;
    }
}
