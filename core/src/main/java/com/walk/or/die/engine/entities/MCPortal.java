package com.walk.or.die.engine.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.walk.or.die.engine.MCGame;
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
    //private MCStateMachine<MCExplorationPlayerState, MCEntity> stateManager;

    public MCPortal(MCGame parent, MCTerrainMap map, String entityGenericName) {
        super(parent, map, entityGenericName);
    }   

    @Override
    public void initFromMapProperties(MapProperties props) {
        ID = MCUtils.getIntProperty(props, "portal_ID", 0);
        destID = MCUtils.getIntProperty(props, "destPortal_ID", 0);
        try {
           destMap = props.get("destMap", String.class); 
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("not all portal have a destination!!");
        }
    }

    @Override
    public void onSpawn() {
        playAnimation("idle");
        System.out.println("Portail " + ID + " vers le portail " + destID + " sur " + destMap);
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
}