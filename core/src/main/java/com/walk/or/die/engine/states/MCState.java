package com.walk.or.die.engine.states;

import com.walk.or.die.engine.MCEventBus;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.input.MCInputManager.ClickTileCommand;
import com.walk.or.die.engine.input.MCInputManager.Command;
import com.walk.or.die.engine.input.MCInputManager.OneMoveCommand;
import java.util.List;
import java.util.ArrayList;

public class MCState {
    private MCEntity parent;
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

    public void enter(List args) {
        System.out.println("Hop");
        MCEventBus bus = MCEventBus.get();
        bus.on("InputPressed", this::inputPressed);
        // Je rentre dans tes MC en bus
    }

    public void exit() {
        MCEventBus bus = MCEventBus.get();
        bus.off("InputPressed", this::inputPressed);
    }
    
    protected void inputPressed(Object data) {
        if (!(data instanceof MCInputManager.Command)) return;
        
        if (data instanceof MCInputManager.ClickTileCommand) {
            System.out.println("Oh, on a clické");
        }
        else if (data instanceof MCInputManager.OneMoveCommand) {
            System.out.println("Oh on a pressé les touches du clavier");
        }
    }

    protected void change_state(String new_state) {
        MCEventBus bus = MCEventBus.get();
        List<String> list = new ArrayList<>();
        list.add(getName());
        list.add(new_state);
        bus.emit("ChangeState", list);
    }

}