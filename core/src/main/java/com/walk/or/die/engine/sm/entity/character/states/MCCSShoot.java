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
 * Represents the state of shooting at something.
 * <p>The state's name is "shoot".</p>
 */
public class MCCSShoot extends MCCharacterState<MCCSShoot.ShootStateArgs> {

    /**
     * Represents arguments needed by the shoot state to start.
     */
    public static class ShootStateArgs extends MCCharacterState.StateArgs {
        /**
         * Represents the target character.
         */
        MCCharacter target;
        /**
         * Represents the attack to be used.
         */
        MCAttack attack;
        /**
         * Represents the path of the bullet.
         */
        List<MCIntVector2> trajectory;

        /**
         * Initializes a new instance of the {@code ShootStateArgs} class.
         *
         * @param e The target character.
         * @param a The attack used.
         * @param traj The trajectory or path of the bullet.
         */
        public ShootStateArgs(MCCharacter e, MCAttack a, List<MCIntVector2> traj) {
            target = e;
            attack = a;
            trajectory = traj;
        }

    }

    /**
     * Initializes a new instance of the {@code MCCSShoot} class.
     *
     * @param parent The parent character.
     */
    public MCCSShoot(MCCharacter parent) {
        super(parent);
        this.name = "shoot";
    }

    /**
     * Called on each frame to update the state.
     *
     * @param delta The time elapsed since the last frame.
     */
    @Override
    public void update(float delta) {
        //if (finished) changeState("idle", new MCESIdle.IdleStateArgs());
    }

    /**
     * Called on each frame to render the state.
     *
     * @param batch The sprite batch used for rendering.
     */
    @Override
    public void render(SpriteBatch batch) {
        
    }

    /**
     * Called at the state's entrance.
     *
     * @param args The arguments for the shoot state.
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
     * Called at the state's exit.
     */
    @Override
    public void exit() {
        super.exit();
    }
    
    /**
     * Handles input commands when a button is pressed.
     *
     * @param data The input command data received.
     */
    @Override
    protected void inputPressed(MCInputManager.Command data) {}

}