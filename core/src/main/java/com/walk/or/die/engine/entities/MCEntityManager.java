package com.walk.or.die.engine.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.shared.MCIntVector2;

public class MCEntityManager {
    private static MCEntityManager instance = null;

    public static MCEntityManager get() {
        if (instance == null) instance = new MCEntityManager();
        return instance;
    }

    private MCEntityManager() {}

    private MCGame parent;

    public void init(MCGame game) {
        parent = game;
    }

    //private MCGame parent;
    private Set<MCEntity> entities = new HashSet<>();
    private List<MCEntity> toKill = new ArrayList<>();

    // projectiles can only be built once !!!!! once at a time !!!
    public MCProjectile buildProjectile(String projType) throws Exception {
        MCProjectile proj = (MCProjectile) MCEntityFactory.get().build(
            parent, 
            parent.getTerrainMap(), 
            projType, 
            "projectile");
        if (proj != null) {
            entities.add(proj);
        }
        return proj;
    }

    public void kill(MCEntity e) {
        toKill.add(e);
    }

    public void addEntity(MCEntity e) {
        entities.add(e);
    }

    public void addAllEntities(Set<MCEntity> e) {
        entities.addAll(e);
    }

    public Set<MCEntity> getEntities() {
        return this.entities;
    }

    public Set<MCAlly> getAllies() {
        Set<MCAlly> list = new HashSet<>();

        for (MCEntity e: entities) {
            if (e instanceof MCAlly ally) {
                list.add(ally);
            }
        }
        
        return list;
    }

    public Set<MCEnemy> getEnemies() {
        Set<MCEnemy> list = new HashSet<>();

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
    
    public MCEntity getEntityFromTile(int layer, MCIntVector2 pos) {
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
        // si on fait pas ca
        // ca fait des concurrent modification exception de partout
        if (!toKill.isEmpty()) {
            entities.removeAll(toKill);
            toKill.clear();
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
