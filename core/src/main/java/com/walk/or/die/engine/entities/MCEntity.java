package com.walk.or.die.engine.entities;

import java.util.ArrayList;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.MCEventBus;
import com.walk.or.die.engine.screens.MCGameScreen;
import com.walk.or.die.engine.sm.MCStateMachine;
import com.walk.or.die.engine.sm.entity.MCEntityState;
import com.walk.or.die.engine.sm.entity.states.MCESClickMove;
import com.walk.or.die.engine.sm.entity.states.MCESIdle;
import com.walk.or.die.engine.sm.entity.states.MCESAim;
import com.walk.or.die.engine.sm.entity.states.MCESShoot;
import com.walk.or.die.engine.sm.game.MCGameState;
import com.walk.or.die.engine.tiledmap.MCGameMap;
import com.walk.or.die.engine.tiledmap.MCMap;

public class MCEntity {
    public static class TileReachedArgs {
        public MCEntity entity;
        public Vector2 tile;

        public TileReachedArgs(MCEntity entity, Vector2 tile) {
            this.entity = entity;
            this.tile = tile;
        }
    }

    public abstract class Attack {
        protected final MCEntity parent;
        protected int power;
        private Map<Vector2, Float> damagePattern;

        public Attack(MCEntity parent, int power, Map<Vector2, Float> pattern) {
            this.parent = parent;
            this.power = power;
            this.damagePattern = pattern;
        }

        public boolean isValidTile(Vector2 targetPos) {
            return damagePattern.containsKey(targetPos);
        }
        
        protected float getDamageAtTile(Vector2 targetPos) {
            Vector2 relativeDist = parent.getTilePosition().sub(targetPos);
            return damagePattern.getOrDefault(relativeDist, -1f);
        }

        public float getDamageTo(MCEntity targetEntity) {
            return getDamageAtTile(targetEntity.getTilePosition());
        }
    }

    private MCGameScreen parent;
    private MCGameMap map;
    private Rectangle hitbox;
    private TextureRegion currentRegion;
    private Sprite sprite;
    private int layer = 1;
    public boolean focus = false;
    public boolean keep = false;

    // A deplacer
    //private MCStateMachine<MCEntityState, MCEntity> stateManager;
    //public Attack baseAttack;

    private float SIZE = 1f;

    public MCEntity(MCGameScreen parent, MCGameMap map, Vector2 spawn, TextureRegion baseRegion) {
        this.parent = parent;
        this.map = map;
        currentRegion = baseRegion;

        sprite = new Sprite(currentRegion);
        sprite.setSize(SIZE, SIZE);
        sprite.setPosition(spawn.x, spawn.y);

        hitbox = new Rectangle(spawn.x, spawn.y, sprite.getWidth(), sprite.getHeight());
    }

    public void update(float delta) {
        // utile pour ajouter des anims par la suite hihihi
        //stateManager.update(delta);
        sprite.setPosition(hitbox.x, hitbox.y);
        sprite.setRegion(currentRegion);
    }

    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }

    // A deplacer
    /*
    public MCStateMachine getStateManager() {
        return this.stateManager;
    }

    // A deplacer
    public void setStateManager(MCStateMachine<MCEntityState, MCEntity> stateManager) {
        this.stateManager = stateManager;
    } */

    public MCGameScreen getParent() {
        return parent;
    }
    
    public float getX() {
        return this.hitbox.x;
    }

    public float getY() {
        return this.hitbox.y;
    }

    public void setX(float x) {
        this.hitbox.x = x;
    }

    public void setY(float y) {
        this.hitbox.y = y;
    }

    public float getSize() {
        return sprite.getWidth();
    }

    public int getLayer() {
        return layer;
    }

    public Vector2 getPosition() {
        return new Vector2(this.hitbox.x, this.hitbox.y);
    }

    public Vector2 getTilePosition() {
        return map.stickToNearestTile(getPosition());
    }

    public void setPosition(float x, float y) {
        this.hitbox.setPosition(x, y);
    }

    public MCGameMap getMap() {
        return map;
    }

    public void getFocus() {
        focus = true;
        System.out.println("Focus get by " + this.toString());
        // MCEventBus.get().emit("ChangedFocus", this);
    }

    public boolean loseFocus() {
        if (keep) return false;
        focus = false;
        return true;
    }


    // A deplacer
    /*
    public boolean shoot(MCEntity target) {
        //int damage = baseAttack.getDamageTo(target);
        //target.getHurt(damage)
        return true;
    }

    // A deplacer
    public void getHurt(int damage) {
        System.out.println("J'ai pris " + damage + "dégats !");
    } */

}