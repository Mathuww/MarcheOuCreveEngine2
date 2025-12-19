package com.walk.or.die.engine.entities;

import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.sm.MCStateMachine;
import com.walk.or.die.engine.sm.entity.character.states.MCCSAim;
import com.walk.or.die.engine.sm.entity.character.states.MCCSClickMove;
import com.walk.or.die.engine.sm.entity.character.states.MCCSDead;
import com.walk.or.die.engine.sm.entity.character.states.MCCSHurt;
import com.walk.or.die.engine.sm.entity.character.states.MCCSIdle;
import com.walk.or.die.engine.sm.entity.character.states.MCCSReady;
import com.walk.or.die.engine.sm.entity.character.states.MCCSShoot;
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
        stateManager.addState(new MCCSClickMove(this));
        stateManager.addState(new MCCSIdle(this));
        stateManager.addState(new MCCSAim(this));
        stateManager.addState(new MCCSShoot(this));
        stateManager.addState(new MCCSReady(this));
        stateManager.addState(new MCCSHurt(this));
        stateManager.addState(new MCCSDead(this));
    }

    @Override
    public void newTurn() {
        super.newTurn();
        getTurnState().reset();
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
