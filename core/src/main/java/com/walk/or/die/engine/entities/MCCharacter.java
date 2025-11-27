package com.walk.or.die.engine.entities;

import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.shared.MCUtils;
import com.walk.or.die.engine.sm.MCStateMachine;
import com.walk.or.die.engine.sm.entity.MCEntityState;
import com.walk.or.die.engine.sm.entity.states.MCESAim;
import com.walk.or.die.engine.sm.entity.states.MCESClickMove;
import com.walk.or.die.engine.sm.entity.states.MCESIdle;
import com.walk.or.die.engine.sm.entity.states.MCESReady;
import com.walk.or.die.engine.sm.entity.states.MCESShoot;
import com.walk.or.die.engine.tiledmap.MCTerrainMap;
import com.walk.or.die.engine.tiledmap.MCMap;

public class MCCharacter extends MCEntity {
    
    protected final int MAX_ATTACK_NUMBER = 6;

    private Integer hp;
    private boolean dead = false;
    private boolean display = true;
    private Integer maxDeplacements;
    private MCStateMachine<MCEntityState, MCEntity> stateManager;
    private Map<String, MCAttack> attacks;
    private String baseAttack;

    public MCCharacter(MCGame parent, MCTerrainMap map, String entityGenericName) {
        super(parent, map, entityGenericName);
        attacks = new HashMap<>();
        stateManager = new MCStateMachine<>(this);
        stateManager.addState(new MCESClickMove(this));
        stateManager.addState(new MCESIdle(this));
        stateManager.addState(new MCESAim(this));
        stateManager.addState(new MCESShoot(this));
        stateManager.addState(new MCESReady(this));
        stateManager.setCurrentState("idle", new MCESIdle.IdleStateArgs());
    }

    @Override
    public void initFromProperties(MapProperties props) throws Exception {
        hp = MCUtils.getIntProperty(props, "hp", 100);
        maxDeplacements = MCUtils.getIntProperty(props, "maxMoves", 2);

        MCAttackFactory attackFact = MCAttackFactory.get();
        for (int i = 1; i < MAX_ATTACK_NUMBER; i++) {
            // on vient chercher attack1, attack2, etc.
            System.out.println("searching for " + "attack" + i);
            String attackName = props.get("attack" + i, String.class);
            if (attackName == null) {
                System.out.println("not found");
                break;
            }
            MCAttack attack = attackFact.build(this, attackName);
            System.out.println(attackName + attack.toString());
            addAttack(attackName, attack);
        }

        baseAttack = props.get("baseAttack", String.class);
    }

    public void addAttack(String name, MCAttack attack) {
        attacks.put(name, attack);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        stateManager.update(delta);
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!display) return;
        super.render(batch);
    }

    public MCStateMachine getStateManager() {
        return this.stateManager;
    }

    public void setStateManager(MCStateMachine<MCEntityState, MCEntity> stateManager) {
        this.stateManager = stateManager;
    }

    public MCAttack getAttack() {
        MCAttack attack = attacks.get(baseAttack);
        if (attack == null)
            throw new IllegalStateException("trying to shoot without a base attack !");
        return attack;
    }
    
    public boolean shoot(MCCharacter target) {
        MCAttack attack = attacks.get(baseAttack);
        if (attack == null)
            throw new IllegalStateException("trying to shoot without a base attack !");
        int damage = attack.getDamageTo(target);
        target.getHurt(damage);
        return true;
    }

    public void getHurt(int damage) {
        if (damage < 0f) 
            throw new IllegalArgumentException("cant get hurt with negative damage");
        hp = hp - damage;
        if (hp <= 0)
            die();
        System.out.println("J'ai pris " + damage + "dégats !");
    }
    
    public boolean isDead() {
        return dead;
    }

    public boolean isHidden() {
        return !display;
    }

    public void show() {
        display = true;
    }

    public void hide() {
        display = false;
    }

    private void die() {
        hp = 0;
        dead = true;
        if (!playAnimation("dead"))
            hide();
    }
}
