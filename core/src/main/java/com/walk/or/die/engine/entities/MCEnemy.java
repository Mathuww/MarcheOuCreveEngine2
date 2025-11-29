package com.walk.or.die.engine.entities;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.ai.MCAI;
import com.walk.or.die.engine.sm.entity.states.MCESClickMove;
import com.walk.or.die.engine.tiledmap.MCPathfinder;
import com.walk.or.die.engine.tiledmap.MCTerrainMap;

public class MCEnemy extends MCCharacter{

    private MCAI ai;

    public MCEnemy(MCGame parent, MCTerrainMap map, String entityGenericName) throws Exception {
        super(parent, map, entityGenericName);
        ai = new MCAI(map);
    }

    public void play() {
        Vector2 pos = ai.getNewPos(MathUtils.floor(getTilePosition().x), MathUtils.floor(getTilePosition().y), getMaxMoves());
        /* getStateManager().getCurrentState().changeState(
            "click_move", 
            new MCESClickMove.MoveStateArgs(pos, MCPathfinder.get().getPath(getTilePosition(), pos))); */
        // J'avoue c'est une méthode de brigand mais bon jsp encore trop quoi faire
    }
    

}
