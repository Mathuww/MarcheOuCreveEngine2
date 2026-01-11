package com.walk.or.die.engine.entities;

import java.util.List;

import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.ai.MCAI;
import com.walk.or.die.engine.shared.MCIntVector2;
import com.walk.or.die.engine.sm.MCStateMachine;
import com.walk.or.die.engine.sm.entity.character.states.MCCSClickMove;
import com.walk.or.die.engine.sm.entity.character.states.MCCSDead;
import com.walk.or.die.engine.sm.entity.character.states.MCCSEnemyIdle;
import com.walk.or.die.engine.sm.entity.character.states.MCCSHurt;
import com.walk.or.die.engine.sm.entity.character.states.MCCSShoot;
import com.walk.or.die.engine.sm.entity.character.states.MCCSSpeedShoot;
import com.walk.or.die.engine.sm.entity.explorationplayer.states.MCEPSMove;
import com.walk.or.die.engine.tiledmap.MCPathfinder;
import com.walk.or.die.engine.tiledmap.MCTerrainMap;

/**
 * A character run by an AI.
 */
public class MCEnemy extends MCCharacter{

    private MCAI ai;
    private int current_decision = 0;
    private Runnable action_finished;

    /**
     * The constructor.
     * @param parent The parent MCGame.
     * @param map The MCTerrainMap.
     * @param entityGenericName The generic name of the entity.
     * @throws Exception If an error occurs during initialization.
     */
    public MCEnemy(MCGame parent, MCTerrainMap map, String entityGenericName) throws Exception {
        super(parent, map, entityGenericName);
        ai = new MCAI(map, this);
        MCStateMachine stateManager = getStateManager();
        stateManager.addState(new MCCSEnemyIdle(this));
        stateManager.addState(new MCCSClickMove(this));
        stateManager.addState(new MCCSShoot(this));
        stateManager.addState(new MCCSHurt(this));
        stateManager.addState(new MCCSDead(this));
        stateManager.addState(new MCCSSpeedShoot(this));
        stateManager.setCallback(this::nextDecision);
    }

    /**
     * Starts to play.
     * @param callback The callback to execute after the decision.
     * @return True if the decision was played, false otherwise.
     */
    public boolean playDecision(Runnable callback) {
        action_finished = callback;
        current_decision = 0;
        MCIntVector2 pos = ai.getNewPos(getTilePosition(), getMaxMoves());
        if (getStateManager().getCurrentState() instanceof MCCSEnemyIdle state) {
            state.play(pos);
            return true;
        }
        System.out.println("oulala faut pas play quand il est pas en idle non mais oh");
        return false;

        // J'avoue c'est une méthode de brigand
        // .... plus maintenant eheh
    }

    /**
     * Shoots.
     * @param state The enemy idle state.
     * @return True if the shoot was executed.
     */
    public boolean shootDecision(MCCSEnemyIdle state) {
        MCAlly victim = ai.getBestShootableAlly(getTilePosition(), 4);
        //System.out.println("shoot decision");
        if (victim != null) {
            List<MCIntVector2> traj = MCPathfinder.get().getBestTrajectory(getTilePosition(), victim.getTilePosition());
            state.shoot(victim, traj);
        }
        else 
            action_finished.run();
        return true;
    }

    /**
     * Handles decisions.
     * @param prev The previous state.
     * @param next The next state.
     */
    public void nextDecision(Object prev, Object next) {
        if (current_decision == 0 && prev instanceof MCCSClickMove moveState && next instanceof MCCSEnemyIdle idleState) {
            shootDecision(idleState);
            current_decision++;
        }
        else if (current_decision == 1) 
            action_finished.run();
    }
    
}