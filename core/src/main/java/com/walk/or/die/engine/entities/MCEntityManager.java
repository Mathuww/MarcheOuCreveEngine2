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
    /**
     * The singleton instance of the entity manager.
     */
    private static MCEntityManager instance = null;

    /**
     * Gets the singleton instance of the entity manager.
     * @return The instance of the entity manager.
     */
    public static MCEntityManager get() {
        if (instance == null) instance = new MCEntityManager();
        return instance;
    }

    /**
     * Constructs a new MCEntityManager.
     * This constructor is private to enforce the singleton pattern.
     */
    private MCEntityManager() {}

    /**
     * The parent game instance.
     */
    private MCGame parent;

    /**
     * Initializes the singleton.
     * @param game The game instance.
     */
    public void init(MCGame game) {
        parent = game;
        MCEventBus.get().on(this, "freezeGame", this::freezeAll);
        MCEventBus.get().on(this, "unfreezeGame", this::unfreezeAll);
    }

    //private MCGame parent;
    /**
     * The set of all active entities in the game.
     */
    private Set<MCEntity> entities = Collections.newSetFromMap(new IdentityHashMap<>());
    /**
     * A temporary set of entities to be removed during the next update cycle.
     */
    private Set<MCEntity> toKill = Collections.newSetFromMap(new IdentityHashMap<>());
    /**
     * A temporary set of entities to be added during the next update cycle.
     */
    private Set<MCEntity> toAdd = Collections.newSetFromMap(new IdentityHashMap<>());
    /**
     * An array of sprite representations of entities that have been "killed" but their corpses are still visible.
     */
    private Array<Sprite> corpses = new Array<>();

    /**
     * Creates a projectile entity.
     * @param projType The projectile type.
     * @return The created projectile.
     * @throws Exception if an error occurs during projectile creation.
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
     * @param except The entity to exclude from freezing.
     */
    public void freezeAll(MCEntity except) {
        //System.out.println("Freeze");
        for (MCEntity e: entities) {
            if (e != except) e.setFreeze(true);
        }
    }

    /**
     * Unfreezes all entities.
     * @param c The object that triggered the unfreeze.
     */
    public void unfreezeAll(Object c) {
        //System.out.println("Unfreeze");
        for (MCEntity e: entities) {
            e.setFreeze(false);
        }
    }
    
    /**
     * Removes an entity from the game.
     * @param e The entity to remove.
     */
    public void kill(MCEntity e) {
        toKill.add(e);
    }

    /**
     * Removes an entity and preserves its sprite as a corpse.
     * @param e The entity to remove.
     */
    public void killAndKeepCorpse(MCEntity e) {
        corpses.add(e.getSprite());
        toKill.add(e);
    }

    /**
     * Adds a new entity to be processed in the next update cycle.
     * @param e The entity to add.
     */
    public void addEntity(MCEntity e) {
        toAdd.add(e);
        //entities.add(e);
    }

    /**
     * Adds a set of exploration-related entities to the game, and manages the player.
     * @param entities The set of entities to add.
     * @param player The existing exploration player, or null if a new one should be created.
     * @return The exploration player, either the existing one or a newly created one.
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
     * Adds a set of entities to be processed in the next update cycle.
     * @param e The set of entities to add.
     */
    public void addAllEntities(Set<MCEntity> e) {
        toAdd.addAll(e);
        //entities.addAll(e);
    }

    /**
     * Gets all entities currently active in the game.
     * @return The set of entities.
     */
    public Set<MCEntity> getEntities() {
        return this.entities;
    }

    /**
     * Gets all MCAllies currently active in the game.
     * @return The set of allies.
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
     * Gets the MCAlly that has the highest priority to become a MCExplorationPlayer.
     * @return The chosen ally.
     * @throws IllegalStateException If no ally is found.
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
     * Gets all MCEnemies currently active in the game.
     * @return The set of enemies.
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
     * Gets the MCExplorationPlayer (which should be unique).
     * @return The exploration player, or null if not found.
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
     * Spawns the exploration player near a specified portal.
     * @param entities The set of entities in the current map.
     * @param destID The ID of the destination portal.
     * @param player The exploration player to spawn.
     * @throws IllegalStateException If the destination portal does not exist or has an invalid spawn direction.
     * @throws MissingDataException If the portal is missing a 'spawnDirection' property.
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
     * Clears the list of all active entities, entities to add, entities to kill, and corpses.
     */
    public void clearEntities() {
        entities.clear();
        toAdd.clear();
        toKill.clear();
        corpses.clear();
    }

    /**
     * Launches the same animation for all entities.
     * @param anim The animation name.
     */
    public void playGlobalAnimation(String anim) {
        for (MCEntity e : entities) {
            e.playAnimation(anim);
        }
    }
    
    /**
     * Gets an entity from its tile's position.
     * @param layer The layer of the tile.
     * @param pos The position of the tile.
     * @return The entity at the specified tile position, or null if no entity is found.
     */
    public MCEntity getEntityFromTile(int layer, MCIntVector2 pos) {
        for (MCEntity e: entities) {
            if (e.getTilePosition().x == pos.x && e.getTilePosition().y == pos.y && e.getLayer() == layer) 
                return e;
        }
        return null;
    }

    /**
     * Checks if any character entity is currently busy.
     * @return True if anyone is busy, false otherwise.
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
     * Called on each frame to update the state of all entities.
     * @param delta The time delta since the last frame.
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
     * Called on each frame to render all entities and corpses.
     * @param batch The sprite batch used for rendering.
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