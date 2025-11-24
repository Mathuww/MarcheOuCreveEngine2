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
import com.walk.or.die.engine.sm.entity.states.MCESClickMove;

import java.util.ArrayList;
import java.util.List;


public class MCESAim extends MCEntityState<MCESAim.AimStateArgs> {

    public static class AimStateArgs extends MCEntityState.StateArgs {}

    public MCESAim(MCEntity parent) {
        super(parent);
        this.name = "aim";
    }

    @Override
    public void update(float delta) {
        //System.out.println("On respire le bon air de la nature");
    }

    @Override
    public void enter(AimStateArgs args) {
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
            MCEntity e = parent.getParent().getEntityFromTile(1, tileCmd.getVector());
            if (e != null) {
                changeState("shoot", new MCESShoot.ShootStateArgs(e));
                return;
            }
            changeState("idle", new MCESIdle.IdleStateArgs());
        }
        else {
            changeState("idle", new MCESIdle.IdleStateArgs());
        }
    }

}