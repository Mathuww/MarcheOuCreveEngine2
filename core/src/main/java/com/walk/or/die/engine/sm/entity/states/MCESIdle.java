package com.walk.or.die.engine.sm.entity.states;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
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
        bus.on(this, "ChangeStateCommand", this::onChangeStateCommand);
        super.enter(args);
    }

    @Override
    public void exit() {
        parent.keep = true;
        bus.off(this, "ChangeStateCommand");
        super.exit();
    }
    
    @Override
    protected void inputPressed(MCInputManager.Command data) {

    }

    private void onChangeStateCommand(MCCharacter.ChangeStateCommandEvent evt) {
        if (!evt.character.equals(parent))
            return; // pas pour nous !

        switch (evt.newState) {
            case READY -> changeState("ready", new MCESReady.ReadyStateArgs());
            case AIM -> changeState("aim", new MCESAim.AimStateArgs(parent.getAttack()));
        }
    }
}
