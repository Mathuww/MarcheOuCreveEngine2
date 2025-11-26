package com.walk.or.die.engine.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
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

public abstract class MCEntity {
    public static class TileReachedArgs {
        public MCEntity entity;
        public Vector2 tile;

        public TileReachedArgs(MCEntity entity, Vector2 tile) {
            this.entity = entity;
            this.tile = tile;
        }
    }

    public static class Attack {
        private final MCEntity parent;
        private int power;
        private Map<Vector2, Float> damagePattern;

        private String senderAnim;
        private String targetAnim;

        public Attack(MCEntity parent, int power, Map<Vector2, Float> pattern) {
            this.parent = parent;
            this.power = power;
            this.damagePattern = pattern;
        }

        public void initFromProperties(MapProperties props) {
            this.senderAnim = props.get("senderAnim", String.class);
            this.targetAnim = props.get("targetAnim", String.class);
        }

        public boolean isValidTile(Vector2 targetPos) {
            if (parent == null)
                throw new IllegalStateException("cant use attack methods without associatin a parent !");
            return damagePattern.containsKey(parent.getTilePosition().cpy().sub(targetPos));
        }
        
        private float getDamageAtTile(Vector2 targetPos) {
            if (parent == null)
                throw new IllegalStateException("cant use attack methods without associatin a parent !");
            Vector2 relativeDist = parent.getTilePosition().cpy().sub(targetPos);
            Float damage = damagePattern.get(relativeDist);
            if (damage == null)
                return -1f;
            else
                return damage * (float)power;
        }

        public float getDamageTo(MCEntity targetEntity) {
            if (parent == null)
                throw new IllegalStateException("cant use attack methods without associatin a parent !");
            return getDamageAtTile(targetEntity.getTilePosition());
        }

        @Override 
        public String toString() {
            String s = "";
            for (int i = -2; i <= 2; i++) {
                for (int j = -2; j <= 2; j++) {
                    Vector2 v = new Vector2(i, j);
                    Float d = damagePattern.get(v);
                    s += "(" + v.x + "," + v.y + ") : " + d + "\n";
                }
            }
            return s;
        }
    }

    private String name;
    private MCGameScreen parent;
    private MCGameMap map;
    private Rectangle hitbox;
    private TextureRegion currentRegion; // ca va bientot degager
    private Map<String, MCAnimation> animations;
    private MCAnimation currentAnim;
    private Sprite sprite;
    private int layer = 1;
    public boolean focus = false;
    public boolean keep = false;

    private float SIZE = 1f;

    public MCEntity(MCGameScreen parent, MCGameMap map, String entityId) {
        this.parent = parent;
        this.map = map;
        this.name = entityId;
        this.animations = new HashMap<>();

        sprite = new Sprite();
        sprite.setSize(SIZE, SIZE);
        sprite.setPosition(0, 0);

        hitbox = new Rectangle(0, 0, sprite.getWidth(), sprite.getHeight());
    }

    public abstract void initFromProperties(MapProperties props) throws Exception;

    public void addAnimation(String animName, MCAnimation anim) {
        animations.put(animName, anim);
    }

    public void playAnimation(String animName) {
        MCAnimation newAnim = animations.get(animName);
        if (newAnim != null) {
            currentAnim = newAnim;
            currentAnim.reset(); // remet statetime à 0 ms
        } else {
            System.err.println("playAnimation " + animName + " not found");
        }
    }

    public void update(float delta) {
        // utile pour ajouter des anims par la suite hihihi
        //stateManager.update(delta);
        sprite.setPosition(hitbox.x, hitbox.y);
        if (currentAnim != null) {
            sprite.setRegion(currentAnim.update(delta));
        }
    }

    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }

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
        return map.stickToNearestTile(getPosition()).cpy();
    }

    public void setPosition(float x, float y) {
        this.hitbox.setPosition(x, y);
    }

    public void setPosition(Vector2 pos) {
        this.hitbox.setPosition(pos.x, pos.y);
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