package com.walk.or.die.engine.cameras;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.input.MCInputManager.Command;
import com.walk.or.die.engine.input.MCInputManager.DirectionalCommand;
import com.walk.or.die.engine.shared.MCEventBus;
import com.walk.or.die.engine.shared.MCIntVector2;

public class MCArrowsCamBehavior extends MCCameraBehavior {
    private final float CAM_MOVE_SPEED = 0.05f;

    private Map<MCIntVector2, Boolean> currentInput;

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
        bus.on(this, "InputPressed", this::inputPressed);
        bus.on(this, "InputReleased", this::inputReleased);
    }

    @Override
    public void exit() {
        MCEventBus bus = MCEventBus.get();
        bus.off(this, "InputPressed");
        bus.off(this, "InputReleased");
    }

    public void inputPressed(Command data) {
        if(data instanceof DirectionalCommand cmd) {
            currentInput.put(cmd.getIntVect(), true);
        }
    }

    public void inputReleased(Command data) {
        if (data instanceof DirectionalCommand cmd) {
            currentInput.put(cmd.getIntVect(), false);
        }
    }

    public Vector2 update(OrthographicCamera gdxCam, float delta) {
        MCCameraManager camManager = MCCameraManager.get();

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

        float camHalfWidth = gdxCam.viewportWidth / 2;
        float camHalfHeight = gdxCam.viewportHeight / 2;

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

        return new Vector2(targetX, targetY);
    }
}
