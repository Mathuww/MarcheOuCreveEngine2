package com.walk.or.die.engine.ai;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.walk.or.die.engine.entities.MCAlly;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.entities.MCEnemy;
import com.walk.or.die.engine.entities.MCEntityManager;
import com.walk.or.die.engine.shared.MCDebugRenderer;
import com.walk.or.die.engine.shared.MCIntVector2;
import com.walk.or.die.engine.tiledmap.MCPathfinder;
import com.walk.or.die.engine.tiledmap.MCTerrainMap;

/**
 * The class which takes decisions for enemies, for moving or shooting.<br>
 * All decisions are based on a score system.
 */
public class MCAI {
    
    private MCTerrainMap map;

    /**
     * The pathfinder singleton object, required to get trajectories and to check for blocked spots. 
     */
    private MCPathfinder pathfinder;
    /**
     * The enemy controlled by this AI instance.
     */
    private MCEnemy parent;

    /**
     * Debug purpose.
     */
    private List<Sprite> sprList;
    /**
     * Debug purpose.
     */
    private TextureRegion validTileTexture;

    /**
     * The constructor.
     * @param map The map on which the moves will occur.
     * @param parent The parent game.
     * @throws Exception
     */
    public MCAI(MCTerrainMap map, MCEnemy parent) throws Exception {
        pathfinder = MCPathfinder.get();
        this.map = map;
        this.parent = parent;
    }

    /**
     * Returns the best possible target, by score.
     * @param pos The enemy position.
     * @param damage The damage the enemy could inflict.
     * @return The best possible target.
     */
    public MCAlly getBestShootableAlly(MCIntVector2 pos, float damage) {
        float best_score = -1f;
        MCAlly bestVictim = null;

        for (MCAlly ally: getShootableAllies(pos)) {
            float newScore = scoreVictim(pos, ally, damage);
            if (newScore > best_score) {
                //System.out.println("Selection AI" + ally + " : " + newScore);
                bestVictim = ally;
                best_score = newScore;
            } 
        }
        return bestVictim;
    }

    /**
     * Returns the score given by shooting a specified ally.
     * @param pos The enemy position (shooting source).
     * @param ally The ally position (shooting target).
     * @param degats The max damage the enemy could inflict.
     * @return The ally score.
     */
    private float scoreVictim(MCIntVector2 pos, MCAlly ally, float degats) {

        float score = pathfinder.isCorrectTrajectory(pathfinder.getBestTrajectory(pos, ally.getTilePosition()), ally.getTilePosition());
        if (score > 0f && ally.getHealth() <= degats) score += 10f;
        //score += pos.dst2(ally.getTilePosition())*0.2f;

        return score;
    }

    /**
     * Returns the allies reachable from the enemy position.
     * @param pos The enemy position.
     * @return The allies.
     */
    private Set<MCAlly> getShootableAllies(MCIntVector2 pos) {
        Set<MCAlly> allies = new HashSet<>();
        for (MCAlly ally: MCEntityManager.get().getAllies()) {
            List<MCIntVector2> traj = pathfinder.getBestTrajectory(pos, ally.getTilePosition());
            if (pathfinder.isCorrectTrajectory(traj, ally.getTilePosition()) != 0f && parent.getAttack().isValidTile(ally.getTilePosition())) {
                //System.out.println(ally);
                allies.add(ally);
            }
        }
        //System.out.println("##########################" + allies);
        return allies;
    }

    /**
     * Returns what the AI thinks is the best position to move on.
     * @param oldPos The current enemy position.
     * @param max_deplacement The max number of tiles we can walk on to reach destination.
     * @return The best position to move.
     */
    public MCIntVector2 getNewPos(MCIntVector2 oldPos, int max_deplacement) {
        List<MCIntVector2> shelts = searchShelts(oldPos, max_deplacement);

        MCIntVector2 best_shelt = oldPos;
        float best_score = -1000f;
        for (MCIntVector2 shelt: shelts) {
            float score = isSheltShootSpot(shelt) - isSheltSafe(shelt)*2f; // CACA
            if (best_score == -1000f || score > best_score) {
                //System.out.println("Total :" + score + "pour la pos" + shelt);
                best_score = score;
                best_shelt = shelt;
            }
        }

        return best_shelt;
    }

