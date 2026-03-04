package com.walk.or.die.engine.sm.entity.character.states;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.entities.MCAlly;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.entities.MCEnemy;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.shared.MCIntVector2;
import com.walk.or.die.engine.sm.entity.character.MCCharacterState;

/**
 * Represents the state to move to a given position.<br>
 * Name = "move"
 */
public class MCCSClickMove extends MCCharacterState<MCCSClickMove.MoveStateArgs> {

    /**
     * Represents the arguments needed by the clickMove state to start.
     */
    public static class MoveStateArgs extends MCCharacterState.StateArgs {
        /** The target position. */
        public MCIntVector2 target;
        /** The path to follow. */
        public List<MCIntVector2> path;

        /**
         * Initializes a new `MoveStateArgs` instance.
         * @param target The targeted position.
         * @param path The path to follow.
         */
        public MoveStateArgs(MCIntVector2 target, List<MCIntVector2> path) {
            this.target = target;
            this.path = path;
        }
    }

    /** The ultimate goal position. */
    private MCIntVector2 goal;

    /** The queue of movements to perform. */
    private Deque<MCIntVector2> movements;
    /** The percentages at which to check for tile boundaries. */
    private Deque<Float> percent_check = new ArrayDeque<>();
    /** The starting position for the current movement segment. */
    private Vector2 start;
    /** The displacement vector for the current movement segment. */
    private Vector2 deplacement = new Vector2(0f,0f);
    /** The current progress percentage of the movement segment. */
    private float percent = 0f;
    /** The movement speed. */
    private float speed = 4f;

    /**
     * Initializes a new `MCCSClickMove` instance.
     * @param parent The parent character.
     */
    public MCCSClickMove(MCCharacter parent) {
        super(parent);
        movements = new ArrayDeque<>();
        this.name = "click_move";
    }

    /**
     * Called on each frame.
     * @param delta The time delta.
     */
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

    /**
     * Called at state entrance.
     * @param args The arguments for the move state.
     */
    @Override
    public void enter(MoveStateArgs args) {
        if (parent instanceof MCAlly ally)
            ally.getTurnState().moved();
        goal = args.target;
        movements.clear();
        movements.addAll(args.path);
        playAnimationOr("run", "walk");
        if (parent instanceof MCEnemy)
            parent.getHudCustomization().canShow = true;
        else
            parent.getHudCustomization().canShow = false;
        parent.notifyHudUpdate(true);
        super.enter(args);
    }

    /**
     * Called at state exit.
     */
    @Override
    public void exit() {
        super.exit();
    }
    
    /**
     * Processes the input command when a key is pressed.
     * @param data The input command data.
     */
    @Override
    protected void inputPressed(MCInputManager.Command data) {
        //System.out.println("Input pressed detect in Move");
        super.inputPressed(data);
    }

    /**
     * Processes the next movement. Changes state to idle if finished.
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
                playAnimationOr("run_right", "walk_right");
            else if (deplacement.x < 0)
                playAnimationOr("run_left", "walk_left");
            else if (deplacement.y > 0)
                playAnimationOr("run_up", "walk_up");
            else if (deplacement.y < 0)
                playAnimationOr("run_down", "walk_down");
        }
    }
    
    /**
     * Plays the given animation or a secondary one if the first one does not exist.
     * @param anim1 The primary animation name.
     * @param anim2 The secondary animation name.
     */
    private void playAnimationOr(String anim1, String anim2) {
        if (!parent.playAnimationWithoutReset(anim1))
            parent.playAnimationWithoutReset(anim2);
    }
}