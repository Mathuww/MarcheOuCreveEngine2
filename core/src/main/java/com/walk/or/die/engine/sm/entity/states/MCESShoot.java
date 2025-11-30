package com.walk.or.die.engine.sm.entity.states;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.cameras.MCCameraManager;
import com.walk.or.die.engine.entities.MCAttack;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.shared.MCEventBus;
import com.walk.or.die.engine.shared.MCIntVector2;
import com.walk.or.die.engine.sm.MCState;
import com.walk.or.die.engine.sm.MCState.StateArgs;
import com.walk.or.die.engine.sm.entity.MCEntityState;
import com.walk.or.die.engine.sm.entity.states.MCESClickMove;
import com.walk.or.die.engine.tiledmap.MCPathfinder;

import java.util.ArrayList;
import java.util.List;


public class MCESShoot extends MCEntityState<MCESShoot.ShootStateArgs> {

    public static class ShootStateArgs extends MCEntityState.StateArgs {
        MCCharacter target;
        MCAttack attack;
        List<MCIntVector2> trajectory;

        public ShootStateArgs(MCCharacter e, MCAttack a, List<MCIntVector2> traj) {
            target = e;
            attack = a;
            trajectory = traj;
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
    public void render(SpriteBatch batch) {
        
    }

    @Override
    public void enter(ShootStateArgs args) {
        super.enter(args);
        MCPathfinder.Simulation result = MCPathfinder.get().isCorrectTrajectory(args.trajectory);
        if (result.success) {
            finished = parent.shoot(args.target, args.attack);
        } else {
            finished = parent.missShoot(result.endPos);
        }

    }

    @Override
    public void exit() {
        super.exit();
    }
    
    @Override
    protected void inputPressed(MCInputManager.Command data) {}
}