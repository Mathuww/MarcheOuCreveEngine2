package com.walk.or.die.engine.entities;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.shared.MCIntVector2;
import com.walk.or.die.engine.shared.MCSharedAssets;
import com.walk.or.die.engine.sm.MCStateMachine;
import com.walk.or.die.engine.tiledmap.MCTerrainMap;

/**
 * The base class to represents something on the grid.
 */
public abstract class MCEntity {
    /**
     * Represent the entity and his new position, useful to trigger events.
     */
    public static class TileReachedArgs {
        public MCEntity entity;
        public MCIntVector2 tile;

        /**
         * The constructor.
         * @param entity
         * @param tile
         */
        public TileReachedArgs(MCEntity entity, MCIntVector2 tile) {
            this.entity = entity;
            this.tile = tile;
        }
    }

    private String id;
    private MCGame parent;
    private MCTerrainMap map;
    private Rectangle hitbox;
    private Map<String, MCAnimation> animations;
    private String currentAnimName;
    private MCAnimation currentAnim;
    private Sprite sprite;
    private int layer = 1;
    /**
     * If the entity need to be display or not.
     */
    public boolean display = true;
    /**
     * If the player select the entity or not.
     */
    public boolean focus = false;
    /**
     * If the entity need to keep the focus or not (when moving for example).
     */
    public boolean keep = false;

    private float SIZE = 1f;

    /**
     * The constructor.
     * @param parent
     * @param map
     * @param entityId
     */
    public MCEntity(MCGame parent, MCTerrainMap map, String entityId) {
        this.parent = parent;
        this.map = map;
        this.id = entityId;
        this.animations = new HashMap<>();

        TextureRegion fallback;
        try {
            fallback = MCSharedAssets.get().getSavedTexture("fallback");
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("cant build entity without fallback texture");
        }
        sprite = new Sprite(fallback);
        sprite.setSize(SIZE, SIZE);
        sprite.setPosition(0, 0);

        hitbox = new Rectangle(0, 0, sprite.getWidth(), sprite.getHeight());
    }

    /**
     * Call on spawn
     */
    public abstract void onSpawn();

    /**
     * Initialize parameters from tiled datas.
     * @param props
     * @throws Exception
     */
    public abstract void initFromProperties(MapProperties props) throws Exception;

    /**
     * Add a possible animation for the entity.
     * @param animName
     * @param anim
     * @see MCAnimation
     */
    public void addAnimation(String animName, MCAnimation anim) {
        animations.put(animName, anim);
        //System.out.println("mes animations sont mtn : " + animations.keySet());
    }

    /**
     * Get an entity's animation.
     * @param animName
     * @return
     * @see MCAnimation
     */
    public MCAnimation getAnimation(String animName) {
        return animations.get(animName);
    }

    /**
     * Play an animation.
     * @param animName
     * @return
     * @see MCAnimation
     */
    public boolean playAnimation(String animName) {
        MCAnimation newAnim = animations.get(animName);
        if (newAnim != null) {
            currentAnimName = animName;
            currentAnim = newAnim;
            currentAnim.reset(); // remet statetime à 0 ms
            //System.out.println(id + ": playAnimation " + animName + " success");
            return true;
        } else {
            //System.err.println(id + ": playAnimation " + animName + " not found");
            return false;
        }
    }

    public boolean playAnimationWithoutReset(String animName) {
        //System.out.println("asking playanimnoreset : " + animName);
        if (currentAnimName == animName)
            return true;
        return playAnimation(animName);
    }

    /**
     * Call each frame.
     * @param delta
     */
    public void update(float delta) {
        //stateManager.update(delta);
        sprite.setPosition(hitbox.x, hitbox.y);
        if (currentAnim != null) {
            sprite.setRegion(currentAnim.update(delta));
        }
    }

    /**
     * Render the entity.
     * @param batch
     */
    public void render(SpriteBatch batch) {
        if (!display) return;
        sprite.draw(batch);
    }

    /**
     * Get the parent (the game).
     * @return
     */
    public MCGame getParent() {
        return parent;
    }
    
    /**
     * Get the x-coordinate.
     */
    public float getX() {
        return this.hitbox.x;
    }

    /**
     * Get the y-coordinate.
     * @return
     */
    public float getY() {
        return this.hitbox.y;
    }

    /**
     * Set the x-coordinate.
     * @param x
     */
    public void setX(float x) {
        this.hitbox.x = x;
    }

    /**
     * Set the y-coordinate.
     * @param y
     */
    public void setY(float y) {
        this.hitbox.y = y;
    }

    /**
     * Get the size of the sprite's entity (assuming it's a square).
     * @return
     */
    public float getSize() {
        return sprite.getWidth();
    }

    /**
     * Get the entity's layer.
     * @return
     */
    public int getLayer() {
        return layer;
    }

    /**
     * Get the entity's position.
     * @return
     */
    public Vector2 getPosition() {
        return new Vector2(this.hitbox.x, this.hitbox.y);
    }

    /**
     * Get the entity's tiled position.
     * @return
     * @see MCIntVector2
     */
    public MCIntVector2 getTilePosition() {
        return map.stickToNearestTile(getPosition());
    }

    /**
     * Set the entity's position.
     * @param x
     * @param y
     */
    public void setPosition(float x, float y) {
        this.hitbox.setPosition(x, y);
    }

    /**
     * Set the entity's position.
     * @param pos
     */
    public void setPosition(Vector2 pos) {
        this.hitbox.setPosition(pos.x, pos.y);
    }

    /**
     * Set the alpha of the sprite.
     * @param alpha
     */
    public void setAlpha(float alpha) {
        sprite.setAlpha(MathUtils.clamp(alpha, 0f, 1f));
    }

    /**
     * Get the hitbox.
     * @return
     */
    public Rectangle getHitbox() {
        return this.hitbox;
    }

    /**
     * If the hitbox collides or overlap the given entity's hitbox
     * @param e
     * @return
     */
    public boolean collidesWith(MCEntity e) {
        return this.hitbox.overlaps(e.getHitbox());
    }

    /**
     * Get the sprite.
     * @return
     */
    public Sprite getSprite() {
        return sprite;
    }

    /**
     * Get the map the entity is on.
     * @return
     */
    public MCTerrainMap getMap() {
        return map;
    }

    /**
     * To give the focus at the entity.
     */
    public void getFocus() {
        focus = true;
        // System.out.println("Focus get by " + this.toString());
        // MCEventBus.get().emit("ChangedFocus", this);
    }

    /**
     * Take off the focus at the entity
     * @return false if the entity block the focus
     */
    public boolean loseFocus() {
        if (keep) return false;
        focus = false;
        return true;
    }

    /**
     * If the entity is invisible or not.
     * @return
     */
    public boolean isHidden() {
        return !display;
    }

    /**
     * Make the entity visible.
     */
    public void show() {
        display = true;
    }

    /**
     * Make the entity invisible.
     */
    public void hide() {
        display = false;
    }

    /**
     * Get the entity's id.
     * @return
     */
    public String getId() {
        return this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MCEntity comp = (MCEntity) obj;
        return comp.getId().equals(this.getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Get the state manager (null by default).
     * @return
     */
    public MCStateMachine getStateManager() {
        return null;
    }
}