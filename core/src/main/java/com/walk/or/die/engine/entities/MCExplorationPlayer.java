package com.walk.or.die.engine.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.shared.MCUtils;
import com.walk.or.die.engine.sm.MCStateMachine;
import com.walk.or.die.engine.sm.entity.character.states.MCCSHurt;
import com.walk.or.die.engine.sm.entity.character.states.MCCSIdle;
import com.walk.or.die.engine.sm.entity.explorationplayer.MCExplorationPlayerState;
import com.walk.or.die.engine.sm.entity.explorationplayer.states.MCEPSIdle;
import com.walk.or.die.engine.sm.entity.explorationplayer.states.MCEPSMove;
import com.walk.or.die.engine.tiledmap.MCTerrainMap;

public class MCExplorationPlayer extends MCEntity {
    private Integer hp;
    private boolean dead = false;
    private Float speed;
    private Float toleranceHitbox;
    private MCStateMachine<MCExplorationPlayerState, MCEntity> stateManager;

    public MCExplorationPlayer(MCGame parent, MCTerrainMap map, String entityGenericName) {
        super(parent, map, entityGenericName);
        stateManager = new MCStateMachine<>(this);
        stateManager.addState(new MCEPSIdle(this));
        stateManager.addState(new MCEPSMove(this));
    }

    @Override
    public void onSpawn() {
        stateManager.setCurrentState("idle", new MCEPSIdle.IdleStateArgs());
        //playAnimation("idle");
    }

    @Override
    public void initFromProperties(MapProperties props) throws Exception {
        hp = MCUtils.getIntProperty(props, "hp", 100);
        speed = MCUtils.getFloatProperty(props, "speed", 2);
        toleranceHitbox = (MCUtils.getFloatProperty(props, "hitboxTolerancePercentage", 0.05f))/(100*2);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        stateManager.update(delta);
    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);
        //stateManager.render(batch);
    }

    public void renderOnGridOverlay(SpriteBatch batch) {
        //stateManager.renderOnGridOverlay(batch);
    }

    public void setStateManager(MCStateMachine<MCExplorationPlayerState, MCEntity> stateManager) {
        this.stateManager = stateManager;
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
        stateManager.setCurrentState("hurt", new MCCSHurt.HurtStateArgs(damage, targetAnim));
        System.out.println("J'ai pris " + damage + "dégats !");
    }
    
    public Float getToleranceHitbox() {
        return toleranceHitbox;
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

    public MCStateMachine getStateManager() {
        return stateManager;
    }
}