package com.walk.or.die.engine.sm.entity.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.walk.or.die.engine.entities.MCAlly;
import com.walk.or.die.engine.entities.MCAttack;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.shared.MCIntVector2;
import com.walk.or.die.engine.sm.entity.MCEntityState;
import com.walk.or.die.engine.tiledmap.MCPathfinder;

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

    //boolean finished = false;

    public MCESShoot(MCCharacter parent) {
        super(parent);
        this.name = "shoot";
    }

    @Override
    public void update(float delta) {
        //if (finished) changeState("idle", new MCESIdle.IdleStateArgs()); 
    }

    @Override
    public void render(SpriteBatch batch) {
        
    }

    @Override
    public void enter(ShootStateArgs args) {
        super.enter(args);

        if (parent instanceof MCAlly ally)
            ally.getTurnState().attacked();

        parent.playAnimation(args.attack.getSenderAnim());
        MCPathfinder.Simulation result = MCPathfinder.get().isCorrectTrajectory(args.trajectory);
        if (result.success) {
            int damage = args.attack.getDamageTo(args.target);
            parent.shootThenCall(args.target.getTilePosition(), args.attack, () -> {
                args.target.getHurt(damage, args.attack.getTargetAnim());
                changeState("idle", new MCESIdle.IdleStateArgs());
            });
        } else { // miss shot !
           parent.shootThenCall(result.endPos, args.attack, () -> {
                System.out.println("MissShot");
                changeState("idle", new MCESIdle.IdleStateArgs());
            });
        }

    }

    @Override
    public void exit() {
        super.exit();
    }

    @Override
    public boolean isBlocking() {
        return true;
    }
    
    @Override
    protected void inputPressed(MCInputManager.Command data) {}

}