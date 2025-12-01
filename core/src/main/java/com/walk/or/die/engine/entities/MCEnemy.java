package com.walk.or.die.engine.entities;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.ai.MCAI;
import com.walk.or.die.engine.shared.MCIntVector2;
import com.walk.or.die.engine.sm.MCStateMachine;
import com.walk.or.die.engine.sm.entity.MCEntityState;
import com.walk.or.die.engine.sm.entity.states.MCESClickMove;
import com.walk.or.die.engine.sm.entity.states.MCESEnemyIdle;
import com.walk.or.die.engine.sm.entity.states.MCESIdle;
import com.walk.or.die.engine.sm.entity.states.MCESShoot;
import com.walk.or.die.engine.tiledmap.MCPathfinder;
import com.walk.or.die.engine.tiledmap.MCTerrainMap;

public class MCEnemy extends MCCharacter{

    private MCAI ai;
    private int current_decision = 0;
    private Runnable action_finished;

    public MCEnemy(MCGame parent, MCTerrainMap map, String entityGenericName) throws Exception {
        super(parent, map, entityGenericName);
        ai = new MCAI(map);
        MCStateMachine stateManager = getStateManager();
        stateManager.addState(new MCESEnemyIdle(this));
        stateManager.addState(new MCESClickMove(this));
        stateManager.addState(new MCESShoot(this));
        stateManager.setCurrentState("idle", new MCESIdle.IdleStateArgs());
        stateManager.setCallback(this::nextDecision);
    }

    public boolean playDecision(Runnable callback) {
        action_finished = callback;
        current_decision = 0;
        MCIntVector2 pos = ai.getNewPos(getTilePosition(), getMaxMoves());
        if (getStateManager().getCurrentState() instanceof MCESEnemyIdle state) {
            state.play(pos);
            return true;
        }
        System.out.println("oulala faut pas play quand il est pas en idle non mais oh");
        return false;

        // J'avoue c'est une méthode de brigand
    }

    public boolean shootDecision(MCESEnemyIdle state) {
        MCAlly victim = ai.getBestShootableAlly(getTilePosition(), 4);
        System.out.println("shoot decision");
        if (victim != null)
            state.shoot(victim, MCPathfinder.get().getTrajectory(getTilePosition(), victim.getTilePosition()));
        else {action_finished.run();}
        return true;
    }

    public void nextDecision(Object prev, Object next) {
        if (current_decision == 0 && prev instanceof MCESClickMove moveState && next instanceof MCESEnemyIdle idleState) {
            shootDecision(idleState);
            current_decision++;
        }
        else if (current_decision == 1) 
            action_finished.run();
    }
    

}
