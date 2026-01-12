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
    private final float COLLISION_THRESHOLD = 0.75f;
    private final float FADING_DURATION = 0.15f;

    private float speed = 4f;
    private float totalDuration;
    private float elapsedTime = 0f;
    private float fadeStateTime = 0f;
    private Vector2 startPos;
    private Vector2 newPos;
    private Vector2 targetPos;
    private MCEntity target;
    private Runnable callback;

    private boolean fading = false;
    private boolean markedToKill = false;

    private final Interpolation easing = Interpolation.pow2Out;

    private static int NEXT_ID = 0;
    private final int id = NEXT_ID++;

    /**
     * The constructor.
     * @param parent The parent MCGame.
     * @param map The MCTerrainMap.
     * @param entityGenericName The entity generic name.
     */
    public MCProjectile(MCGame parent, MCTerrainMap map, String entityGenericName) {
        super(parent, map, entityGenericName);
        display = false;
        //System.out.println("new projo : " + this);
    }

    @Override public void onSpawn() {}

    @Override
    public void initFromProperties(MapProperties props) {
        speed = MCUtils.getFloatProperty(props, "speed", speed);
    }

    @Override 
    public void setFreeze(boolean bool) {
        //System.out.println("Aie aie aie je suis freeze " + this);
        super.setFreeze(bool);
    }
    /**
     * Calls the callback when the run ends.
     * @param callback The callback to be called.
     */
    public void callOnArrival(Runnable callback) {
        this.callback = callback;
    }

    /**
     * Shoots the bullet towards a tile.
     * @param targetGridPos The target grid position.
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

    @Override
    public String toString() {
        return super.toString() + " id : " + id;
    }
}