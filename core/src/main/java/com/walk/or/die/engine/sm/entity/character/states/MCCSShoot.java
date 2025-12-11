package com.walk.or.die.engine.sm.entity.character.states;

import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.walk.or.die.engine.entities.MCAlly;
import com.walk.or.die.engine.entities.MCAttack;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.shared.MCIntVector2;
import com.walk.or.die.engine.sm.entity.character.MCCharacterState;
import com.walk.or.die.engine.tiledmap.MCPathfinder;

/**
 * The state of shooting on something.<br>
 * Name = "shoot"
 */
public class MCCSShoot extends MCCharacterState<MCCSShoot.ShootStateArgs> {

    /**
     * Class wich represents args needed by shoot state to start.
     */
    public static class ShootStateArgs extends MCCharacterState.StateArgs {
        MCCharacter target;
        MCAttack attack;
        List<MCIntVector2> trajectory;

        /**
         * The constructor.
         * @param e - the target
         * @param a - the used attack
         * @param traj - path of the bullet
         */
        public ShootStateArgs(MCCharacter e, MCAttack a, List<MCIntVector2> traj) {
            target = e;
            attack = a;
            trajectory = traj;
        }

    }

    /**
     * The constructor.
     * @param parent
     */
    public MCCSShoot(MCCharacter parent) {
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
                changeState("idle", new MCCSIdle.IdleStateArgs());
            });
        } else { // miss shot !
           parent.shootThenCall(result.endPos, args.attack, () -> {
                System.out.println("MissShot");
                changeState("idle", new MCCSIdle.IdleStateArgs());
            });
        }

    }

    @Override
    public void exit() {
        super.exit();
    }
    
    @Override
    protected void inputPressed(MCInputManager.Command data) {}

}