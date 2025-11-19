package com.walk.or.die.engine.states;

import java.util.List;

import com.walk.or.die.engine.entities.MCEntity;

import java.util.ArrayList;

public class MCStateMachine {
    
    private MCEntity parent;
    private List<MCState> states;
    private MCState currentState;

    public MCStateMachine(MCEntity parent) {
        this.parent = parent;
        states = new ArrayList<MCState>();
    };

    public void addState(MCState state) {
        states.add(state);
        currentState = state;
        state.enter();
    }

    public void update(float delta) {
        if (currentState == null) return ;
        currentState.update(delta);
    }

    public void stateTransition(MCState prevState, MCState nextState) {
        if (prevState.getName() != currentState.getName()) {
            return ;
        }

        currentState.exit();
        currentState = nextState;
        currentState.enter();

    }

}