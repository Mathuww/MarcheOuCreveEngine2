package com.walk.or.die.engine.sm.entity.states;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.cameras.MCCameraManager;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.shared.MCEventBus;
import com.walk.or.die.engine.sm.MCState;
import com.walk.or.die.engine.sm.MCState.StateArgs;
import com.walk.or.die.engine.sm.entity.MCEntityState;

import java.util.ArrayList;
import java.util.List;


public class MCESIdle extends MCEntityState<MCESIdle.IdleStateArgs> {

    public static class IdleStateArgs extends MCEntityState.StateArgs {}

    public MCESIdle(MCCharacter parent) {
        super(parent);
        this.name = "idle";
    }

    @Override
    public void update(float delta) {
        //System.out.println("On respire le bon air de la nature");
    }

    @Override
    public void enter(IdleStateArgs args) {
        parent.keep = false;
        parent.playAnimation("idle");
        super.enter(args);
    }

    @Override
    public void exit() {
        parent.keep = true;
        super.exit();
    }
    
    @Override
    protected void inputPressed(MCInputManager.Command data) {
        //System.out.println("Input pressed detect in Idle");
        if (!parent.focus) return;
        if (data instanceof MCInputManager.ReadyCommand) {
            changeState("ready", new MCESReady.ReadyStateArgs());
            
        } else if (data instanceof MCInputManager.AimCommand) {
            System.out.println("oh");
            changeState("aim", new MCESAim.AimStateArgs(parent.getAttack()));
        }
    }

}
