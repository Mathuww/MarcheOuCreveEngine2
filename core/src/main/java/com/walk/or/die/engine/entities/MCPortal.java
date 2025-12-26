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

public class MCPortal extends MCEntity {
    private int fps;
    private int ID;
    private int destID;
    private String destMap;

    public MCPortal(MCGame parent, MCTerrainMap map, String entityGenericName) {
        super(parent, map, entityGenericName);
    }   

    @Override
    public void initFromMapProperties(MapProperties props) throws MissingDataException {
        ID = MCUtils.getIntProperty(props, "portal_ID", -1);
        destID = MCUtils.getIntProperty(props, "destPortal_ID", -1);
        destMap = props.get("destMap", String.class);
        if(ID < -1) {
            throw new MissingDataException("the portal " + ID + " don't have an identification !");
        } else if(destID < -1) {
            throw new MissingDataException("the portal " + destID + " don't have an identification for the destination portal!");
        } else if (destMap == null || destMap.isEmpty()) {
            throw new MissingDataException("the portal doesn' have a desination map");
        }
        FileHandle mapFile = Gdx.files.internal(getParent().getRootMap() + destMap + ".tmx");
        if (!mapFile.exists()) {
            throw new MissingDataException("cant load " + mapFile + " for the destination map of portal because it doesnt exists.");
        }
    }

    @Override
    public void onSpawn() {
        playAnimation("idle");
    }

    @Override
    public void initFromProperties(MapProperties props) throws Exception {
        fps = MCUtils.getIntProperty(props, "fps", 4);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);
    }

    /**
     * Activate the teleportation (load the new map) with the portal data
     */
    public void teleportation() {
        getParent().teleportationActivate(destMap + ".tmx");
    }
}