    /**
     * Returns the safety score of a shelter. 
     * The higher the score, the less safe the shelter is.
     * @param pos The shelter position.
     * @return The score.
     */
    private float isSheltSafe(MCIntVector2 pos) {
        Set<MCAlly> list = MCEntityManager.get().getAllies();

        float result = 0f;
        if (MCEntityManager.get().getEntityFromTile(1, pos) instanceof MCCharacter) return 100f;
        for (MCAlly ally: list) {
            List<MCIntVector2> traj = pathfinder.getBestTrajectory(
                ally.getTilePosition(),
                pos);
            result += pathfinder.isCorrectTrajectory(traj, pos);
            
        }
        //System.out.println("score safe " + result + " pour la pos " + pos);
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

    /**
     * Returns the shooting score of a shelter. 
     * The higher the score, the better the shelter is.
     * @param pos The shelter position.
     * @return The score.
     */
    private float isSheltShootSpot(MCIntVector2 pos) {
        Set<MCAlly> list = MCEntityManager.get().getAllies();

        float result = 0f;
        for (MCAlly ally: list) {
            List<MCIntVector2> traj = pathfinder.getBestTrajectory(
                pos,
                ally.getTilePosition()
            );
            
            result += pathfinder.isCorrectTrajectory(traj, ally.getTilePosition());
        }
        /*
        get all character
        get trajectory from posx posy to character
        if is Correct Trajectory 1
        if is moyenne trajectory 0.33
        if kill 10
        */
        //System.out.println("score attaque " + result + " pour la pos " + pos);
        return result;
    }

    /**
     * Returns a list of potentially protected positions.
     * @param pos The shelter position.
     * @param maxMoves The max number of tiles we can walk on to reach destination.
     * @return The list of positions.
     */
    private List<MCIntVector2> searchShelts(MCIntVector2 pos, int maxMoves) {
        List<MCIntVector2> list = new ArrayList<>();
        
        for (int x = -maxMoves; x <= maxMoves; x++) {
            for (int y = -maxMoves; y <= maxMoves; y++) {
                MCIntVector2 newV = new MCIntVector2(pos.x + x, pos.y + y);
                if (Math.abs(x) + Math.abs(y) <= maxMoves && 
                    newV.x < map.getWidth() && newV.y < map.getHeight() &&
                    newV.x >= 0 && newV.y >=0 &&
                    (MCEntityManager.get().getEntityFromTile(1, newV) == null || MCEntityManager.get().getEntityFromTile(1, newV) == parent) &&
                    neighborsShelt(newV)) {
                        list.add(newV);
                        showSpot(newV);
                }
            }
        }



        return list;
    }

    /**
     * Returns whether something around is protecting the position.
     * @param pos The position.
     * @return 
     */
    private boolean neighborsShelt(MCIntVector2 pos) {
        List<MCIntVector2> newPositions = new ArrayList<>();
        newPositions.add(new MCIntVector2(pos.x - 1, pos.y));
        newPositions.add(new MCIntVector2(pos.x+1, pos.y));
        newPositions.add(new MCIntVector2(pos.x, pos.y-1));
        newPositions.add(new MCIntVector2(pos.x, pos.y+1));

        boolean isOneProtected = newPositions.stream().anyMatch(this::check); // || ....

        /*
        if (isOneProtected) {
            String str = pos.toString();
            for (MCIntVector2 p : newPositions)   
                str += pathfinder.isProtect(p);
            //System.out.println(str);
        } */
        return isOneProtected;
    }

    /**
     * Return whether the position is filled.
     * @param v The position.
     * @return
     */
    private boolean check(MCIntVector2 v) {
        return pathfinder.isProtect(v) && !(MCEntityManager.get().getEntityFromTile(1, v) instanceof MCAlly);
    }

    /**
     * Render (call each frame)
     * @param batch
     */
    public void render(SpriteBatch batch) {
        for (Sprite spr : sprList) {
            spr.draw(batch);
        }
    }

    /**
     * Debug function. (Adds a static sprite to debug the given position.)
     * @param spot
     */
    public void showSpot(MCIntVector2 spot) { 
        MCDebugRenderer.get().addDebugTile(spot);
    }

}
