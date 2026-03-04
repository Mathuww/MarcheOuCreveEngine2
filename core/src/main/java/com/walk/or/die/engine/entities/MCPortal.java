package com.walk.or.die.engine.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.exceptions.MissingDataException;
import com.walk.or.die.engine.shared.MCUtils;
import com.walk.or.die.engine.sm.MCStateMachine;
import com.walk.or.die.engine.sm.entity.character.states.MCCSHurt;
import com.walk.or.die.engine.sm.entity.explorationplayer.MCExplorationPlayerState;
import com.walk.or.die.engine.sm.entity.explorationplayer.states.MCEPSIdle;
import com.walk.or.die.engine.sm.entity.explorationplayer.states.MCEPSMove;
import com.walk.or.die.engine.tiledmap.MCTerrainMap;

/**
 * Describes the portal entity. When an MCExplorationPlayer comes across it, the player gets teleported to the portal's destination.
 * @see MCExplorationPlayer
 */
public class MCPortal extends MCEntity {
    /** Controls the frames per second for animations. */
    private int fps;
    /** The unique identifier of this portal. */
    private int ID;
    /** The unique identifier of the destination portal. */
    private int destID;
    /** The name of the destination map. */
    private String destMap;
    /** The direction where the player should spawn in the destination map. */
    private String spawnDirection;

    /**
     * Constructs a new portal instance.
     *
     * @param parent The parent game instance.
     * @param map The terrain map.
     * @param entityGenericName The entity's generic name.
     */
    public MCPortal(MCGame parent, MCTerrainMap map, String entityGenericName) {
        super(parent, map, entityGenericName);
    }

    /**
     * Initializes the portal from map properties.
     *
     * @param props The MapProperties instance.
     * @throws IllegalStateException When the state of the entity is invalid during initialization.
     * @throws MissingDataException When the map properties are missing or invalid.
     */
    @Override
    public void initFromMapProperties(MapProperties props) throws IllegalStateException, MissingDataException {
        ID = MCUtils.getIntProperty(props, "portal_ID", -1);
        destID = MCUtils.getIntProperty(props, "destPortal_ID", -1);
        destMap = props.get("destMap", String.class);
        spawnDirection = props.get("spawnDirection", String.class);
        if(ID <= -1) {
            throw new MissingDataException("the portal " + ID + " don't have an identification !");
        } else if(destID <= -1) {
            throw new MissingDataException("the portal " + destID + " don't have an identification for the destination portal!");
        } else if (destMap == null || destMap.isEmpty()) {
            throw new MissingDataException("the portal doesn' have a desination map");
        }
        FileHandle mapFile = Gdx.files.internal(getParent().getRootMap() + destMap + ".tmx");
        if (!mapFile.exists()) {
            throw new MissingDataException("cant load " + mapFile + " for the destination map of portal because it doesnt exists.");
        }
    }

    /**
     * Gets the unique identifier of this portal.
     * @return The portal's unique identifier.
     */
    public int getPortalID() {
        return ID;
    }

    /**
     * Gets the unique identifier of the destination portal.
     * @return The destination portal's unique identifier.
     */
    public int getDestID() {
        return destID;
    }

    /**
     * Gets the name of the destination map.
     * @return The destination map's name.
     */
    public String getDestMap() {
        return destMap;
    }

    /**
     * Gets the direction at which the player should spawn in the destination map.
     * @return The spawn direction.
     */
    public String getSpawnDirection() {
        return spawnDirection;
    }

    /**
     * Called when the entity spawns.
     */
    @Override
    public void onSpawn() {
        playAnimation("idle");
    }

    /**
     * Initializes the portal from properties.
     *
     * @param props The MapProperties instance.
     * @throws Exception When an error occurs.
     */
    @Override
    public void initFromProperties(MapProperties props) throws Exception {
        fps = MCUtils.getIntProperty(props, "fps", 4);
    }

    /**
     * Called on each frame to update the portal's state.
     * @param delta The delta time, representing the time elapsed since the last frame.
     */
    @Override
    public void update(float delta) {
        super.update(delta);
    }

    /**
     * Called on each frame to render the portal.
     * @param batch The SpriteBatch instance used for rendering.
     */
    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);
    }

    /**
     * Activates the teleportation process using the portal's destination data.
     * This typically involves loading a new map.
     */
    public void teleportation() {
        getParent().teleportationActivate(getDestMap() + ".tmx", getDestID());
    }

}