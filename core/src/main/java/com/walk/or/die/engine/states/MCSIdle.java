package com.walk.or.die.engine.states;

import com.walk.or.die.engine.entities.MCEntity;

public class MCSIdle extends MCState {

    public MCSIdle(MCEntity parent) {
        super(parent);
    }

    @Override
    public void update(float delta) {
        //System.out.println("On respire le bon air de la nature");
    }

    @Override
    public void enter() {
        super.enter();
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
