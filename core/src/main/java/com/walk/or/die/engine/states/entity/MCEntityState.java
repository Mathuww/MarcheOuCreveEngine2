package com.walk.or.die.engine.states.entity;

import com.walk.or.die.engine.MCEventBus;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.input.MCInputManager.ClickTileCommand;
import com.walk.or.die.engine.input.MCInputManager.Command;
import com.walk.or.die.engine.input.MCInputManager.DirectionalCommand;
import com.walk.or.die.engine.states.MCState;
import com.walk.or.die.engine.states.MCStateMachine;
import com.walk.or.die.engine.states.MCState.StateArgs;
import com.walk.or.die.engine.states.MCStateMachine.TransitionArgs;

import java.util.List;
import java.util.function.Consumer;
import java.util.ArrayList;

public class MCEntityState<T extends MCEntityState.StateArgs> extends MCState<T> {
    protected MCEntity parent;

    public MCEntityState(MCEntity parent) {
        this.parent = parent;
    }

    public MCEntity getParent() {
        return parent;
    }

    @Override
    public void update(float delta) {}

    @Override
    public void enter(T args) {
        //System.out.println("Enter " + getName());
        MCEventBus bus = MCEventBus.get();
        listen("InputPressed", this::inputPressed);
        // Je rentre dans tes MC en bus
    }

    @Override
    public void exit() {
        //System.out.println("Exit " + getName());
        //unsubscribeAll();
        MCEventBus.get().off(this, "InputPressed");
    }
    
    protected void inputPressed(MCInputManager.Command data) {
        if (!(data instanceof MCInputManager.Command)) return;
        
        if (data instanceof MCInputManager.ClickTileCommand) {
            System.out.println("Oh, on a clické");
        }
        else if (data instanceof MCInputManager.DirectionalCommand) {
            System.out.println("Oh on a pressé les touches du clavier");
        }
    }

    protected void changeState(String newState, MCEntityState.StateArgs args) {
       parent.getStateManager().stateTransitionCheck(new MCEntityStateMachine.TransitionArgs(getName(), newState, args));
    }

}