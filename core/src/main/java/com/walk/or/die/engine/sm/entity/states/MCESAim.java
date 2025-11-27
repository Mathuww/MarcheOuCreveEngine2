package com.walk.or.die.engine.sm.entity.states;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.cameras.MCCameraManager;
import com.walk.or.die.engine.entities.MCAttack;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.entities.MCEntity;
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
    public void enter(AimStateArgs args) {
        super.enter(args);
        this.attack = args.attack;
        attack.update();
        attack.display = true;
    }

    @Override
    public void exit() {
        attack.display = false;
        super.exit();
    }
    
    @Override
    protected void inputPressed(MCInputManager.Command data) {
        //System.out.println("Input pressed detect in Idle");
        if (data instanceof MCInputManager.ClickTileCommand tileCmd) {
            MCEntity e = parent.getParent().getEntityFromTile(1, tileCmd.getVector());
            if (e != null && e instanceof MCCharacter c) {
                MCPathfinder pathfinder = MCPathfinder.get();
                List<Vector2> traj = pathfinder.getTrajectory(
                    MathUtils.floor(parent.getX()), 
                    MathUtils.floor(parent.getY()), 
                    MathUtils.floor(c.getPosition().x),
                    MathUtils.floor(c.getPosition().y));
                if (traj.size() < 2) { // y tires sur soi meme !!!! il est fou ou quoi ????
                    changeState("idle", new MCESIdle.IdleStateArgs());
                    return;
                }
                traj.remove(traj.size() - 1); // on prend pas en compte le dernier, c'est la cible (donc forcément pas walkable)
                traj.remove(0); // l'attaquant occupe forcément aussi une case
                if (attack.isValidTile(c.getPosition()) && pathfinder.isCorrectTrajectory(traj)) {
                    changeState("shoot", new MCESShoot.ShootStateArgs(c, attack));
                    return;
                }
            }
            changeState("idle", new MCESIdle.IdleStateArgs());
        }
        else if (!(data instanceof MCInputManager.DirectionalCommand bipboup)) {
            changeState("idle", new MCESIdle.IdleStateArgs());
        }
    }

}