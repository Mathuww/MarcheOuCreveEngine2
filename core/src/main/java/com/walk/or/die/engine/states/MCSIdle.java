package com.walk.or.die.engine.states;

import com.walk.or.die.engine.MCEventBus;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.input.MCInputManager;

import java.util.ArrayList;
import java.util.List;

public class MCSIdle extends MCState {

    public MCSIdle(MCEntity parent) {
        super(parent);
        this.name = "idle";
    }

    @Override
    public void update(float delta) {
        //System.out.println("On respire le bon air de la nature");
    }

    @Override
    public void enter(List args) {
        super.enter(args);
    }

    @Override
    public void exit() {
        super.exit();
    }
    
    @Override
    protected void inputPressed(Object data) {
        if (!(data instanceof MCInputManager.Command)) return;
        
        if (data instanceof MCInputManager.ClickTileCommand) {
            List args = new ArrayList<>();
            args.add(getName());
            args.add("click_move");
            MCEventBus.get().emit("ChangeState", args);
        }
        else if (data instanceof MCInputManager.OneMoveCommand) {
            System.out.println("Oh on a pressé les touches du clavier");
        }
    }
}
