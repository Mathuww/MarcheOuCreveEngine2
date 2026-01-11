package com.walk.or.die.engine.entities;

import java.util.Map;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.shared.MCUtils;
import com.walk.or.die.engine.sm.MCStateMachine;
import com.walk.or.die.engine.sm.entity.character.states.MCCSHurt;
import com.walk.or.die.engine.sm.entity.explorationplayer.MCExplorationPlayerState;
import com.walk.or.die.engine.sm.entity.explorationplayer.states.MCEPSIdle;
import com.walk.or.die.engine.sm.entity.explorationplayer.states.MCEPSMove;
import com.walk.or.die.engine.tiledmap.MCTerrainMap;

public class MCExplorationPlayer extends MCEntity {
    private int hp;
    private int maxHp;
    private boolean dead = false;
    private Float toleranceHitbox;
    private MCStateMachine<MCExplorationPlayerState, MCEntity> stateManager;

    /**
     * Constructs a new MCExplorationPlayer.
     * @param parent The parent game.
     * @param map The terrain map.
     * @param entityGenericName The generic name of the entity.
     */
    public MCExplorationPlayer(MCGame parent, MCTerrainMap map, String entityGenericName) {
        super(parent, map, entityGenericName);
        stateManager = new MCStateMachine<>(this);
        stateManager.addState(new MCEPSIdle(this));
        stateManager.addState(new MCEPSMove(this));
    }

    /**
     * Constructs a new MCExplorationPlayer from an ally.
     * @param ally The ally to copy from.
     */
    public MCExplorationPlayer(MCAlly ally) {
        super(
            ally.getParent(),
            ally.getMap(),
            "explorationPlayer"
        );
        this.setMaxHp(ally.getMaxHp());
        this.setHealth(ally.getHealth());
        this.setPosition(ally.getPosition());

        Map<String, MCAnimation> animations = ally.getAnimationMap();
        for (String animName : animations.keySet()) {
            System.out.println("copying anim to expl player " + animName);
            MCAnimation anim = animations.get(animName);
            addAnimation(animName, anim);
        }
        
        stateManager = new MCStateMachine<>(this);
        stateManager.addState(new MCEPSIdle(this));
        stateManager.addState(new MCEPSMove(this));

        toleranceHitbox = ally.getToleranceHitbox();
        
    }

    /**
     * Called on spawn.
     */
    @Override
    public void onSpawn() {
        stateManager.setCurrentState("idle", new MCEPSIdle.IdleStateArgs());
    }

    /**
     * Initializes from properties.
     * @param props The properties to initialize from.
     * @throws Exception if an error occurs.
     */
    @Override
    public void initFromProperties(MapProperties props) throws Exception {
        maxHp = MCUtils.getIntProperty(props, "hp", 100);
        hp = maxHp;
        toleranceHitbox = (MCUtils.getFloatProperty(props, "hitboxTolerancePercentage", 0.05f))/(100*2);
    }

    /**
     * Called on each frame.
     * @param delta The time delta.
     */
    @Override
    public void update(float delta) {
        super.update(delta);
        stateManager.update(delta);
    }

    /**
     * Called on each frame.
     * @param batch The sprite batch.
     */
    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);
        stateManager.render(batch);
    }

    /**
     * Sets the state manager.
     * @param stateManager The state manager to set.
     */
    public void setStateManager(MCStateMachine<MCExplorationPlayerState, MCEntity> stateManager) {
        this.stateManager = stateManager;
    }

    /**
     * Gets the health.
     * @return The current health.
     */
    public int getHealth() {
        return hp;
    }

    /**
     * Gets the max health.
     * @return The maximum health.
     */
    public int getMaxHp() {
        return maxHp;
    }

    /**
     * Makes the entity get hurt.
     * @param damage The damage taken.
     * @param targetAnim The target animation.
     * @throws IllegalArgumentException If damage is negative.
     */
    public void getHurt(int damage, String targetAnim) {
        if (dead)
            return;
        if (damage < 0f) 
            throw new IllegalArgumentException("cant get hurt with negative damage");
        hp = Math.max(0, hp - damage);
        stateManager.setCurrentState("hurt", new MCCSHurt.HurtStateArgs(damage, targetAnim));
        //System.out.println("J'ai pris " + damage + "dégats !");
    }
    
    /**
     * Gets the tolerance hitbox.
     * @return The tolerance hitbox.
     */
    public Float getToleranceHitbox() {
        return toleranceHitbox;
    }

    /**
     * Checks if the player is dead.
     * @return True if the player is dead, false otherwise.
     */
    public boolean isDead() {
        return dead;
    }

    /**
     * Sets the max hp.
     * @param maxHp The maximum health to set.
     */
    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    /**
     * Sets the health.
     * @param h The health to set.
     */
    public void setHealth(int h) {
        this.hp = h;
    }

    /**
     * Sets the player to dead.
     */
    public void setDead() {
        dead = true;
    }

    /**
     * Checks if the player is busy.
     * @return True if the player is busy, false otherwise.
     */
    public boolean isBusy() {
        if (stateManager.getCurrentState() == null) 
            return false;
        return stateManager.getCurrentState().isBlocking();
    }

    /**
     * Gets the state manager.
     * @return The state manager.
     */
    public MCStateMachine getStateManager() {
        return stateManager;
    }
}