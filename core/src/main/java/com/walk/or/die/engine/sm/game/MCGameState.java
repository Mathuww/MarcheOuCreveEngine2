package com.walk.or.die.engine.sm.game;

import com.walk.or.die.engine.sm.MCState;
import com.walk.or.die.engine.sm.entity.MCEntityStateMachine;

public abstract class MCGameState<T extends MCGameState.StateArgs> extends MCState<T> {
    public MCGameState() {
        super();
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
        MCGameStateMachine.get().stateTransitionCheck(new MCEntityStateMachine.TransitionArgs(getName(), newState, args));
    }
}
