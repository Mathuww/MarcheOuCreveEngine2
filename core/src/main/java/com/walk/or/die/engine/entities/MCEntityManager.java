package com.walk.or.die.engine.entities;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.exceptions.MissingDataException;
import com.walk.or.die.engine.exceptions.TooManyExceptionsException;
import com.walk.or.die.engine.shared.MCIntVector2;
import com.walk.or.die.engine.ui.MCHUDManager;

/**
 * A singleton who manages all the entity in the game.
 */
public class MCEntityManager {
    private static MCEntityManager instance = null;

    /**
     * The getter.
     * @return
     */
    public static MCEntityManager get() {
        if (instance == null) instance = new MCEntityManager();
        return instance;
    }

    private MCEntityManager() {}

    private MCGame parent;

    /**
     * Init the singleton
     * @param game
     */
    public void init(MCGame game) {
        parent = game;
    }

    //private MCGame parent;
    private Set<MCEntity> entities = new HashSet<>();
    private Set<MCEntity> toKill = new HashSet<>();
    private Set<MCEntity> toAdd = new HashSet<>();
    private Array<Sprite> corpses = new Array<>();

    /**
     * Create a projectile entity.
     * @param projType
     * @return
     * @throws Exception
     * @see MCProjectile
     */
    public MCProjectile buildProjectile(String projType) throws Exception {
        // projectiles can only be built once !!!!! once at a time !!!
        MCProjectile proj = (MCProjectile) MCEntityFactory.get().build(
            parent, 
            parent.getTerrainMap(), 
            projType, 
            "projectile");
        if (proj != null) {
            addEntity(proj);
            //entities.add(proj);
        }
        return proj;
    }

    /**
     * Remove an entity.
     * @param e
     */
    public void kill(MCEntity e) {
        toKill.add(e);
    }

    /**
     * Remove an entity, and show his corpse.
     * @param e
     */
    public void killAndKeepCorpse(MCEntity e) {
        corpses.add(e.getSprite());
        toKill.add(e);
    }

    /**
     * Add a new entity.
     * @param e
     */
    public void addEntity(MCEntity e) {
        toAdd.add(e);
        //entities.add(e);
    }

    /**
     * Add a set of entities.
     * @param e
     */
    public void addAllEntities(Set<MCEntity> e) {
        toAdd.addAll(e);
        //entities.addAll(e);
    }

    /**
     * Get all entities in game.
     * @return
     */
    public Set<MCEntity> getEntities() {
        return this.entities;
    }

    /**
     * Get all MCAllies in the game.
     * @return
     * @see MCAlly
     */
    public Set<MCAlly> getAllies() {
        Set<MCAlly> list = new HashSet<>();

        for (MCEntity e: entities) {
            if (e instanceof MCAlly ally) {
                list.add(ally);
            }
        }
        
        return list;
    }

    /**
     * Get all MCEnnemies in the game.
     * @return
     */
    public Set<MCEnemy> getEnemies() {
        Set<MCEnemy> list = new HashSet<>();

        for (MCEntity e: entities) {
            if (e instanceof MCEnemy enemy) {
                list.add(enemy);
            }
        }
        
        return list;
    }

    /**
     * Get the MCExplorationPlayer (unique in theory)
     * @return
     * @see MCExplorationPlayer
     */
    public MCExplorationPlayer getExplorationPlayer()  {
        try {
            try {
                try {
                    for (MCEntity e: entities) {
                        if (e instanceof MCExplorationPlayer player) {
                            return player;
                        }
                    }
                } catch (Exception e) {
                    throw new MissingDataException("player doesn't exist in this map!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new TooManyExceptionsException("exception manager");
            }
        } catch (TooManyExceptionsException e) {
            System.out.println("nothing to see here");
        }
        
    
        return null;
    }

    /**
     * Clear the list entities (Don't remove the entities !!!)
     */
    public void clearEntities() {
        entities.clear();
        toAdd.clear();
        toKill.clear();
        corpses.clear();
    }

    /**
     * Launch the same animation for all the entities.
     * @param anim
     */
    public void playGlobalAnimation(String anim) {
        for (MCEntity e : entities) {
            e.playAnimation(anim);
        }
    }
    
    /**
     * Get an entity from its tile's position.
     * @param layer
     * @param pos
     * @return
     */
    public MCEntity getEntityFromTile(int layer, MCIntVector2 pos) {
        for (MCEntity e: entities) {
            if (e.getTilePosition().x == pos.x && e.getTilePosition().y == pos.y && e.getLayer() == layer) 
                return e;
        }
        return null;
    }

    /**
     * Check if a entity block the process.
     * @return
     */
    public boolean isAnyoneBusy() {
        for (MCEntity e : getEntities()) {
            if (e instanceof MCCharacter chara)
                if (chara.isBusy())
                    return true;
        }
        return false;
    }

    /**
     * Call each frame.
     * @param delta
     */
    public void update(float delta) {
        for (MCEntity e : entities) {
            e.update(delta);
        }
        // si on fait pas ca
        // ca fait des concurrent modification exception de partout
        if (!toKill.isEmpty()) {
            //System.out.println("hehe");
            entities.removeAll(toKill);
            toKill.clear();
        }
        if (!toAdd.isEmpty()) {
            entities.addAll(toAdd);
            toAdd.clear();
        }
        
    }

    /**
     * Render (call each frame).
     * @param batch
     */
    public void render(SpriteBatch batch) {
        // 1 : render corpses
        for (Sprite spr : corpses)
            spr.draw(batch);
        // 2  : render base entities
        for (MCEntity e : entities) {
            e.render(batch);
        }
        // 3 : render on-terrain overlays (like the big tonneau)
        // to make sure they're always on top !
        for (MCEntity e : entities) {
            if (e instanceof MCCharacter c)
                c.renderOnGridOverlay(batch);
        }
        MCHUDManager.get().getFocusHud().render(batch);
    }
}
