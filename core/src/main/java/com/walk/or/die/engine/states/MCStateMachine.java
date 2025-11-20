package com.walk.or.die.engine.states;

import com.walk.or.die.engine.MCEventBus;
import com.walk.or.die.engine.entities.MCEntity;

import java.util.List;
import java.util.ArrayList;

public class MCStateMachine {
    
    private MCEntity parent;
    private List<MCState> states;
    private MCState currentState;

    public MCStateMachine(MCEntity parent) {
        MCEventBus bus = MCEventBus.get();
        this.parent = parent;
        states = new ArrayList<MCState>();
        bus.on("ChangeState", this::stateTransitionCheck);
    };

    public void setCurrentState(String name, List args) {
        currentState = getState(name);
        currentState.enter(args); 
    }

    public void addState(MCState state) {
        states.add(state);
    }

    public void update(float delta) {
        if (currentState == null) return ;
        currentState.update(delta);
    }

    public void stateTransitionCheck(Object data) {
        if (!(data instanceof List)) {
            return ;
        }

        List<String> list = (List<String>) data;
        String prevStateName = list.get(0), nextStateName = list.get(1);

        if (list.size() != 2 || prevStateName != currentState.getName() || !stateExists(nextStateName)) {
            return ;
        }

        System.out.println("Heho");
        stateTransition(currentState, getState(nextStateName), new ArrayList<>());
        
    }

    public boolean stateExists(String name) {
        for (MCState state : states) {
            if (state.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public MCState getState(String name) {
        for (MCState state : states) {
            if (state.getName().equalsIgnoreCase(name)) {
                return state;
            }
        }
        return null;
    }

    public void stateTransition(MCState prevState, MCState nextState, List args) {
        if (prevState.getName() != currentState.getName()) {
            return ;
        }

        currentState.exit();
        currentState = nextState;
        currentState.enter(args);

    }

}