package com.walk.or.die.engine.sm.entity.character.states;

import com.walk.or.die.engine.cameras.MCCameraManager.CameraMode;
import com.walk.or.die.engine.capacities.MCSpeedShoot;
import com.walk.or.die.engine.entities.MCAlly;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.sm.entity.character.MCCharacterState;
import com.walk.or.die.engine.sm.entity.character.states.MCCSShoot.ShootStateArgs;
import com.walk.or.die.engine.tiledmap.MCPathfinder;

public class MCCSSpeedShoot extends MCCharacterState<MCCSShoot.ShootStateArgs> {
    
    public MCCSSpeedShoot(MCCharacter parent) {
        super(parent);
        this.name = "speedShoot";
    }

    /**
     * Called at state entrance.
     * @param args the shoot state arguments.
     */
    @Override
    public void enter(MCCSShoot.ShootStateArgs args) {
        super.enter(args);
        bus.emit("freezeGame", parent);
        //System.out.println(this);
        parent.getHudCustomization().canShow = true;
        parent.notifyHudUpdate(true);

        parent.playAnimation(args.attack.getSenderAnim());
        MCPathfinder.Simulation result = MCPathfinder.get().simulateTrajectory(args.trajectory, args.target.getTilePosition());
        if (result.success) {
            int damage = args.attack.getDamageTo(args.target);
            //System.out.println("On tire bien comme prévu");
            parent.shootThenCall(args.target.getTilePosition(), args.attack, () -> {
                if (camManager.getMode() == CameraMode.FOLLOW)
                    camManager.setFollowTarget(args.target);
                args.target.getHurt(damage);
                //System.out.println("Et mtn le callback");
                changeState("idle", new MCCSIdle.IdleStateArgs());
            });
        } else {
            System.out.println("this shouldn't be possible, what's is happening here ? Where am i ?");
        }
    }
    
    /**
     * Called at state exit.
     */
    @Override
    public void exit() {
        bus.emit("unfreezeGame", null);
        //System.out.println("Yey;" + this);
        super.exit();
    }

}