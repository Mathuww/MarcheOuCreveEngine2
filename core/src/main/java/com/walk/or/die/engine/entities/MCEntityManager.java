package com.walk.or.die.engine.entities;

import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.exceptions.MissingDataException;
import com.walk.or.die.engine.exceptions.TooManyExceptionsException;
import com.walk.or.die.engine.shared.MCEventBus;
import com.walk.or.die.engine.shared.MCIntVector2;
import com.walk.or.die.engine.ui.MCHUDManager;
import com.walk.or.die.engine.tiledmap.MCTerrainMap;

/**
 * A singleton that manages all the entities in the game.
 */
public class MCEntityManager {
    private static MCEntityManager instance = null;

    /**
     * Gets the instance.
     * @return the instance
     */
    public static MCEntityManager get() {
        if (instance == null) instance = new MCEntityManager();
        return instance;
    }

    private MCEntityManager() {}

    private MCGame parent;

    /**
     * Initializes the singleton.
     * @param game the game instance
     */
    public void init(MCGame game) {
        parent = game;
        MCEventBus.get().on(this, "freezeGame", this::freezeAll);
        MCEventBus.get().on(this, "unfreezeGame", this::unfreezeAll);
    }

    //private MCGame parent;
    private Set<MCEntity> entities = Collections.newSetFromMap(new IdentityHashMap<>());
    private Set<MCEntity> toKill = Collections.newSetFromMap(new IdentityHashMap<>());
    private Set<MCEntity> toAdd = Collections.newSetFromMap(new IdentityHashMap<>());
    private Array<Sprite> corpses = new Array<>();

    /**
     * Creates a projectile entity.
     * @param projType the projectile type
     * @return the created projectile
     * @throws Exception if an error occurs during projectile creation
     * @see MCProjectile
     */
    public MCProjectile buildProjectile(String projType) throws Exception {
        // projectiles can only be built once !!!!! once at a time !!! 
        MCProjectile proj = (MCProjectile) MCEntityFactory.get().build(
            parent, 
            parent.getTerrainMap(), 
            projType, 
            "projectile", null);
        
        //if (proj != null) {
        //    addEntity(proj);
        //}
        addEntity(proj);
        //System.out.println("added : " + proj);
        return proj;
    }

    /**
     * Freezes all entities except the specified one.
     * @param except the entity to exclude from freezing
     */
    public void freezeAll(MCEntity except) {
        //System.out.println("Freeze");
        for (MCEntity e: entities) {
            if (e != except) e.setFreeze(true);
        }
    }

    /**
     * Unfreezes all entities.
     * @param c the object that triggered the unfreeze
     */
    public void unfreezeAll(Object c) {
        //System.out.println("Unfreeze");
        for (MCEntity e: entities) {
            e.setFreeze(false);
        }
    }
    
    /**
     * Removes an entity.
     * @param e the entity to remove
     */
    public void kill(MCEntity e) {
        toKill.add(e);
    }

    /**
     * Removes an entity and shows its corpse.
     * @param e the entity to remove
     */
    public void killAndKeepCorpse(MCEntity e) {
        corpses.add(e.getSprite());
        toKill.add(e);
    }

    /**
     * Adds a new entity.
     * @param e the entity to add
     */
    public void addEntity(MCEntity e) {
        toAdd.add(e);
        //entities.add(e);
    }

    /**
     * Adds exploration entities.
     * @param entities the set of entities to add
     * @return what will be the exploration player
     */
    public MCExplorationPlayer addExplorationEntities(Set<MCEntity> entities, MCExplorationPlayer player) {
        Set<MCAlly> list = new HashSet<>();
        MCExplorationPlayer newPlayer;

        if(player == null) {
            Set<MCAlly> allies = new HashSet<>();

            for (MCEntity e : entities) {
                if (e instanceof MCAlly ally) {
                    allies.add(ally);
                }
            }
            
            MCAlly chosen = allies.iterator().next();
            newPlayer = new MCExplorationPlayer(chosen);
            addEntity(newPlayer);
            newPlayer.onSpawn();
        } else {
            newPlayer = player;

        }

        //addEntity(newPlayer);
        //newPlayer.onSpawn();

        for (MCEntity e : entities) {
            if (!(e instanceof MCEnemy enemy) && !(e instanceof MCAlly ally)) {
                addEntity(e);
            }
        }

        return newPlayer;
    }

    /**
     * Adds a set of entities.
     * @param e the set of entities to add
     */
    public void addAllEntities(Set<MCEntity> e) {
        toAdd.addAll(e);
        //entities.addAll(e);
    }

    /**
     * Gets all entities in the game.
     * @return the set of entities
     */
    public Set<MCEntity> getEntities() {
        return this.entities;
    }

