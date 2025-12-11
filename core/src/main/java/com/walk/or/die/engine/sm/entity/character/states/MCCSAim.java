package com.walk.or.die.engine.sm.entity.character.states;

import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.entities.MCAlly;
import com.walk.or.die.engine.entities.MCAttack;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.entities.MCEntityManager;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.shared.MCIntVector2;
import com.walk.or.die.engine.sm.entity.character.MCCharacterState;
import com.walk.or.die.engine.tiledmap.MCPathfinder;

/**
 * The state to symbolize when we choose the target to shoot.<br>
 * Name = "aim"
 */
public class MCCSAim extends MCCharacterState<MCCSAim.AimStateArgs> {
    private MCAttack attack;
    private MCIntVector2 tile = new MCIntVector2(-1, -1);
    /**
     * Class which represents args needed by the aim state to start.
     */
    public static class AimStateArgs extends MCCharacterState.StateArgs {
        public MCAttack attack;

        /**
         * Constructor
         * @param attack - the attack represented
         * @see MCAttack
         */
        public AimStateArgs(MCAttack attack) {
            this.attack = attack;
        }
    }

    public MCCSAim(MCCharacter parent) {
        super(parent);
        this.name = "aim";
    }

    @Override
    public void update(float delta) {
        //System.out.println("On respire le bon air de la nature");
    }

    @Override
    public void render(SpriteBatch batch) {
    }

    @Override
    public void renderOnGridOverlay(SpriteBatch batch) {
        attack.render(batch);
    }

    @Override
    public void enter(AimStateArgs args) {
        super.enter(args);
        parent.playAnimation("aim");
        this.bus.emit("connectMouseMoved", new MCInputManager.MouseListener(this::mouseMoved));
        this.attack = args.attack;
        attack.computeValidTilesDisplay();
        attack.display = true;
        MCInputManager.get().triggerMouseUpdate(); // Initialise la position de la souris
    }

    @Override
    public void exit() {
        attack.display = false;
        tile = new MCIntVector2(-1, -1);
        this.bus.emit("disconnectMouseMoved", null);
        super.exit();
    }

    private void cancel() {
        changeState("idle", new MCCSIdle.IdleStateArgs());
    }
    
    @Override
    protected void inputPressed(MCInputManager.Command data) {
        //System.out.println("Input pressed detect in Idle");
        if (data instanceof MCInputManager.ClickTileCommand tileCmd) {
            MCEntity e = MCEntityManager.get().getEntityFromTile(1, tileCmd.getIntVect());
            if (e != null && e instanceof MCCharacter c) { // plus tard : remplacer par MCEnemy !!!!
                // a améliorer plus trad !!!
                if (e instanceof MCAlly) {
                    cancel();
                    return;
                }

                MCPathfinder pathfinder = MCPathfinder.get();
                List<MCIntVector2> traj = pathfinder.getValidTrajectory(
                    parent.getTilePosition(),
                    tile);
                if (traj.size() <= 0) { // y tires sur soi meme !!!! il est fou ou quoi ????
                    cancel();
                    return;
                }
                if (attack.isValidTile(c.getTilePosition())) {
                    changeState("shoot", new MCCSShoot.ShootStateArgs(c, attack, traj));
                    return;
                }
            }
            cancel();
        }
        else if (!(data instanceof MCInputManager.DirectionalCommand bipboup)) {
            cancel();
        }
    }

    private void mouseMoved(Vector2 pos) {
        MCIntVector2 newPos = new MCIntVector2(pos);
        if (!tile.equals(newPos)) {
            tile = newPos;

            if (!attack.isValidTile(tile)) {
                attack.clearTrajectory();
                return;
            }

            List<MCIntVector2> traj = MCPathfinder.get().getTrajectory(parent.getTilePosition(), tile);
            if (traj.size() < 2) {
                attack.clearTrajectory();
                return;
            }

            attack.showTrajectory(traj);
        }
    }

}