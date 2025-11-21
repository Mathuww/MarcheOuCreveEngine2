package com.walk.or.die.engine.states;

import com.walk.or.die.engine.MCEventBus;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.input.MCInputManager.ClickTileCommand;
import com.walk.or.die.engine.input.MCInputManager.Command;
import com.walk.or.die.engine.input.MCInputManager.DirectionalCommand;
import java.util.List;
import java.util.function.Consumer;
import java.util.ArrayList;

public class MCState<T extends MCState.StateArgs> {

    // Classe des arguments pour initialiser un state
    public static class StateArgs {}


    private Consumer<MCInputManager.Command> inputPressedConsumer;
    protected MCEntity parent;
    protected String name;

    public MCState(MCEntity parent) {
        this.parent = parent;
    }
    
    public String getName() {
        return name;
    }

    public MCEntity getParent() {
        return parent;
    }

    public void update(float delta) {}

    public void enter(T args) {
        System.out.println("Enter " + getName());
        MCEventBus bus = MCEventBus.get();
        inputPressedConsumer = this::inputPressed;
        bus.on("InputPressed", inputPressedConsumer);
        // Je rentre dans tes MC en bus
    }

    public void exit() {
        System.out.println("Exit " + getName());
        MCEventBus bus = MCEventBus.get();
        bus.off("InputPressed", inputPressedConsumer);
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

    protected void changeState(String newState, StateArgs args) {
        MCEventBus bus = MCEventBus.get();
        bus.emit("ChangeState", new MCStateMachine.TransitionArgs(getName(), newState, args));
    }

}