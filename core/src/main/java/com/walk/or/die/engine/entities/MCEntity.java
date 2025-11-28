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
import com.walk.or.die.engine.shared.MCEventBus;
import com.walk.or.die.engine.sm.MCStateMachine;
import com.walk.or.die.engine.sm.entity.MCEntityState;
import com.walk.or.die.engine.sm.entity.states.MCESClickMove;
import com.walk.or.die.engine.sm.entity.states.MCESIdle;
import com.walk.or.die.engine.sm.entity.states.MCESAim;
import com.walk.or.die.engine.sm.entity.states.MCESShoot;
import com.walk.or.die.engine.sm.game.MCGameState;
import com.walk.or.die.engine.tiledmap.MCTerrainMap;
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

    private String name;
    private MCGame parent;
    private MCTerrainMap map;
    private Rectangle hitbox;
    private Map<String, MCAnimation> animations;
    private MCAnimation currentAnim;
    private Sprite sprite;
    private int layer = 1;
    public boolean display = true;
    public boolean focus = false;
    public boolean keep = false;

    private float SIZE = 1f;

    public MCEntity(MCGame parent, MCTerrainMap map, String entityId) {
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

    public boolean playAnimation(String animName) {
        MCAnimation newAnim = animations.get(animName);
        if (newAnim != null) {
            currentAnim = newAnim;
            currentAnim.reset(); // remet statetime à 0 ms
            return true;
        } else {
            System.err.println("playAnimation " + animName + " not found");
            return false;
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
        if (!display) return;
        sprite.draw(batch);
    }

    public MCGame getParent() {
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

    public Rectangle getHitbox() {
        return this.hitbox;
    }

    public boolean collidesWith(MCEntity e) {
        return this.hitbox.overlaps(e.getHitbox());
    }

    public MCTerrainMap getMap() {
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

    public boolean isHidden() {
        return !display;
    }

    public void show() {
        display = true;
    }

    public void hide() {
        display = false;
    }
}