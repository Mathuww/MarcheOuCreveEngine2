package com.walk.or.die.engine.sm.entity.explorationplayer.states;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.entities.MCExplorationPlayer;
import com.walk.or.die.engine.entities.MCPortal;
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

        /**
         * @param firstData The initial directional command.
         */
        public MoveStateArgs(MCInputManager.DirectionalCommand firstData) {
            this.firstData = firstData;
        }
    }

    private Vector2 relativeMove;
    private DirectionalCommand lastCmd;
    private int nbConcurrentCommand;

    private final float MOVE_SPEED = 2f;

    private Map<MCIntVector2, Boolean> currentInput;

    private float limitX;
    private float limitY;


    /**
     * @param parent The exploration player.
     */
    public MCEPSMove(MCExplorationPlayer parent) {
        super(parent);
        this.name = "move";
        currentInput = new HashMap<>();
        limitX = (float) parent.getMap().getWidth() - parent.getSize();
        limitY = (float) parent.getMap().getHeight() - parent.getSize();
        relativeMove = new Vector2(0f, 0f);
    }

    /**
     * Updates the command.
     * @param cmd The directional command.
     * @param action A boolean indicating whether the command is being activated or deactivated.
     */
    private void updateCommand(MCInputManager.DirectionalCommand cmd, boolean action) {
        if(action) {
            currentInput.put(cmd.getIntVect(), true);
            lastCmd = cmd;
        } else {
            currentInput.put(cmd.getIntVect(), false);
        }
    }

    /**
     * Checks if the map limit is blocked.
     * @param projX The projected X coordinate.
     * @param projY The projected Y coordinate.
     * @return True if the map limit is blocked, false otherwise.
     */
    private boolean limitMapBlocked(float projX, float projY) {
        if((relativeMove.y == 0f) && (projX == 0f || projX  >= limitX)) {
            return true;
        } else if ((relativeMove.x == 0f) && (projY == 0f || projY  >= limitY)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if a collision is blocking the movement.
     * @param projX The projected X coordinate.
     * @param projY The projected Y coordinate.
     * @return True if a collision is blocking the movement, false otherwise.
     */
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
                    if(e instanceof MCPortal portal) {
                        portal.teleportation();
                        return false;
                    } else {
                        return true;
                    }
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

    /**
     * Checks if the movement is blocked.
     * @param projX The projected X coordinate.
     * @param projY The projected Y coordinate.
     * @return True if the movement is blocked, false otherwise.
     */
    private boolean blocked(float projX, float projY) {
        return limitMapBlocked(projX, projY) || collisionBlocked(projX, projY);
    }

    /**
     * Resets the input.
     */
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

    /**
     * Called at entrance
     * @param args The move state arguments.
     */
    @Override
    public void enter(MoveStateArgs args) {
        parent.playAnimation("walk");
        resetInput();
        updateCommand(args.firstData, true);
        bus.on(this, "InputPressed", this::inputPressed);
        bus.on(this, "InputReleased", this::inputReleased);
    }

    /**
     * Called at exit
     */
    @Override
    public void exit() {
        bus.off(this, "InputPressed");
        bus.off(this, "InputReleased");
    }

    /**
     * Called when an input is pressed.
     * @param data The command data.
     */
    public void inputPressed(Command data) {
        if(data instanceof DirectionalCommand cmd) {
            updateCommand(cmd, true);
        }
    }

    /**
     * Called when an input is released.
     * @param data The command data.
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

    /**
     * Called on each frame
     * @param delta The time delta
     */
    @Override
    public void update(float delta) { // IL FAUT UTILISER LE DELTAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
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

        relativeMove.x = relativeMove.x * MOVE_SPEED * delta;
        relativeMove.y = relativeMove.y * MOVE_SPEED * delta;
        
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

        if(!blocked(posX, posY)) {
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