package com.walk.or.die.engine.sm.game;

import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.sm.MCState;
import com.walk.or.die.engine.sm.MCStateMachine;

public abstract class MCGameState<T extends MCGameState.StateArgs> extends MCState<T> {
    protected MCGame parent;

    public MCGameState(MCGame parent) {
        super();
        this.parent = parent;
    }

    @Override
    public void enter(T args) {
        System.out.println("entering game state " + this.name);
    }

    @Override
    public void exit() {

    }

    @Override
    public void update(float delta) {

    }

    @Override
    protected void changeState(String newState, MCGameState.StateArgs args) {
        parent.getStateManager().stateTransitionCheck(new MCStateMachine.TransitionArgs(getName(), newState, args));
    }
}
