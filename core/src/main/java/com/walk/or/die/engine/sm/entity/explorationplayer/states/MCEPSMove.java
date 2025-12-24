package com.walk.or.die.engine.sm.entity.explorationplayer.states;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.entities.MCExplorationPlayer;
import com.walk.or.die.engine.entities.MCEntityManager;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.input.MCInputManager.Command;
import com.walk.or.die.engine.input.MCInputManager.DirectionalCommand;
import com.walk.or.die.engine.shared.MCIntVector2;
import com.walk.or.die.engine.sm.entity.explorationplayer.MCExplorationPlayerState;
import com.walk.or.die.engine.tiledmap.MCTerrainMap;


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
        limitX = (float) parent.getMap().getWidth() - parent.getSize();
        limitY = (float) parent.getMap().getHeight() - parent.getSize();
        relativeMove = new Vector2(0f, 0f);
    }

    private void updateCommand(MCInputManager.DirectionalCommand cmd, boolean action) {
        if(action) {
            currentInput.put(cmd.getIntVect(), true);
            lastCmd = cmd;
        } else {
            currentInput.put(cmd.getIntVect(), false);
        }
    }

    private boolean limitMapBlocked(float projX, float projY) {
        if((relativeMove.y == 0f) && (projX == 0f || projX  >= limitX)) {
            return true;
        } else if ((relativeMove.x == 0f) && (projY == 0f || projY  >= limitY)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean collisionBlocked(float projX, float projY) {
        float tolerance = parent.getToleranceHitbox();

        Rectangle projHitbox = new Rectangle(
            projX + tolerance,
            projY + tolerance,
            parent.getSize() - 2 * tolerance,
            parent.getSize() - 2 * tolerance
        ); 

        for (MCEntity e : MCEntityManager.get().getEntities()) {
            if (!e.equals(parent)) {
                if (e.getHitbox().overlaps(projHitbox)) {
                    return true;
                }
            }
        }

        int minX = (int) Math.floor((projX + tolerance));
        int maxX = (int) Math.ceil((projX + parent.getSize() - tolerance));

        int minY = (int) Math.floor((projY + tolerance));
        int maxY = (int) Math.ceil((projY + parent.getSize()  - tolerance));

        for (int i = minX; i < maxX; i++) {
            for (int j = minY; j < maxY; j++) {
                MCIntVector2 projPos = new MCIntVector2(i, j);
                if (!parent.getMap().isWalkable(new MCIntVector2(i, j))) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean blocked(float projX, float projY) {
        return limitMapBlocked(projX, projY) || collisionBlocked(projX, projY);
    }

    private void resetInput() {
        float[][] directions = {
            {0f, +1f}, {0f, -1f},
            {+1f, 0f}, {-1f, 0f}
        };
        for (float[] dir : directions) {
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
        nbConcurrentCommand = 0;

        for (Boolean action : currentInput.values()) {
            if(action) {
                nbConcurrentCommand +=1;
            }
        }
            
        for (Map.Entry<MCIntVector2, Boolean> entry : currentInput.entrySet()) {
            if(entry.getValue() && ((nbConcurrentCommand == 1) || (entry.getKey().equals(lastCmd.getIntVect())))) {
                MCIntVector2 command = entry.getKey();
                relativeMove.x = (float) command.x;
                relativeMove.y = (float) command.y;
            }
        }
        if (relativeMove.len() > 0) relativeMove.nor();

        relativeMove.x = relativeMove.x * CAM_MOVE_SPEED;
        relativeMove.y = relativeMove.y * CAM_MOVE_SPEED;
        
        float posX = (float) parent.getX() + relativeMove.x;
        float posY = (float) parent.getY() + relativeMove.y;

        posX = MathUtils.clamp(
            posX, 
            0f, 
            limitX
        );
        
        posY = MathUtils.clamp(
            posY, 
        0f, 
            limitY
        );

        if(blocked(posX, posY)) {
            changeState("idle", new MCEPSIdle.IdleStateArgs());
            return;
        } else {
            if (relativeMove.x > 0f) {
                parent.playAnimationWithoutReset("walk_right");
            } else if (relativeMove.x < 0f) {
                parent.playAnimationWithoutReset("walk_left");
            } else if (relativeMove.y > 0f) {
                parent.playAnimationWithoutReset("walk_up");
            } else if (relativeMove.y < 0f) {
                parent.playAnimationWithoutReset("walk_down");
            }
            parent.setX(posX);
            parent.setY(posY);
        }
    }
}