package com.walk.or.die.engine.sm.entity.character.states;

import com.walk.or.die.engine.entities.MCAlly;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.shared.MCIntVector2;
import com.walk.or.die.engine.sm.entity.character.MCCharacterState;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * The state to move to a given position.<br>
 * Name = "move"
 */
public class MCCSClickMove extends MCCharacterState<MCCSClickMove.MoveStateArgs> {

    /**
     * Class which represents args needed by the clickMove state to start.
     */
    public static class MoveStateArgs extends MCCharacterState.StateArgs {
        public MCIntVector2 target;
        public List<MCIntVector2> path;

        /**
         * The constructor.
         * @param target - The targeted pos.
         * @param path - The path to follow.
         */
        public MoveStateArgs(MCIntVector2 target, List<MCIntVector2> path) {
            this.target = target;
            this.path = path;
        }
    }

    private MCIntVector2 goal;

    private Deque<MCIntVector2> movements;
    private Deque<Float> percent_check = new ArrayDeque<>();
    private Vector2 start;
    private Vector2 deplacement = new Vector2(0f,0f);
    private float percent = 0f;
    private float speed = 4f;

    /**
     * The constructor.
     * @param parent
     */
    public MCCSClickMove(MCCharacter parent) {
        super(parent);
        movements = new ArrayDeque<>();
        this.name = "click_move";
    }

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

    /**
     * Process the next movement. Change state to idle if finished.
     */
    private void nextMove() {
        if (movements.size() == 0) {
            changeState("idle", new MCCSIdle.IdleStateArgs());
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
    
}