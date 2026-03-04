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

    /** Holds the tile reached arguments. */
    private MCEntity.TileReachedArgs args;

    /** Indicates whether a shoot action has been performed. */
    private boolean hasShoot = false;

    /**
     * Constructs a new MCSpeedShoot object.
     *
     * @param parent The parent character.
     */
    public MCSpeedShoot(MCCharacter parent) {
        super(parent, "SPEED SHOOT");
        MCEventBus.get().on(this, "EntityTileReached", this::tileReached);
    }

    /**
     * Returns a summary of the effect.
     *
     * @return A summary string for the effect.
     */
    @Override
    public String getSummary() {
        return "This ally will return every attack to the sender for one turn.";
    }

    /**
     * Creates a copy of this effect for a given target.
     *
     * @param target The character to apply the copied effect to.
     * @return A new instance of the effect copied for the target.
     */
    @Override
    public MCEffects copy(MCCharacter target) {
        return new MCSpeedShoot(target);
    }

    /**
     * Handles the tile reached event.
     *
     * @param args The tile reached arguments.
     */
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

    /**
     * Delays the shoot action.
     *
     * @param n The object that triggers the delay.
     */
    private void delay(Object n) {
        if (hasShoot) return ;
        if (parent.isFreeze()) return;
        MCEventBus.get().off(this, "unfreezeGame");
        shoot();
    }

    /**
     * Performs the shoot action.
     */
    private void shoot() {
        List<MCIntVector2> list = MCPathfinder.get().getBestTrajectory(parent.getTilePosition(), args.tile);

        if (MCPathfinder.get().isCorrectTrajectory(list, args.tile) == 1f && args.entity instanceof MCEnemy c) { // Est ce que je tire seulement si je suis sur de réussir le shoot ?
            hasShoot = true;
            parent.getStateManager().setCurrentState("speedShoot", new MCCSShoot.ShootStateArgs(c, parent.getAttack(), list));
            setDispose(true);
            parent.removeEffect(name);
        }
    }

    /**
     * Sets the dispose state of the effect.
     *
     * @param bool The dispose state.
     */
    @Override
    public void setDispose(boolean bool) {
        if (bool) {
            MCEventBus.get().off(this, "EntityTileReached");
            MCEventBus.get().off(this, "unfreezeGame");
        }
        super.setDispose(bool);
    }

    /**
     * Called on a new turn.
     */
    @Override
    public void onNewTurn() {
        setDispose(true);
    }

}