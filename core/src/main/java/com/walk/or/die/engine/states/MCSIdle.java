package com.walk.or.die.engine.states;

import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.MCEventBus;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.input.MCInputManager;

import java.util.ArrayList;
import java.util.List;


public class MCSIdle extends MCState<MCSIdle.IdleStateArgs> {

    public static class IdleStateArgs extends MCState.StateArgs {}

    public MCSIdle(MCEntity parent) {
        super(parent);
        this.name = "idle";
    }

    @Override
    public void update(float delta) {
        //System.out.println("On respire le bon air de la nature");
    }

    @Override
    public void enter(IdleStateArgs args) {
        super.enter(args);
    }

    @Override
    public void exit() {
        super.exit();
    }
    
    @Override
    protected void inputPressed(MCInputManager.Command data) {
        System.out.println("Input pressed detect in Idle");
        if (data instanceof MCInputManager.ClickTileCommand) {
            MCInputManager.ClickTileCommand tileCmd = (MCInputManager.ClickTileCommand) data;
            changeState("click_move", new MCSClickMove.MoveStateArgs(tileCmd.getVector()));
        }
        else if (data instanceof MCInputManager.DirectionalCommand) {
            System.out.println("Oh on a pressé les touches du clavier");
        }
    }
}
