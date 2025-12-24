package com.walk.or.die.engine.capacities;

import java.util.List;

import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.entities.MCEnemy;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.shared.MCEventBus;
import com.walk.or.die.engine.shared.MCIntVector2;
import com.walk.or.die.engine.sm.entity.character.states.MCCSIdle;
import com.walk.or.die.engine.sm.entity.character.states.MCCSShoot;
import com.walk.or.die.engine.tiledmap.MCPathfinder;

public class MCSpeedShoot extends MCEffects {
    
    private MCEntity.TileReachedArgs args;

    private boolean hasShoot = false;

    public MCSpeedShoot(MCCharacter parent, String displayName) {
        super(parent, displayName);
        MCEventBus.get().on(this, "EntityTileReached", this::tileReached);
    }

    private void tileReached(MCEntity.TileReachedArgs args) {
        if (hasShoot) return ;
        this.args = args;

        if (args.entity instanceof MCEnemy c && parent.getAttack().isValidTile(args.tile)) {
            if (parent.isFreeze()) {
                MCEventBus.get().on(this, "unfreezeGame", this::delay);
            } else {
                shoot();
            }
        }
    }

    private void delay(Object n) {
        if (hasShoot) return ;
        if (parent.isFreeze()) return;
        MCEventBus.get().off(this, "unfreezeGame");
        shoot();
    }

    private void shoot() {
        System.out.println("Shoot");
        List<MCIntVector2> list = MCPathfinder.get().getBestTrajectory(parent.getTilePosition(), args.tile);

        if (MCPathfinder.get().isCorrectTrajectory(list, args.tile) == 1f && args.entity instanceof MCEnemy c) { // Est ce que je tire seulement si je suis sur de réussir le shoot ?
            hasShoot = true;
            parent.getStateManager().setCurrentState("speedShoot", new MCCSShoot.ShootStateArgs(c, parent.getAttack(), list));
            setDispose(true);
            parent.removeEffect(name);
        }
    }

    @Override
    public void setDispose(boolean bool) {
        if (bool) {
            MCEventBus.get().off(this, "EntityTileReached");
            MCEventBus.get().off(this, "unfreezeGame");
        }
        super.setDispose(bool);
    }

    @Override
    public void onNewTurn() {
        setDispose(true);
    }

}
