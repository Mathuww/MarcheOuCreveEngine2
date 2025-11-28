package com.walk.or.die.engine.entities;

import com.badlogic.gdx.Game;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.ai.MCAI;
import com.walk.or.die.engine.tiledmap.MCTerrainMap;

public class MCEnemy extends MCCharacter{

    MCAI ai;

    public MCEnemy(MCGame parent, MCTerrainMap map, String entityGenericName) throws Exception {
        super(parent, map, entityGenericName);
        ai = new MCAI(map);
    }

    

}
