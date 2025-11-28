package com.walk.or.die.engine.ai;

import java.util.List;
import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.entities.MCEntityManager;
import com.walk.or.die.engine.shared.MCDebugRenderer;
import com.walk.or.die.engine.shared.MCSharedAssets;
import com.walk.or.die.engine.tiledmap.MCPathfinder;
import com.walk.or.die.engine.tiledmap.MCTerrainMap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


public class MCAI {
    
    private MCTerrainMap map;
    private MCPathfinder pathfinder;
    private TextureRegion validTileTexture;

    //debug
    private List<Sprite> sprList;

    public MCAI(MCTerrainMap map) throws Exception {
        pathfinder = MCPathfinder.get();
        this.map = map;
    }

    public float isSheltSafe(int posX, int posY) {
        /*
        get all character
        get trajectory from character to posx posy
        if is Correct Trajectory +1
        if is moyenne trajectory +0.33
        return score
        0 safe, puis ensuite comparaison
        */
        
        return 1f;
    }

    public float isSheltShootSpot(int posX, int posY) {
        /*
        get all character
        get trajectory from posx posy to character
        if is Correct Trajectory 1
        if is moyenne trajectory 0.33
        if kill 10
        */

        return 1f;
    }

    public List<Vector2> searchShelts(int posX, int posY, int max_deplacement) {
        List<Vector2> list = new ArrayList<>();
        
        for (int x = -max_deplacement; x <= max_deplacement; x++) {
            for (int y = -max_deplacement; y <= max_deplacement; y++) {
                if (Math.abs(x) + Math.abs(y) <= max_deplacement && 
                    posX+x < map.getWidth() && posY+y < map.getHeight() &&
                    posX+x>=0 && posY+y>=0 &&
                    MCEntityManager.get().getEntityFromTile(1, new Vector2(posX+x, posY+y)) == null &&
                    neighborsShelt(posX+x, posY+y)) {
                        list.add(new Vector2(posX+x, posY+y));
                        showSpot(posX + x, posY + y);
                }
            }
        }

        return list;
    }

    public boolean neighborsShelt(int posX, int posY) {
        if (pathfinder.isProtect(posX-1, posY) || 
            pathfinder.isProtect(posX+1, posY) ||
            pathfinder.isProtect(posX, posY-1) ||
            pathfinder.isProtect(posX, posY+1))
        System.out.println("(" + posX + "," + posY + ") : " + pathfinder.isProtect(posX-1, posY) +
         pathfinder.isProtect(posX+1, posY) + pathfinder.isProtect(posX, posY-1) + 
        pathfinder.isProtect(posX, posY+1) );
        return pathfinder.isProtect(posX-1, posY) || 
            pathfinder.isProtect(posX+1, posY) ||
            pathfinder.isProtect(posX, posY-1) ||
            pathfinder.isProtect(posX, posY+1) ;
    }

    public void render(SpriteBatch batch) {
        for (Sprite spr : sprList) {
            spr.draw(batch);
        }
    }

    public void showSpot(int x, int y) { 
        MCDebugRenderer.get().addDebugTile(new Vector2(x, y));
    }
}
