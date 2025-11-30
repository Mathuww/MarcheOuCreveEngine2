package com.walk.or.die.engine.entities;

import java.util.function.Consumer;
import java.util.function.Function;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.shared.MCIntVector2;
import com.walk.or.die.engine.shared.MCUtils;
import com.walk.or.die.engine.tiledmap.MCTerrainMap;

public class MCProjectile extends MCEntity {
    private float speed = 4f;
    private float totalDuration;
    private float elapsedTime = 0f;
    private Vector2 startPos;
    private Vector2 newPos;
    private Vector2 targetPos;
    private MCEntity target;
    private Runnable callback;

    private boolean arrived = false;

    private final Interpolation easing = Interpolation.pow2Out;

    public MCProjectile(MCGame parent, MCTerrainMap map, String entityGenericName) {
        super(parent, map, entityGenericName);
        display = false;
    }

    @Override
    public void initFromProperties(MapProperties props) {
        speed = MCUtils.getFloatProperty(props, "speed", speed);
    }

    public void callOnArrival(Runnable callback) {
        this.callback = callback;
    }

    public void setCollisionTrigger(MCEntity e) {
        target = e;
    }

    public void launchTo(MCIntVector2 targetGridPos) {
        playAnimation("idle");
        this.targetPos = targetGridPos.toGdxVect();
        this.startPos = this.getPosition().cpy();
        this.newPos = new Vector2(startPos);

        float dist = startPos.dst(this.targetPos);
        totalDuration = (1.5f * dist) / speed;

        display = true;
    }

    // a ce niveau la, on est censés avoir vérifié
    // que le chemin est clair pour le projectile
    // donc plus aucune vérification ici.
    @Override
    public void update(float delta) {
        if (targetPos != null && !arrived) {
            // pas encore lancé ou alors déjà arrivé
            // (EntityManager a peut etre pas encore pu tuer le projectile donc on veut pas
            // run le callback 2 fois)

            elapsedTime += delta;

            float alpha = Math.min(1f, elapsedTime / totalDuration);
            alpha = easing.apply(alpha);

            newPos.set(startPos).lerp(targetPos, alpha);
            setPosition(newPos);

            if (target != null)
                arrived = collidesWith(target);
            else
                arrived = (alpha >= 1f);
            if (arrived) { // la balle est arrivée billy !! :(
                if (callback != null) callback.run();
                MCEntityManager.get().kill(this); // adieu
            }
        }

        super.update(delta);
    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);
    }
}
