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
 * The portal entity. When an MCExplorationPlayer comes across it, it gets teleported to the portal's destination.
 * @see MCExplorationPlayer
 */
public class MCPortal extends MCEntity {
    private int fps;
    private int ID;
    private int destID;
    private String destMap;
    private String spawnDirection;

    public MCPortal(MCGame parent, MCTerrainMap map, String entityGenericName) {
        super(parent, map, entityGenericName);
    }

    /**
     * Initializes from map properties.
     *
     * @param props the MapProperties instance
     * @throws MissingDataException When the map properties are missing or invalid
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
     * Gets the portal ID.
     * @return the portal ID.
     */
    public int getPortalID() {
        return ID;
    }

    /**
     * Gets the portal ID to teleport to.
     * @return The destination portal ID.
     */
    public int getDestID() {
        return destID;
    }

    /**
     * Gets the destination map
     * @return the destinationn map.
     */
    public String getDestMap() {
        return destMap;
    }

    /**
     * Gets at which tile the playe should spawn in the destination map.
     * @return the spawn direction.
     */
    public String getSpawnDirection() {
        return spawnDirection;
    }

    /**
     * Called on spawn
     */
    @Override
    public void onSpawn() {
        playAnimation("idle");
    }

    /**
     * Initializes from properties.
     *
     * @param props the MapProperties instance
     * @throws Exception When an error occurs
     */
    @Override
    public void initFromProperties(MapProperties props) throws Exception {
        fps = MCUtils.getIntProperty(props, "fps", 4);
    }

    /**
     * Called on each frame.
     * @param delta the delta time
     */
    @Override
    public void update(float delta) {
        super.update(delta);
    }

    /**
     * Called on each frame.
     * @param batch the SpriteBatch instance
     */
    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);
    }

    /**
     * Activates the teleportation (load the new map) with the portal data.
     */
    public void teleportation() {
        getParent().teleportationActivate(getDestMap() + ".tmx", getDestID());
    }
}