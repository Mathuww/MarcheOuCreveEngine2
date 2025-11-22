package com.walk.or.die.engine.states;

import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.input.MCInputManager;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;


public class MCSClickMove extends MCState<MCSClickMove.MoveStateArgs> {

    public static class MoveStateArgs extends MCState.StateArgs {
        public Vector2 target;

        public MoveStateArgs(Vector2 target) {
            this.target = target;
        }
    }

    private Vector2 goal;

    private Deque<Vector2> movements;
    private Vector2 start;
    private Vector2 deplacement = new Vector2(0f,0f);
    private float percent = 0f;
    private float speed = 4f;

    public MCSClickMove(MCEntity parent) {
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
            nextMove();
        }
    }

    @Override
    public void enter(MoveStateArgs args) {
        goal = args.target;
        movements.clear();
        movements.addAll(parent.getMap().getPath(parent.getPosition(), goal));
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
        if (movements.size() == 0) changeState("idle", new MCSIdle.IdleStateArgs()) ;
        else {
            Vector2 targetPos = movements.removeFirst();

            start = parent.getPosition();
            deplacement.x = MathUtils.floor(targetPos.x - parent.getX()); // avant : cast (int)end.x....
            deplacement.y = MathUtils.floor(targetPos.y - parent.getY());
            percent = 0f;
        }
    }
}