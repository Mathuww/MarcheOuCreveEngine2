package com.walk.or.die.engine.states.entity;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.MCEventBus;
import com.walk.or.die.engine.cameras.MCCameraManager;
import com.walk.or.die.engine.cameras.MCCameraMode;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.states.MCState;
import com.walk.or.die.engine.states.MCState.StateArgs;

import java.util.ArrayList;
import java.util.List;


public class MCESIdle extends MCEntityState<MCESIdle.IdleStateArgs> {

    public static class IdleStateArgs extends MCEntityState.StateArgs {}

    public MCESIdle(MCEntity parent) {
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
        //System.out.println("Input pressed detect in Idle");
        if (data instanceof MCInputManager.ClickTileCommand tileCmd) {
            changeState("click_move", new MCESClickMove.MoveStateArgs(tileCmd.getVector()));
        }
        else if (data instanceof MCInputManager.DirectionalCommand) {
            System.out.println("Oh on a pressé les touches du clavier");
        } else if (data instanceof MCInputManager.OtherKeyCommand keyCmd) {
            if (keyCmd.key == Input.Keys.F) {
                MCCameraManager camManager = MCCameraManager.get();
                if (camManager.getMode() == MCCameraMode.FOLLOW) camManager.setMode(MCCameraMode.ARROWS);
                else camManager.setMode(MCCameraMode.FOLLOW);
            }
        }
    }
}