    /**
     * Gets all MCAllies in the game.
     * @return the set of allies
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
     * Gets the MCAlly that has the highest priority to become a MCExplorationPlayer
     * @return The chosen ally.
     * @see MCAlly
     */
    public MCAlly getBestAlly() throws IllegalStateException {
        MCAlly chosen = null;

        int maxHP = 0;
        int maxPriorityLevel = 0;

        for (MCEntity e: entities) {
            if (e instanceof MCAlly ally) {
                if(maxPriorityLevel < ally.getPriorityLevel()) {
                    chosen = ally;
                    maxPriorityLevel = ally.getPriorityLevel();
                    maxHP = ally.getHealth();
                } else if ((maxPriorityLevel == ally.getPriorityLevel()) && (maxHP < ally.getHealth())) {
                    chosen = ally;
                    maxHP = ally.getHealth();    
                }
            }
        }

        if(chosen == null) {
            throw new IllegalStateException("Missing ally for continuous game in exploration player!");
        }

        return chosen;
    }

    /**
     * Gets all MCEnemies in the game.
     * @return the set of enemies
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
     * Gets the MCExplorationPlayer (unique in theory).
     * @return the exploration player
     * @see MCExplorationPlayer
     */
    public MCExplorationPlayer getExplorationPlayer()  {
        for (MCEntity e: entities) {
            if (e instanceof MCExplorationPlayer player) {
                return player;
            }
        }
        return null;
    }

    /**
     * 
     */
    public void spawnExplorationPlayerWithPortal(Set<MCEntity> entities, int destID, MCExplorationPlayer player) throws IllegalStateException, MissingDataException {
        Boolean findPortal = false;

        for (MCEntity e: entities) {
            if (e instanceof MCPortal portal) {
                if(portal.getPortalID() == destID) {
                    findPortal = true;
                    //System.out.println("Portal Trouvé");
                    MCIntVector2 direction = portal.getTilePosition();
                    String spawnDirection = portal.getSpawnDirection();
                    if (spawnDirection == null)
                        throw new MissingDataException("missing spawnDirection prop. for portal " + portal.getPortalID());
                    if (spawnDirection.isEmpty() || !(spawnDirection.equals("top") || spawnDirection.equals("bottom") || spawnDirection.equals("left") || spawnDirection.equals("right"))) {
                        throw new IllegalStateException("the portal " + portal.getPortalID() + " doesn't have a good direction. The possible direction are 'bottom', 'left', 'top' and 'right'.");
                    }
                    if(portal.getSpawnDirection().equals("bottom")) {
                        direction.y -= 1;
                    } else if(portal.getSpawnDirection().equals("right")) {
                        direction.x += 1;
                    } else if(portal.getSpawnDirection().equals("top")) {
                        direction.y += 1;
                    } else if(portal.getSpawnDirection().equals("left")) {
                        direction.x -= 1;
                    }

                    player.setTilePosition(direction);
                    
                    addEntity(player);
                    player.onSpawn();
                }
            }
        }

        if(findPortal == false) {
            throw new IllegalStateException("the destination portal " + destID + " doesn't exist in the next map!");
        }
    }

    /**
     * Clears the list of entities.
     */
    public void clearEntities() {
        entities.clear();
        toAdd.clear();
        toKill.clear();
        corpses.clear();
    }

    /**
     * Launches the same animation for all the entities.
     * @param anim the animation name
     */
    public void playGlobalAnimation(String anim) {
        for (MCEntity e : entities) {
            e.playAnimation(anim);
        }
    }
    
    /**
     * Gets an entity from its tile's position.
     * @param layer the layer of the tile
     * @param pos the position of the tile
     * @return the entity at the specified tile position
     */
    public MCEntity getEntityFromTile(int layer, MCIntVector2 pos) {
        for (MCEntity e: entities) {
            if (e.getTilePosition().x == pos.x && e.getTilePosition().y == pos.y && e.getLayer() == layer) 
                return e;
        }
        return null;
    }

    /**
     * Checks if an entity blocks the process.
     * @return true if anyone is busy, false otherwise
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
     * Called on each frame.
     * @param delta the time delta
     */
    public void update(float delta) {
        for (MCEntity e : entities) {
            //if (e instanceof MCProjectile p) System.out.println(p);
            if (!e.isFreeze()) e.update(delta);
        }
        // si on fait pas ca
        // ca fait des concurrent modification exception de partout
        if (!toKill.isEmpty()) {
            //System.out.println("hehe");
            entities.removeAll(toKill);
            toKill.clear();

            if (getAllies().isEmpty()) {
                MCEventBus.get().emit("CombatDone", MCGame.CombatDoneArgs.ENEMIES_WON);
                //System.out.println("sending  enemies won event");
            } else if (getEnemies().isEmpty()) {
                MCEventBus.get().emit("CombatDone", MCGame.CombatDoneArgs.ALLIES_WON);
                //System.out.println("sending  zalloies won event");
            }
        }
        if (!toAdd.isEmpty()) {
            entities.addAll(toAdd);
            //System.out.println(toAdd);
            toAdd.clear();
        }
        
    }

    /**
     * Called on each frame.
     * @param batch the sprite batch
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
                c.renderEffects(batch);
        }
        MCHUDManager.get().getFocusHud().render(batch);
    }
}