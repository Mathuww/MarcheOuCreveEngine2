package com.walk.or.die.engine.sm.entity.states;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.cameras.MCCameraManager;
import com.walk.or.die.engine.entities.MCAlly;
import com.walk.or.die.engine.entities.MCAttack;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.entities.MCEntityManager;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.shared.MCEventBus;
import com.walk.or.die.engine.sm.MCState;
import com.walk.or.die.engine.sm.MCState.StateArgs;
import com.walk.or.die.engine.sm.entity.MCEntityState;
import com.walk.or.die.engine.sm.entity.states.MCESClickMove;
import com.walk.or.die.engine.tiledmap.MCPathfinder;

import java.util.ArrayList;
import java.util.List;


public class MCESAim extends MCEntityState<MCESAim.AimStateArgs> {
    private MCAttack attack;
    private int tileX = -1;
    private int tileY = -1;
    public static class AimStateArgs extends MCEntityState.StateArgs {
        public MCAttack attack;

        public AimStateArgs(MCAttack attack) {
            this.attack = attack;
        }
    }

    public MCESAim(MCCharacter parent) {
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
        this.bus.emit("connectMouseMoved", new MCInputManager.Function(this::mouseMoved));
        this.attack = args.attack;
        attack.computeValidTilesDisplay();
        attack.display = true;
        // Pour régler le bug qui se produit si on bouge pas la souris en entrant dans Aim 
        // (ca actualise pas la traj + ca fait miss shot car tileX et tileY changent pas)
        MCInputManager.get().triggerMouseUpdate();
    }

    @Override
    public void exit() {
        attack.display = false;
        tileX = -1;
        tileY = -1;
        this.bus.emit("disconnectMouseMoved", null);
        super.exit();
    }
    
    @Override
    protected void inputPressed(MCInputManager.Command data) {
        //System.out.println("Input pressed detect in Idle");
        if (data instanceof MCInputManager.ClickTileCommand tileCmd) {
            MCEntity e = MCEntityManager.get().getEntityFromTile(1, tileCmd.getVector());
            if (e != null && e instanceof MCCharacter c) { // plus tard : remplacer par MCEnemy !!!!
                // a améliorer plus trad !!!
                if (e instanceof MCAlly) {
                    changeState("idle", new MCESIdle.IdleStateArgs());
                    return;
                }

                MCPathfinder pathfinder = MCPathfinder.get();
                List<Vector2> traj = pathfinder.getTrajectory(
                    MathUtils.floor(parent.getX()), 
                    MathUtils.floor(parent.getY()), 
                    MathUtils.floor(tileX),
                    MathUtils.floor(tileY));
                if (traj.size() < 2) { // y tires sur soi meme !!!! il est fou ou quoi ????
                    changeState("idle", new MCESIdle.IdleStateArgs());
                    return;
                }
                traj.remove(traj.size() - 1); // on prend pas en compte le dernier, c'est la cible (donc forcément pas walkable)
                traj.remove(0); // l'attaquant occupe forcément aussi une case
                if (attack.isValidTile(c.getPosition())) {
                    changeState("shoot", new MCESShoot.ShootStateArgs(c, attack, traj));
                    return;
                }
            }
            changeState("idle", new MCESIdle.IdleStateArgs());
        }
        else if (!(data instanceof MCInputManager.DirectionalCommand bipboup)) {
            changeState("idle", new MCESIdle.IdleStateArgs());
        }
    }


    private void mouseMoved(Vector2 pos) {
        int newx = MathUtils.floor(pos.x);
        int newy = MathUtils.floor(pos.y);
        if (newx != tileX || newy != tileY) {
            tileX = newx;
            tileY = newy;

            if (!attack.isValidTile(new Vector2(tileX, tileY))) {
                attack.clearTrajectory();
                return;
            }
            int startx = MathUtils.floor(parent.getX());
            int starty = MathUtils.floor(parent.getY());

            List<Vector2> traj = MCPathfinder.get().getTrajectory(startx, starty, tileX, tileY);
            if (traj.size() < 2) {
                attack.clearTrajectory();
                return;
            }


            
            System.out.println("switching trajectory");
            attack.showTrajectory(traj);
        }
    }

}