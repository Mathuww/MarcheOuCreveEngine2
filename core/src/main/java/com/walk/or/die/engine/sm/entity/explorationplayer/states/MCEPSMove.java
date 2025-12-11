package com.walk.or.die.engine.sm.entity.explorationplayer.states;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.cameras.MCCameraManager;
import com.walk.or.die.engine.entities.MCEntityManager;
import com.walk.or.die.engine.entities.MCExplorationPlayer;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.input.MCInputManager.Command;
import com.walk.or.die.engine.input.MCInputManager.DirectionalCommand;
import com.walk.or.die.engine.shared.MCIntVector2;
import com.walk.or.die.engine.sm.entity.explorationplayer.MCExplorationPlayerState;


public class MCEPSMove extends MCExplorationPlayerState<MCEPSMove.MoveStateArgs> {

    public static class MoveStateArgs extends MCExplorationPlayerState.StateArgs {
        public MCInputManager.DirectionalCommand firstData;

        public MoveStateArgs(MCInputManager.DirectionalCommand firstData) {
            this.firstData = firstData;
        }
    }

    private Vector2 relativeMove;
    private float speed = 4f;
    private DirectionalCommand lastCmd;
    private int nbConcurrentCommand;

    private final float CAM_MOVE_SPEED = 0.022f;

    private Map<MCIntVector2, Boolean> currentInput;

    private float limitX;
    private float limitY;


    public MCEPSMove(MCExplorationPlayer parent) {
        super(parent);
        this.name = "move";
        currentInput = new HashMap<>();
        limitX = parent.getMap().getWidth() - parent.getSize();
        limitY = parent.getMap().getHeight() - parent.getSize();
        relativeMove = new Vector2(0, 0);
    }

    private void updateCommand(MCInputManager.DirectionalCommand cmd, boolean action) {
        if(action) {
            currentInput.put(cmd.getIntVect(), true);
            lastCmd = cmd;
            relativeMove.set(0,0);
        } else {
            currentInput.put(cmd.getIntVect(), false);
        }
    }

    private boolean blocked() {
        if((relativeMove.y == 0) && (parent.getX() == 0 || parent.getX() >= limitX)) {
            return true;
        } else if ((relativeMove.x == 0) && (parent.getY() == 0 || parent.getY() >= limitY)) {
            return true;
        } else {
            return false;
        }
    }

    private void resetInput() {
        int[][] directions = {
            {0, +1}, {0, -1},
            {+1, 0}, {-1, 0}
        };
        for (int[] dir : directions) {
            currentInput.put(new MCIntVector2(dir[0], dir[1]), false);
        }
        relativeMove.x = 0f;
        relativeMove.y = 0f;
    }

    @Override
    public void enter(MoveStateArgs args) {
        parent.playAnimation("walk");
        resetInput();
        updateCommand(args.firstData, true);
        bus.on(this, "InputPressed", this::inputPressed);
        bus.on(this, "InputReleased", this::inputReleased);
    }

    @Override
    public void exit() {
        bus.off(this, "InputPressed");
        bus.off(this, "InputReleased");
    }

    /**
     * Call when a input is pressed.
     * @param data
     */
    public void inputPressed(Command data) {
        if(data instanceof DirectionalCommand cmd) {
            updateCommand(cmd, true);
        }
    }

    /**
     * Call when a input is released.
     * @param data
     */
    public void inputReleased(Command data) {
        if (data instanceof DirectionalCommand cmd) {
            updateCommand(cmd, false);

            //verificate if nothing input was pressed
            for (Boolean value : currentInput.values()) {
                if(value == true) {
                    return;
                }
            }
            
            changeState("idle", new MCEPSIdle.IdleStateArgs());
        }
    }

    @Override
    public void update(float delta) {
        MCCameraManager camManager = MCCameraManager.get();

        nbConcurrentCommand = 0;

        for (Boolean action : currentInput.values()) {
            if(action) {
                nbConcurrentCommand +=1;
            }
        }
            
        for (Map.Entry<MCIntVector2, Boolean> entry : currentInput.entrySet()) {
            if(entry.getValue() && ((nbConcurrentCommand == 1) || (entry.getKey().equals(lastCmd.getIntVect())))) {
                MCIntVector2 command = entry.getKey();
                relativeMove.x += command.x;
                relativeMove.y += command.y;
            }
        }
        if (relativeMove.len() > 0) relativeMove.nor();

        relativeMove.x = relativeMove.x * CAM_MOVE_SPEED;
        relativeMove.y = relativeMove.y * CAM_MOVE_SPEED;
        
        float posX = parent.getX() + relativeMove.x;
        float posY = parent.getY() + relativeMove.y;

        parent.setX(
            MathUtils.clamp(
                posX, 
            0f, 
                limitX
            )
        );

        parent.setY(
            MathUtils.clamp(
                posY, 
            0f, 
                limitY
            )
        );

        if(blocked()) {
            changeState("idle", new MCEPSIdle.IdleStateArgs());
        } else {
            if (relativeMove.x > 0)
                parent.playAnimationWithoutReset("walk_right");
            else if (relativeMove.x < 0)
                parent.playAnimationWithoutReset("walk_left");
            else if (relativeMove.y > 0)
                parent.playAnimationWithoutReset("walk_up");
            else if (relativeMove.y < 0)
                parent.playAnimationWithoutReset("walk_down");
        }
    }
}