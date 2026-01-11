package com.walk.or.die.engine.sm.entity.character.states;

import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.walk.or.die.engine.cameras.MCCameraManager;
import com.walk.or.die.engine.cameras.MCCameraManager.CameraMode;
import com.walk.or.die.engine.entities.MCAlly;
import com.walk.or.die.engine.entities.MCAttack;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.shared.MCIntVector2;
import com.walk.or.die.engine.sm.entity.character.MCCharacterState;
import com.walk.or.die.engine.tiledmap.MCPathfinder;
import com.walk.or.die.engine.ui.MCHUDHPBar;
import com.walk.or.die.engine.ui.MCHUDManager;

/**
 * The state of shooting on something.<br>
 * Name = "shoot"
 */
public class MCCSShoot extends MCCharacterState<MCCSShoot.ShootStateArgs> {

    /**
     * Class which represents args needed by shoot state to start.
     */
    public static class ShootStateArgs extends MCCharacterState.StateArgs {
        MCCharacter target;
        MCAttack attack;
        List<MCIntVector2> trajectory;

        /**
         * The constructor.
         * @param e The target
         * @param a The used attack
         * @param traj Path of the bullet
         */
        public ShootStateArgs(MCCharacter e, MCAttack a, List<MCIntVector2> traj) {
            target = e;
            attack = a;
            trajectory = traj;
        }

    }

    /**
     * The constructor.
     * @param parent The parent
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

    /**
     * Called at state entrance.
     * @param args The shoot state arguments
     */
    @Override
    public void enter(ShootStateArgs args) {
        super.enter(args);
        parent.getHudCustomization().canShow = true;
        parent.notifyHudUpdate(true);

        if (parent instanceof MCAlly ally)
            ally.getTurnState().attacked();

        parent.playAnimation(args.attack.getSenderAnim());
        MCPathfinder.Simulation result = MCPathfinder.get().simulateTrajectory(args.trajectory, args.target.getTilePosition());
        if (result.success) {
            int damage = args.attack.getDamageTo(args.target);
            parent.shootThenCall(args.target.getTilePosition(), args.attack, () -> {
                if (camManager.getMode() == CameraMode.FOLLOW)
                    camManager.setFollowTarget(args.target);
                args.target.getHurt(damage);
                changeState("idle", new MCCSIdle.IdleStateArgs());
            });
        } else { // miss shot !
           parent.shootThenCall(result.endPos, args.attack, () -> {
                //System.out.println("MissShot");
                changeState("idle", new MCCSIdle.IdleStateArgs());
            });
        }

    }

    /**
     * Called at state exit.
     */
    @Override
    public void exit() {
        super.exit();
    }
    
    /**
     * @param data The input command data
     */
    @Override
    protected void inputPressed(MCInputManager.Command data) {}

}