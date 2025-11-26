package com.walk.or.die.engine.entities;

import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.walk.or.die.engine.MCUtils;
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
    
    protected final int MAX_ATTACK_NUMBER = 6;

    private Integer hp;
    private Integer maxDeplacements;
    private MCStateMachine<MCEntityState, MCEntity> stateManager;
    private Attack String;
    private Map<String, MCEntity.Attack> attacks;
    private String baseAttack;

    public MCCharacter(MCGameScreen parent, MCGameMap map, String entityGenericName) {
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
            MCEntity.Attack attack = attackFact.build(this, attackName);
            System.out.println(attackName + attack.toString());
            addAttack(attackName, attack);
        }

        baseAttack = props.get("baseAttack", String.class);
    }

    public void addAttack(String name, MCEntity.Attack attack) {
        attacks.put(name, attack);
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
