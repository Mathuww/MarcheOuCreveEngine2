package com.walk.or.die.engine.entities;

import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.sm.MCStateMachine;
import com.walk.or.die.engine.sm.entity.states.MCESAim;
import com.walk.or.die.engine.sm.entity.states.MCESClickMove;
import com.walk.or.die.engine.sm.entity.states.MCESIdle;
import com.walk.or.die.engine.sm.entity.states.MCESReady;
import com.walk.or.die.engine.sm.entity.states.MCESShoot;
import com.walk.or.die.engine.tiledmap.MCTerrainMap;

public class MCAlly extends MCCharacter {
    public MCAlly(MCGame parent, MCTerrainMap map, String entityGenericName) {
        super(parent, map, entityGenericName);

        MCStateMachine stateManager = getStateManager();
        stateManager.addState(new MCESClickMove(this));
        stateManager.addState(new MCESIdle(this));
        stateManager.addState(new MCESAim(this));
        stateManager.addState(new MCESShoot(this));
        stateManager.addState(new MCESReady(this));
        stateManager.setCurrentState("idle", new MCESIdle.IdleStateArgs());
    }
}
