package com.walk.or.die.engine.ai;

import java.util.List;
import java.util.ArrayList;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.walk.or.die.engine.entities.MCAlly;
import com.walk.or.die.engine.entities.MCEntityManager;
import com.walk.or.die.engine.shared.MCDebugRenderer;
import com.walk.or.die.engine.shared.MCIntVector2;
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

    public MCIntVector2 getNewPos(MCIntVector2 oldPos, int max_deplacement) {
        List<MCIntVector2> shelts = searchShelts(oldPos, max_deplacement);

        MCIntVector2 best_shelt = oldPos;
        float best_score = -1f;
        for (MCIntVector2 shelt: shelts) {
            float score = isSheltSafe(shelt) + isSheltShootSpot(shelt);
            if (best_score == -1f || score < best_score) {
                best_score = score;
                best_shelt = shelt;
            }
        }

        return best_shelt;
    }

    public float isSheltSafe(MCIntVector2 pos) {
        List<MCAlly> list = MCEntityManager.get().getAllies();

        float result = 0f;
        for (MCAlly ally: list) {
            List<MCIntVector2> traj = pathfinder.getTrajectory(
                ally.getTilePosition(),
                pos);

            if (pathfinder.isCorrectTrajectory(traj).success) {
                result += 1f;
            }
            
        }
        /*
        get all character
        get trajectory from character to posx posy
        if is Correct Trajectory +1
        if is moyenne trajectory +0.33
        return score
        0 safe, puis ensuite comparaison
        */
        
        return result;
    }

    public float isSheltShootSpot(MCIntVector2 pos) {
        List<MCAlly> list = MCEntityManager.get().getAllies();

        float result = 0f;
        for (MCAlly ally: list) {
            List<MCIntVector2> traj = pathfinder.getTrajectory(
                pos,
                ally.getTilePosition()
            );

            if (pathfinder.isCorrectTrajectory(traj).success) {
                result += 1f;
            }
        }
        /*
        get all character
        get trajectory from posx posy to character
        if is Correct Trajectory 1
        if is moyenne trajectory 0.33
        if kill 10
        */

        return result;
    }

    public List<MCIntVector2> searchShelts(MCIntVector2 pos, int maxMoves) {
        List<MCIntVector2> list = new ArrayList<>();
        
        for (int x = -maxMoves; x <= maxMoves; x++) {
            for (int y = -maxMoves; y <= maxMoves; y++) {
                MCIntVector2 newV = new MCIntVector2(pos.x + x, pos.y + y);
                if (Math.abs(x) + Math.abs(y) <= maxMoves && 
                    newV.x < map.getWidth() && newV.y < map.getHeight() &&
                    newV.x >= 0 && newV.y >=0 &&
                    MCEntityManager.get().getEntityFromTile(1, newV) == null &&
                    neighborsShelt(newV)) {
                        list.add(newV);
                        showSpot(newV);
                }
            }
        }

        return list;
    }

    public boolean neighborsShelt(MCIntVector2 pos) {
        List<MCIntVector2> newPositions = new ArrayList<>();
        newPositions.add(new MCIntVector2(pos.x - 1, pos.y));
        newPositions.add(new MCIntVector2(pos.x+1, pos.y));
        newPositions.add(new MCIntVector2(pos.x, pos.y-1));
        newPositions.add(new MCIntVector2(pos.x, pos.y+1));

        boolean isOneProtected = newPositions.stream().anyMatch(pathfinder::isProtect);

        if (isOneProtected) {
            String str = pos.toString();
            for (MCIntVector2 p : newPositions)
                str += pathfinder.isProtect(p);
            System.out.println(str);
        }
        return isOneProtected;
    }

    public void render(SpriteBatch batch) {
        for (Sprite spr : sprList) {
            spr.draw(batch);
        }
    }

    public void showSpot(MCIntVector2 spot) { 
        MCDebugRenderer.get().addDebugTile(spot);
    }

}
