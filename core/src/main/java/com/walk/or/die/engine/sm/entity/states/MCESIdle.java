package com.walk.or.die.engine.sm.entity.states;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.MCEventBus;
import com.walk.or.die.engine.cameras.MCCameraManager;
import com.walk.or.die.engine.cameras.MCCameraMode;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.sm.MCState;
import com.walk.or.die.engine.sm.MCState.StateArgs;
import com.walk.or.die.engine.sm.entity.MCEntityState;

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
        if (!parent.focus) return;
        if (data instanceof MCInputManager.ClickTileCommand tileCmd) {
            changeState("click_move", new MCESClickMove.MoveStateArgs(tileCmd.getVector()));
        }
        else if (data instanceof MCInputManager.AimCommand) {
            System.out.println("oh");
            changeState("aim", new MCESAim.AimStateArgs());
        }
        else if (data instanceof MCInputManager.DirectionalCommand) {
            System.out.println("Oh on a pressé les touches du clavier");
        }
    }

}
