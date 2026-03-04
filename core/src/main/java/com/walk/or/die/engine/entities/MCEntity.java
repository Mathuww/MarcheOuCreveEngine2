package com.walk.or.die.engine.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.exceptions.MissingDataException;
import com.walk.or.die.engine.shared.MCIntVector2;
import com.walk.or.die.engine.shared.MCSharedAssets;
import com.walk.or.die.engine.sm.MCStateMachine;
import com.walk.or.die.engine.tiledmap.MCTerrainMap;

/**
 * The base class that represents something on the grid.
 */
public abstract class MCEntity {
    /**
     * Represents the entity and its new position, useful to trigger events.
     */
    public static class TileReachedArgs {
        /** The entity. */
        public MCEntity entity;
        /** The tile. */
        public MCIntVector2 tile;

        /**
         * The constructor.
         * @param entity The entity.
         * @param tile The tile.
         */
        public TileReachedArgs(MCEntity entity, MCIntVector2 tile) {
            this.entity = entity;
            this.tile = tile;
        }
    }

    /** The unique identifier for the entity. */
    private String id;
    /** The parent game instance. */
    private MCGame parent;
    /** The terrain map the entity belongs to. */
    private MCTerrainMap map;
    /** The hitbox of the entity for collision detection. */
    private Rectangle hitbox;
    /** A map of animations available for this entity. */
    private Map<String, MCAnimation> animations;
    /** The name of the currently playing animation. */
    private String currentAnimName;
    /** The currently playing animation. */
    private MCAnimation currentAnim;
    /** The sprite representing the entity. */
    private Sprite sprite;
    /** The rendering layer of the entity. */
    private int layer = 1;
    /** Indicates if the entity's state is frozen. */
    private boolean freeze = false;

    /**
     * If the entity needs to be displayed or not.
     */
    public boolean display = true;
    /**
     * If the player selects the entity or not.
     */
    public boolean focus = false;
    /**
     * If the entity needs to keep the focus or not (when moving for example).
     */
    public boolean keep = false;

    /** The default size of the entity's sprite. */
    private float SIZE = 1f;

    /**
     * The constructor.
     * @param parent The parent game.
     * @param map The terrain map.
     * @param entityId The entity id.
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
     * Initializes parameters from map properties.
     * @param props The map properties.
     * @throws MissingDataException If required data is missing from the properties.
     */
    public void initFromMapProperties(MapProperties props) throws MissingDataException {};

    /**
     * Called on spawn.
     */
    public abstract void onSpawn();

    /**
     * Initializes parameters from tiled data.
     * @param props The map properties.
     * @throws Exception If an error occurs during initialization.
     */
    public abstract void initFromProperties(MapProperties props) throws Exception;

    /**
     * Adds a possible animation for the entity.
     * @param animName The animation name.
     * @param anim The animation.
     * @see MCAnimation
     */
    public void addAnimation(String animName, MCAnimation anim) {
        animations.put(animName, anim);
        //System.out.println("mes animations sont mtn : " + animations.keySet());
    }

    /**
     * Gets the animation names.
     * @return The set of animation names.
     */
    public Set<String> getAnimationNames() {
        return animations.keySet();
    }

    /**
     * Gets the animation map.
     * @return The map of animations.
     */
    public Map<String, MCAnimation> getAnimationMap() {
        return animations;
    }

    /**
     * Checks if there is an animation.
     * @param animName The animation name.
     * @return True if there is the animation, false otherwise.
     */
    public boolean hasAnimation(String animName) {
        return animations.containsKey(animName);
    }

    /**
     * Sets the freeze state.
     * @param bool The freeze value.
     */
    public void setFreeze(boolean bool) {
        freeze = bool;
    }

    /**
     * Checks if it is freezed.
     * @return True if it is freezed, false otherwise.
     */
    public boolean isFreeze() {
        return freeze;
    }
    
    /**
     * Gets an entity's animation.
     * @param animName The animation name.
     * @return The animation.
     * @see MCAnimation
     */
    public MCAnimation getAnimation(String animName) {
        return animations.get(animName);
    }

    /**
     * Plays an animation.
     * @param animName The animation name.
     * @return True if the animation is played, false otherwise.
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

    /**
     * Plays an animation without resetting its state.
     * @param animName The animation name.
     * @return True if the animation is played without resetting its state, false otherwise.
     */
    public boolean playAnimationWithoutReset(String animName) {
        //System.out.println("asking playanimnoreset : " + animName);
        if (currentAnimName == animName)
            return true;
        return playAnimation(animName);
    }

