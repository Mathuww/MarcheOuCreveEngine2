package com.walk.or.die.engine.sm.entity.explorationplayer.states;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.cameras.MCCameraManager;
import com.walk.or.die.engine.entities.MCEntityManager;
import com.walk.or.die.engine.entities.MCExplorationPlayer;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.input.MCInputManager.Command;
import com.walk.or.die.engine.input.MCInputManager.DirectionalCommand;
import com.walk.or.die.engine.shared.MCEventBus;
import com.walk.or.die.engine.shared.MCIntVector2;
import com.walk.or.die.engine.sm.entity.explorationplayer.MCExplorationPlayerState;


public class MCEPSMove extends MCExplorationPlayerState<MCEPSMove.MoveStateArgs> {

    public static class MoveStateArgs extends MCExplorationPlayerState.StateArgs {
        public MCInputManager.DirectionalCommand data;

        public MoveStateArgs(MCInputManager.DirectionalCommand data) {
            this.data = data;
        }
    }

    private MCIntVector2 goal;

    private Deque<MCIntVector2> movements;
    private Deque<Float> percent_check = new ArrayDeque<>();
    private Vector2 start;
    private Vector2 deplacement = new Vector2(0f,0f);
    private float percent = 0f;
    private float speed = 4f;

    private final float CAM_MOVE_SPEED = 0.05f;

    private Map<MCIntVector2, Boolean> currentInput;

    public MCEPSMove(MCExplorationPlayer parent) {
        super(parent);
        this.name = "move";
        currentInput = new HashMap<>();
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
        currentInput.put(args.data.getIntVect(), true);
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

    /**
     * Call when a input is pressed.
     * @param data
     */
    public void inputPressed(Command data) {
        if(data instanceof DirectionalCommand cmd) {
            currentInput.put(cmd.getIntVect(), true);

        }
    }

    /**
     * Call when a input is released.
     * @param data
     */
    public void inputReleased(Command data) {
        if (data instanceof DirectionalCommand cmd) {
            currentInput.put(cmd.getIntVect(), false);
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
            
        for (Map.Entry<MCIntVector2, Boolean> entry : currentInput.entrySet()) {
            if (entry.getValue()) { // true
                MCIntVector2 cmd = entry.getKey();
                relativeMove.x += cmd.x;
                relativeMove.y += cmd.y;
                break;
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
/*

    @Override
    public void update(float delta) {
        if (deplacement.x != 0f) {
            percent += delta*speed/Math.abs(deplacement.x);
            if (percent >= 1f) {
                percent = 0f;
                parent.setX(start.x + deplacement.x);
                deplacement.x = 0f;
            } else {
                parent.setX(start.x + deplacement.x*percent);
            }
        }
        else if (deplacement.y != 0f) {
            percent += delta*speed/Math.abs(deplacement.y);
            if (percent >= 1f) {
                percent = 0f;
                parent.setY(start.y + deplacement.y);
                deplacement.y = 0f;
            } else {
                parent.setY(start.y + deplacement.y*percent);
            }
        } else {
            //System.out.println(parent.getPosition());
            bus.emit("EntityTileReached", new MCEntity.TileReachedArgs(this.parent, parent.getTilePosition()));
            nextMove();
        }

        if (!percent_check.isEmpty() && percent > percent_check.getFirst()) {
            bus.emit("EntityTileReached", new MCEntity.TileReachedArgs(this.parent, parent.getTilePosition()));
            percent_check.pollFirst();
            //System.out.println("Tile reached : " + parent.getTilePosition().toString()); // Bon c'est la merde je veux que ça se print à chack case mais ça marche pas mdr
        }
    }

    @Override
    public void enter(MoveStateArgs args) {
        if (parent instanceof MCAlly ally)
            ally.getTurnState().moved();
        goal = args.target;
        movements.clear();
        movements.addAll(args.path);
        parent.playAnimation("walk");
        super.enter(args);
    }

    @Override
    public void exit() {
        super.exit();
    }
    
    @Override
    protected void inputPressed(MCInputManager.Command data) {
        //System.out.println("Input pressed detect in Move");
        super.inputPressed(data);
    }

    private void nextMove() {
        if (movements.size() == 0) {
            changeState("idle", new MCESIdle.IdleStateArgs());
        } else {
            Vector2 targetPos = movements.removeFirst().toGdxVect();

            start = parent.getPosition();
            deplacement = new MCIntVector2(targetPos.cpy().sub(start)).toGdxVect();
            percent = 0f;
            
            int step = Math.abs(((int) deplacement.x) + ((int) deplacement.y));
            percent_check.clear();
            for (int i = 1; i <= step; i++) {
                float new_percent = i / (float) step;
                percent_check.add(new_percent);
            }
            //System.out.println("Percent check : " + percent_check.toString());

            // tile by tile sans diag donc on peut tester comme ca
            // (ca crash pas le jeu si l'anim existe pas juste il se passe rien)
            if (deplacement.x > 0)
                parent.playAnimation("walk_right");
            else if (deplacement.x < 0)
                parent.playAnimation("walk_left");
            else if (deplacement.y > 0)
                parent.playAnimation("walk_up");
            else if (deplacement.y < 0)
                parent.playAnimation("walk_down");
        }
    }

    @Override
    public boolean isBlocking() {
        return true;
    }
    */
}