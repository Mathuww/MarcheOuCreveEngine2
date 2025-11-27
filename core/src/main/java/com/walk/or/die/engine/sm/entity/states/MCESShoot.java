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
import com.walk.or.die.engine.sm.entity.states.MCESClickMove;

import java.util.ArrayList;
import java.util.List;


public class MCESShoot extends MCEntityState<MCESShoot.ShootStateArgs> {

    public static class ShootStateArgs extends MCEntityState.StateArgs {
        MCCharacter target;

        public ShootStateArgs(MCCharacter e) {
            target = e;
        }
    }

    boolean finished = false;

    public MCESShoot(MCCharacter parent) {
        super(parent);
        this.name = "shoot";
    }

    @Override
    public void update(float delta) {
        if (finished) changeState("idle", new MCESIdle.IdleStateArgs()); 
    }

    @Override
    public void enter(ShootStateArgs args) {
        super.enter(args);
        finished = parent.shoot(args.target);

    }

    @Override
    public void exit() {
        super.exit();
    }
    
    @Override
    protected void inputPressed(MCInputManager.Command data) {}
}