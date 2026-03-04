package com.walk.or.die.engine.entities;

import java.util.function.Consumer;
import java.util.function.Function;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.shared.MCIntVector2;
import com.walk.or.die.engine.shared.MCUtils;
import com.walk.or.die.engine.tiledmap.MCTerrainMap;

/**
 * A bullet entity.
 */
public class MCProjectile extends MCEntity {
    /** The squared distance threshold for collision detection. */
    private final float COLLISION_THRESHOLD = 0.75f;
    /** The duration in seconds for the projectile's fading animation. */
    private final float FADING_DURATION = 0.15f;

    /** The movement speed of the projectile. */
    private float speed = 4f;
    /** The total duration in seconds for the projectile's travel. */
    private float totalDuration;
    /** The elapsed time since the projectile was launched. */
    private float elapsedTime = 0f;
    /** The elapsed time during the fading animation. */
    private float fadeStateTime = 0f;
    /** The starting position of the projectile. */
    private Vector2 startPos;
    /** The current interpolated position of the projectile. */
    private Vector2 newPos;
    /** The target position of the projectile in world coordinates. */
    private Vector2 targetPos;
    /** The target entity of the projectile. */
    private MCEntity target;
    /** The callback function to execute when the projectile arrives at its target. */
    private Runnable callback;

    /** Indicates whether the projectile is currently fading out. */
    private boolean fading = false;
    /** Indicates whether the projectile has been marked for removal from the game. */
    private boolean markedToKill = false;

    /** The interpolation function used for smooth movement. */
    private final Interpolation easing = Interpolation.pow2Out;

    /** The next available unique ID for a projectile. */
    private static int NEXT_ID = 0;
    /** The unique identifier for this projectile instance. */
    private final int id = NEXT_ID++;

    /**
     * Constructs a new projectile.
     * @param parent The parent MCGame.
     * @param map The terrain map.
     * @param entityGenericName The entity's generic name.
     */
    public MCProjectile(MCGame parent, MCTerrainMap map, String entityGenericName) {
        super(parent, map, entityGenericName);
        display = false;
        //System.out.println("new projo : " + this);
    }

    /**
     * Called at spawn.
     */
    @Override public void onSpawn() {}

    /**
     * Initializes the projectile from map properties.
     * @param props The map properties.
     */
    @Override
    public void initFromProperties(MapProperties props) {
        speed = MCUtils.getFloatProperty(props, "speed", speed);
    }

    /**
     * Sets the freeze state of the projectile.
     * @param bool The boolean value indicating whether the projectile should be frozen.
     */
    @Override 
    public void setFreeze(boolean bool) {
        //System.out.println("Aie aie aie je suis freeze " + this);
        super.setFreeze(bool);
    }
    /**
     * Sets a callback to be executed upon the projectile's arrival.
     * @param callback The callback to be executed upon arrival.
     */
    public void callOnArrival(Runnable callback) {
        this.callback = callback;
    }

    /**
     * Launches the projectile towards a specified target grid position.
     * @param targetGridPos The target grid position for the projectile.
     */
    public void launchTo(MCIntVector2 targetGridPos) {
        playAnimation("idle");
        this.targetPos = targetGridPos.toGdxVect();
        this.startPos = this.getPosition().cpy();
        this.newPos = new Vector2(startPos);

        float dist = startPos.dst(this.targetPos);
        totalDuration = (1.5f * dist) / speed;

        // calculer rotation
        float dx = targetGridPos.x - startPos.x;
        float dy = targetGridPos.y - startPos.y;
        float angleRad = (float) Math.atan2(dy, dx);
        float angleDeg = (float) Math.toDegrees(angleRad);
        getSprite().setRotation(angleDeg);

        display = true;
    }

    // a ce niveau la, on est censés avoir vérifié
    // que le chemin est clair pour le projectile
    // donc plus aucune vérification ici.
    /**
     * Called on each frame.
     * @param delta The time delta.
     */
    @Override
    public void update(float delta) {
        //System.out.println("update : " + fading + " | " +markedToKill + " | " + this);
        if (markedToKill)  // en attente de la PDM
            return;

        if (fading) {
            fadeStateTime += delta;
            // pas le meme alpha qu'en bas hein !
            float alpha = 1 - (fadeStateTime / FADING_DURATION);
            setAlpha(alpha);
            if (fadeStateTime >= FADING_DURATION) {
                MCEntityManager.get().kill(this); // adieu
                markedToKill = true;
            }
            return;
        }

        if (targetPos != null) {
            // pas encore lancé ou alors déjà arrivé
            // (EntityManager a peut etre pas encore pu tuer le projectile donc on veut pas
            // run le callback 2 fois)

            elapsedTime += delta;

            float alpha = Math.min(
                1f, 
                elapsedTime / Math.max(totalDuration, MathUtils.FLOAT_ROUNDING_ERROR)
            );
            alpha = easing.apply(alpha);

            newPos.set(startPos).lerp(targetPos, alpha);
            setPosition(newPos);

            // dst2 : dst^2 (+ rapide)
            if (newPos.dst2(targetPos) <= COLLISION_THRESHOLD * COLLISION_THRESHOLD) { 
                // la balle est arrivée billy !! :(
                if (callback != null) {
                    callback.run();
                    callback = null;
                }
                fading = true;
            }
        }

        super.update(delta);
    }

    /**
     * Called on each frame.
     * @param batch The sprite batch.
     */
    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);
    }

    /**
     * Returns a string representation of this projectile.
     * @return A string representation of this projectile.
     */
    @Override
    public String toString() {
        return super.toString() + " id : " + id;
    }
}