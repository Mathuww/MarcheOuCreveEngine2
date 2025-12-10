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

    private float speed = 4f;
    private DirectionalCommand lastCmd;
    private int nbConcurrentCommande;

    private final float CAM_MOVE_SPEED = 0.05f;

    private Map<MCIntVector2, Boolean> currentInput;

    public MCEPSMove(MCExplorationPlayer parent) {
        super(parent);
        this.name = "move";
        currentInput = new HashMap<>();
    }

    private void updateCommande(MCInputManager.DirectionalCommand cmd, boolean action) {
        if(action) {
            currentInput.put(cmd.getIntVect(), true);
            lastCmd = cmd;
        } else {
            currentInput.put(cmd.getIntVect(), false);
        }
    }

    @Override
    public void enter(MoveStateArgs args) {
        parent.playAnimation("walk");
        int[][] directions = {
            {0, +1}, {0, -1},
            {+1, 0}, {-1, 0}
        };
        for (int[] dir : directions) {
            currentInput.put(new MCIntVector2(dir[0], dir[1]), false);
        }
        updateCommande(args.firstData, true);
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
            updateCommande(cmd, true);

        }
    }

    /**
     * Call when a input is released.
     * @param data
     */
    public void inputReleased(Command data) {
        if (data instanceof DirectionalCommand cmd) {
            updateCommande(cmd, false);

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

        Vector2 relativeMove = new Vector2(0, 0);

        MCExplorationPlayer player = MCEntityManager.get().getExplorationPlayer();

        nbConcurrentCommande = 0;

        for (Boolean action : currentInput.values()) {
            if(action) {
                nbConcurrentCommande +=1;
            }
        }
            
        for (Map.Entry<MCIntVector2, Boolean> entry : currentInput.entrySet()) {
            if(entry.getValue() && ((nbConcurrentCommande == 1) || (entry.getKey().equals(lastCmd.getIntVect())))) {
                MCIntVector2 commande = entry.getKey();
                relativeMove.x += commande.x;
                relativeMove.y += commande.y;
            }
        }
        if (relativeMove.len() > 0) relativeMove.nor();

        relativeMove.x = relativeMove.x * CAM_MOVE_SPEED;
        relativeMove.y = relativeMove.y * CAM_MOVE_SPEED;
        
        float targetX = player.getX() + relativeMove.x;
        float targetY = player.getY() + relativeMove.y;

        Vector2 lowerLimit = camManager.getGlobalLowerLimit();
        Vector2 upperLimit = camManager.getGlobalUpperLimit();


        targetX = MathUtils.clamp(
            targetX, 
            lowerLimit.x, 
            upperLimit.x
        );
        targetY = MathUtils.clamp(
            targetY, 
            lowerLimit.y, 
            upperLimit.y
        );

        player.setX(targetX);
        player.setY(targetY);
    }
}