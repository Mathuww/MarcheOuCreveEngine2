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
 * Represents a character controlled by an AI.
 */
public class MCEnemy extends MCCharacter{

    /**
     * The AI controller for this enemy.
     */
    private MCAI ai;
    /**
     * Tracks the current decision index being processed.
     */
    private int current_decision = 0;
    /**
     * The callback to execute when the current action is finished.
     */
    private Runnable action_finished;

    /**
     * Constructs an MCEnemy instance.
     * @param parent The parent MCGame instance.
     * @param map The terrain map for the enemy.
     * @param entityGenericName The generic name of this entity.
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
     * Initiates the enemy's decision-making process.
     * @param callback The callback to execute after the decision is completed.
     * @return True if the decision was successfully initiated, false otherwise.
     */
    public boolean playDecision(Runnable callback) {
        action_finished = callback;
        current_decision = 0;
        MCIntVector2 pos = ai.getNewPos(getTilePosition(), getMaxMoves());
        if (getStateManager().getCurrentState() instanceof MCCSEnemyIdle state) {
            state.play(pos);
            return true;
        }
        return false;

        // J'avoue c'est une méthode de brigand
        // .... plus maintenant eheh
    }

    /**
     * Executes a shooting decision.
     * @param state The enemy idle state.
     * @return True if the shooting decision was executed.
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
     * Handles the transition between states to process decisions.
     * @param prev The previous state object.
     * @param next The next state object.
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