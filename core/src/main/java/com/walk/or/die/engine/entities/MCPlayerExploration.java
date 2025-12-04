package com.walk.or.die.engine.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.shared.MCUtils;
import com.walk.or.die.engine.sm.MCStateMachine;
import com.walk.or.die.engine.sm.entity.MCEntityState;
import com.walk.or.die.engine.sm.entity.states.MCESHurt;
import com.walk.or.die.engine.sm.entity.states.MCESIdle;
import com.walk.or.die.engine.tiledmap.MCTerrainMap;

public class MCPlayerExploration extends MCEntity {
    private Integer hp;
    private boolean dead = false;
    private Integer maxDeplacements;
    private MCStateMachine<MCEntityState, MCEntity> stateManager;

    public MCPlayerExploration(MCGame parent, MCTerrainMap map, String entityGenericName) {
        super(parent, map, entityGenericName);
        stateManager = new MCStateMachine<>(this);
    }

    @Override
    public void onSpawn() {
        stateManager.setCurrentState("idle", new MCESIdle.IdleStateArgs());
    }

    @Override
    public void initFromProperties(MapProperties props) throws Exception {
        hp = MCUtils.getIntProperty(props, "hp", 100);
        maxDeplacements = MCUtils.getIntProperty(props, "maxMoves", 2);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        stateManager.update(delta);
    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);
        stateManager.render(batch);
    }

    public void renderOnGridOverlay(SpriteBatch batch) {
        stateManager.renderOnGridOverlay(batch);
    }

    public MCStateMachine getStateManager() {
        return this.stateManager;
    }

    public void setStateManager(MCStateMachine<MCEntityState, MCEntity> stateManager) {
        this.stateManager = stateManager;
    }

    public int getMaxMoves() {
        return this.maxDeplacements;
    }
    
    public int getHealth() {
        return hp;
    }

    public void getHurt(int damage, String targetAnim) {
        if (dead)
            return;
        if (damage < 0f) 
            throw new IllegalArgumentException("cant get hurt with negative damage");
        hp = Math.max(0, hp - damage);
        stateManager.setCurrentState("hurt", new MCESHurt.HurtStateArgs(damage, targetAnim));
        System.out.println("J'ai pris " + damage + "dégats !");
    }
    
    public boolean isDead() {
        return dead;
    }

    public void setDead() {
        dead = true;
    }

    public boolean isBusy() {
        if (stateManager.getCurrentState() == null) 
            return false;
        return stateManager.getCurrentState().isBlocking();
    }

}