package com.walk.or.die.engine.entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.screens.MCGameScreen;
import com.walk.or.die.engine.sm.MCStateMachine;
import com.walk.or.die.engine.sm.entity.MCEntityState;
import com.walk.or.die.engine.sm.entity.states.MCESAim;
import com.walk.or.die.engine.sm.entity.states.MCESClickMove;
import com.walk.or.die.engine.sm.entity.states.MCESIdle;
import com.walk.or.die.engine.sm.entity.states.MCESReady;
import com.walk.or.die.engine.sm.entity.states.MCESShoot;
import com.walk.or.die.engine.tiledmap.MCGameMap;
import com.walk.or.die.engine.tiledmap.MCMap;

public class MCCharacter extends MCEntity {
    
    private String name;
    private int hp;
    private int maxDeplacements;
    private MCStateMachine<MCEntityState, MCEntity> stateManager;
    private Attack baseAttack;

    public MCCharacter(MCGameScreen parent, MCGameMap map, Vector2 spawn, TextureRegion baseRegion) {
        super(parent, map, spawn, baseRegion);
        
        stateManager = new MCStateMachine<>(this);
        stateManager.addState(new MCESClickMove(this));
        stateManager.addState(new MCESIdle(this));
        stateManager.addState(new MCESAim(this));
        stateManager.addState(new MCESShoot(this));
        stateManager.addState(new MCESReady(this));
        stateManager.setCurrentState("idle", new MCESIdle.IdleStateArgs());
        
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        stateManager.update(delta);
    }

    public MCStateMachine getStateManager() {
        return this.stateManager;
    }

    public void setStateManager(MCStateMachine<MCEntityState, MCEntity> stateManager) {
        this.stateManager = stateManager;
    }

    public boolean shoot(MCEntity target) {
        //int damage = baseAttack.getDamageTo(target);
        //target.getHurt(damage)
        return true;
    }

    public void getHurt(int damage) {
        System.out.println("J'ai pris " + damage + "dégats !");
    }
    

}
