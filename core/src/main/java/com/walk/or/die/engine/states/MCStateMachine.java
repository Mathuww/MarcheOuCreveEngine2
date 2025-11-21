package com.walk.or.die.engine.states;

import com.walk.or.die.engine.MCEventBus;
import com.walk.or.die.engine.entities.MCEntity;

import java.util.List;
import java.util.ArrayList;

public class MCStateMachine {
    
    // Classe pour l'event de transition
    public static class TransitionArgs<T extends MCState.StateArgs> {
        public String prevState;
        public String nextState;
        public T args;

        public TransitionArgs (String prevState, String nextState, T args) {
            this.prevState = prevState;
            this.nextState = nextState;
            this.args = args;
        }
    }

    protected MCEntity parent;
    private List<MCState> states;
    private MCState currentState;

    public MCStateMachine(MCEntity parent) {
        MCEventBus bus = MCEventBus.get();
        this.parent = parent;
        states = new ArrayList<MCState>();
        bus.on("ChangeState", this::stateTransitionCheck);
    };

    public void setCurrentState(String name, MCState.StateArgs args) {
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

    public void stateTransitionCheck(TransitionArgs<?> args) {
        if (!args.prevState.equals(currentState.getName()) || !stateExists(args.nextState)) {
            return ;
        }

        System.out.println("Transition from " + args.prevState + " to " + args.nextState);
        stateTransition(currentState, getState(args.nextState), args.args);
        
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

    private <T extends MCState.StateArgs> void stateTransition(MCState prevState, MCState nextState, T args) {
        if (!prevState.getName().equals(currentState.getName())) {
            return ;
        }

        currentState.exit();
        nextState.enter(args);
        currentState = nextState;
    }

}