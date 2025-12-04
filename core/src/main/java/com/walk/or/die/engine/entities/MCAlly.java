package com.walk.or.die.engine.entities;

import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.sm.MCStateMachine;
import com.walk.or.die.engine.sm.entity.states.MCESAim;
import com.walk.or.die.engine.sm.entity.states.MCESClickMove;
import com.walk.or.die.engine.sm.entity.states.MCESDead;
import com.walk.or.die.engine.sm.entity.states.MCESHurt;
import com.walk.or.die.engine.sm.entity.states.MCESIdle;
import com.walk.or.die.engine.sm.entity.states.MCESReady;
import com.walk.or.die.engine.sm.entity.states.MCESShoot;
import com.walk.or.die.engine.tiledmap.MCTerrainMap;

public class MCAlly extends MCCharacter {
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

    public AllyTurnState getTurnState() {
        return turnState;
    }
}
