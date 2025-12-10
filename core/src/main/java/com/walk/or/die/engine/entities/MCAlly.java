package com.walk.or.die.engine.entities;

import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.sm.MCStateMachine;
import com.walk.or.die.engine.sm.entity.character.states.MCESAim;
import com.walk.or.die.engine.sm.entity.character.states.MCESClickMove;
import com.walk.or.die.engine.sm.entity.character.states.MCESDead;
import com.walk.or.die.engine.sm.entity.character.states.MCESHurt;
import com.walk.or.die.engine.sm.entity.character.states.MCESIdle;
import com.walk.or.die.engine.sm.entity.character.states.MCESReady;
import com.walk.or.die.engine.sm.entity.character.states.MCESShoot;
import com.walk.or.die.engine.sm.entity.explorationplayer.states.MCESMoveExploration;
import com.walk.or.die.engine.tiledmap.MCTerrainMap;

/**
 * A character that can be controlled.
 */
public class MCAlly extends MCCharacter {
    /**
     * A class to manage the actions.
     */
    public class AllyTurnState {
        public boolean hasMoved = false;
        public boolean hasAttacked = false;
        public boolean hasUsedCapacity = false;
        
        public AllyTurnState() {}

        public void reset() {
            hasMoved = false;
            hasAttacked = false;
            hasUsedCapacity = false;
        }

        public boolean canMove() {return !hasMoved;}
        public boolean canAttack() {return !hasAttacked;}
        public boolean canUseCapacity() {return !hasUsedCapacity;}

        public void moved() {hasMoved = true;}
        public void attacked() {hasAttacked = true;}
        public void capacityUsed() {hasUsedCapacity = true;}
    }   

    private AllyTurnState turnState = new AllyTurnState();

    /**
     * The constructor.
     * @param parent
     * @param map
     * @param entityGenericName
     */
    public MCAlly(MCGame parent, MCTerrainMap map, String entityGenericName) {
        super(parent, map, entityGenericName);

        MCStateMachine stateManager = getStateManager();
        stateManager.addState(new MCESClickMove(this));
        stateManager.addState(new MCESIdle(this));
        stateManager.addState(new MCESAim(this));
        stateManager.addState(new MCESShoot(this));
        stateManager.addState(new MCESReady(this));
        stateManager.addState(new MCESHurt(this));
        stateManager.addState(new MCESDead(this));
    }

    /**
     * Get the current ally actions.
     * @return
     * @see AllyTurnState
     */
    public AllyTurnState getTurnState() {
        return turnState;
    }
}
