package com.walk.or.die.engine.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import java.util.ArrayList;
import java.util.List;
import com.walk.or.die.engine.MCGame;

public class MCEntityManager {
    private static MCEntityManager instance = null;

    public static MCEntityManager get() {
        if (instance == null) instance = new MCEntityManager();
        return instance;
    }

    private MCEntityManager() {}

    //private MCGame parent;
    private Array<MCEntity> entities = new Array<>();

    public void addEntity(MCEntity e) {
        entities.add(e);
    }

    public void addAllEntities(Array<MCEntity> e) {
        entities.addAll(e);
    }

    public Array<MCEntity> getEntities() {
        return this.entities;
    }

    public List<MCAlly> getAllies() {
        List<MCAlly> list = new ArrayList<>();

        for (MCEntity e: entities) {
            if (e instanceof MCAlly ally) {
                list.add(ally);
            }
        }
        
        return list;
    }

    public List<MCEnemy> getEnemies() {
        List<MCEnemy> list = new ArrayList<>();

        for (MCEntity e: entities) {
            if (e instanceof MCEnemy enemy) {
                list.add(enemy);
            }
        }
        
        return list;
    }

    public void clearEntities() {
        entities.clear();
    }

    public void playGlobalAnimation(String anim) {
        for (MCEntity e : entities) {
            e.playAnimation(anim);
        }
    }
    
    public MCEntity getEntityFromTile(int layer, Vector2 pos) {
        for (MCEntity e: entities) {
            if (e.getTilePosition().x == pos.x && e.getTilePosition().y == pos.y && e.getLayer() == layer) 
                return e;
        }
        return null;
    }

    public void update(float delta) {
        for (MCEntity e : entities) {
            e.update(delta);
        }
    }

    public void render(SpriteBatch batch) {
        // 1  : render base entities
        for (MCEntity e : entities) {
            e.render(batch);
        }
        // 2 : render on-terrain overlays (like the big tonneau)
        // to make sure they're always on top !
        for (MCEntity e : entities) {
            if (e instanceof MCCharacter c)
                c.renderOnGridOverlay(batch);
        }
    }
}
