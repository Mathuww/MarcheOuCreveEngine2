package com.walk.or.die.engine.sm.entity.states;

import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.shared.MCEventBus;
import com.walk.or.die.engine.sm.MCState;
import com.walk.or.die.engine.sm.MCState.StateArgs;
import com.walk.or.die.engine.sm.entity.MCEntityState;
import com.walk.or.die.engine.sm.entity.states.MCESIdle.IdleStateArgs;
import com.walk.or.die.engine.tiledmap.MCPathfinder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;


public class MCESClickMove extends MCEntityState<MCESClickMove.MoveStateArgs> {

    public static class MoveStateArgs extends MCEntityState.StateArgs {
        public Vector2 target;
        public List<Vector2> path;

        public MoveStateArgs(Vector2 target, List<Vector2> path) {
            this.target = target;
            this.path = path;
        }
    }

    private Vector2 goal;

    private Deque<Vector2> movements;
    private Deque<Float> percent_check = new ArrayDeque<>();
    private Vector2 start;
    private Vector2 deplacement = new Vector2(0f,0f);
    private float percent = 0f;
    private float speed = 4f;

    public MCESClickMove(MCCharacter parent) {
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
        System.out.println("On redebug cette fonction de merde");
        if (movements.size() == 0) changeState("idle", new MCESIdle.IdleStateArgs()) ;
        else {
            Vector2 targetPos = movements.removeFirst();

            start = parent.getPosition();
            deplacement.x = MathUtils.floor(targetPos.x - parent.getX()); // avant : cast (int)end.x....
            deplacement.y = MathUtils.floor(targetPos.y - parent.getY());
            percent = 0f;
            
            int step = Math.abs(((int) deplacement.x) + ((int) deplacement.y));
            percent_check.clear();
            for (int i = 1; i <= step; i++) {
                float new_percent = i / (float) step;
                percent_check.add(new_percent);
            }
            //System.out.println("Percent check : " + percent_check.toString());
        }
    }

}