package com.walk.or.die.engine.states;

import com.walk.or.die.engine.entities.MCEntity;
import com.badlogic.gdx.math.Vector2;

import java.util.List;


public class MCSClickMove extends MCState {

    Vector2 goal;

    public MCSClickMove(MCEntity parent) {
        super(parent);
        this.name = "click_move";
    }

    @Override
    public void update(float delta) {
        System.out.println("On respire le bon air de la nature");
    }

    public void enter(List args) {
        //this.goal = goal;
        super.enter(args);
    }

    @Override
    public void exit() {
        super.exit();
    }
    
    @Override
    protected void inputPressed(Object data) {
        super.inputPressed(data);
    }
}