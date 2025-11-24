package com.walk.or.die.engine.sm.entity;

import com.walk.or.die.engine.MCEventBus;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.input.MCInputManager.ClickTileCommand;
import com.walk.or.die.engine.input.MCInputManager.Command;
import com.walk.or.die.engine.input.MCInputManager.DirectionalCommand;
import com.walk.or.die.engine.sm.MCState;
import com.walk.or.die.engine.sm.MCStateMachine;
import com.walk.or.die.engine.sm.MCState.StateArgs;
import com.walk.or.die.engine.sm.MCStateMachine.TransitionArgs;

import java.util.List;
import java.util.function.Consumer;
import java.util.ArrayList;

public abstract class MCEntityState<T extends MCEntityState.StateArgs> extends MCState<T> {
    protected MCEntity parent;

    public MCEntityState(MCEntity parent) {
        super();
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
        listen("InputPressed", this::inputPressed);
        // Je rentre dans tes MC en bus
    }

    @Override
    public void exit() {
        //System.out.println("Exit " + getName());
        //unsubscribeAll();
        bus.off(this, "InputPressed");
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
       parent.getStateManager().stateTransitionCheck(new MCStateMachine.TransitionArgs(getName(), newState, args));
    }

}