    /**
     * Called on each frame.
     * @param delta The time delta.
     */
    public void update(float delta) {
        //stateManager.update(delta);
        sprite.setPosition(hitbox.x, hitbox.y);
        if (currentAnim != null) {
            sprite.setRegion(currentAnim.update(delta));
        }
    }

    /**
     * Renders the entity.
     * @param batch The sprite batch.
     */
    public void render(SpriteBatch batch) {
        if (!display) return;
        sprite.draw(batch);
    }

    /**
     * Gets the parent game instance.
     * @return The game.
     */
    public MCGame getParent() {
        return parent;
    }
    
    /**
     * Gets the x-coordinate.
     * @return The x-coordinate.
     */
    public float getX() {
        return this.hitbox.x;
    }

    /**
     * Gets the y-coordinate.
     * @return The y-coordinate.
     */
    public float getY() {
        return this.hitbox.y;
    }

    /**
     * Sets the x-coordinate.
     * @param x The x-coordinate.
     */
    public void setX(float x) {
        this.hitbox.x = x;
    }

    /**
     * Sets the y-coordinate.
     * @param y The y-coordinate.
     */
    public void setY(float y) {
        this.hitbox.y = y;
    }

    /**
     * Gets the size of the sprite's entity (assuming it is a square).
     * @return The size.
     */
    public float getSize() {
        return sprite.getWidth();
    }

    /**
     * Gets the entity's layer.
     * @return The layer.
     */
    public int getLayer() {
        return layer;
    }

    /**
     * Gets the entity's position.
     * @return The position.
     */
    public Vector2 getPosition() {
        return new Vector2(this.hitbox.x, this.hitbox.y);
    }

    /**
     * Gets the entity's tiled position.
     * @return The tile position.
     * @see MCIntVector2
     */
    public MCIntVector2 getTilePosition() {
        return map.stickToNearestTile(getPosition());
    }

    /**
     * Sets the entity's position.
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     */
    public void setPosition(float x, float y) {
        this.hitbox.setPosition(x, y);
    }

    /**
     * Sets the entity's position.
     * @param pos The position.
     */
    public void setPosition(Vector2 pos) {
        this.hitbox.setPosition(pos.x, pos.y);
    }

    /**
     * Sets the entity's position to a specific tile.
     * @param pos The tile position.
     */
    public void setTilePosition(MCIntVector2 pos) {
        this.hitbox.setPosition(pos.x, pos.y);
    }

    /**
     * Sets the alpha of the sprite.
     * @param alpha The alpha value.
     */
    public void setAlpha(float alpha) {
        sprite.setAlpha(MathUtils.clamp(alpha, 0f, 1f));
    }

    /**
     * Gets the hitbox.
     * @return The hitbox.
     */
    public Rectangle getHitbox() {
        return this.hitbox;
    }

    /**
     * Checks if the hitbox collides or overlaps the given entity's hitbox.
     * @param e The other entity.
     * @return True if collides, false otherwise.
     */
    public boolean collidesWith(MCEntity e) {
        return this.hitbox.overlaps(e.getHitbox());
    }

    /**
     * Gets the sprite.
     * @return The sprite.
     */
    public Sprite getSprite() {
        return sprite;
    }

    /**
     * Gets the map the entity is on.
     * @return The terrain map.
     */
    public MCTerrainMap getMap() {
        return map;
    }

    /**
     * Gives focus to the entity.
     */
    public void getFocus() {
        focus = true;
        // System.out.println("Focus get by " + this.toString());
        // MCEventBus.get().emit("ChangedFocus", this);
    }

    /**
     * Takes off the focus from the entity.
     * @return False if the entity blocks the focus, true otherwise.
     */
    public boolean loseFocus() {
        if (keep) return false;
        focus = false;
        return true;
    }

    /**
     * Checks if the entity is invisible or not.
     * @return True if it is hidden, false otherwise.
     */
    public boolean isHidden() {
        return !display;
    }

    /**
     * Makes the entity visible.
     */
    public void show() {
        display = true;
    }

    /**
     * Makes the entity invisible.
     */
    public void hide() {
        display = false;
    }

    /**
     * Gets the entity's id.
     * @return The id.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Compares this entity to the specified object.
     * @param obj The object to compare with.
     * @return True if the entities are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MCEntity comp = (MCEntity) obj;
        return comp.getId().equals(this.getId());
    }

    /**
     * Gets the hash code for this entity.
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Gets the state manager (null by default).
     * @return The state manager.
     */
    public MCStateMachine getStateManager() {
        return null;
    }
}