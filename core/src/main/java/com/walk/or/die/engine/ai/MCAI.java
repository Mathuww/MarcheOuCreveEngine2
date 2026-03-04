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
 * Represents the decision-making AI for enemies, calculating moves and shots based on a score system.
 */
public class MCAI {

    /**
     * The terrain map on which the AI operates.
     */
    private MCTerrainMap map;

    /**
     * The pathfinder singleton used for trajectory calculation and collision checks.
     */
    private MCPathfinder pathfinder;
    /**
     * The enemy entity controlled by this AI instance.
     */
    private MCEnemy parent;

    /**
     * A list of sprites used for debugging purposes.
     */
    private List<Sprite> sprList;
    /**
     * The texture used to visually mark valid tiles for debugging.
     */
    private TextureRegion validTileTexture;

    /**
     * Constructs a new AI instance.
     * @param map The map on which moves occur.
     * @param parent The enemy entity controlled by this AI.
     * @throws Exception If an error occurs during initialization.
     */
    public MCAI(MCTerrainMap map, MCEnemy parent) throws Exception {
        pathfinder = MCPathfinder.get();
        this.map = map;
        this.parent = parent;
    }

    /**
     * Identifies the best target based on a scoring system.
     * @param pos The enemy position.
     * @param damage The damage the enemy could inflict.
     * @return The best possible ally target, or null if none found.
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
     * Calculates the score for attacking a specific ally.
     * @param pos The enemy position (shooting source).
     * @param ally The ally target.
     * @param degats The maximum damage the enemy can inflict.
     * @return The calculated score for this target.
     */
    private float scoreVictim(MCIntVector2 pos, MCAlly ally, float degats) {

        float score = pathfinder.isCorrectTrajectory(pathfinder.getBestTrajectory(pos, ally.getTilePosition()), ally.getTilePosition());
        if (score > 0f && ally.getHealth() <= degats) score += 10f;
        //score += pos.dst2(ally.getTilePosition())*0.2f;

        return score;
    }

    /**
     * Retrieves a set of allies that can be targeted from the current position.
     * @param pos The enemy position.
     * @return A set containing all reachable allies.
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
     * Determines the optimal position to move to.
     * @param oldPos The current enemy position.
     * @param max_deplacement The maximum number of tiles the enemy can walk on.
     * @return The best coordinates to move to.
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
     * Calculates the safety score of a potential shelter position.
     * The higher the score, the less safe the shelter is.
     * @param pos The shelter position.
     * @return The calculated safety score.
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
     * Calculates the offensive potential of a shelter position.
     * The higher the score, the better the shelter is for attacking.
     * @param pos The shelter position.
     * @return The calculated shooting score.
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
     * Identifies a list of potential shelter positions within range.
     * @param pos The starting position.
     * @param maxMoves The maximum movement range in tiles.
     * @return A list of valid shelter coordinates.
     */
    private List<MCIntVector2> searchShelts(MCIntVector2 pos, int maxMoves) {
        List<MCIntVector2> list = new ArrayList<>();

        for (int x = -maxMoves; x <= maxMoves; x++) {
            for (int y = -maxMoves; y <= maxMoves; y++) {
                MCIntVector2 newV = new MCIntVector2(pos.x + x, pos.y + y);
                List<MCIntVector2> pathToNewV = pathfinder.getPath(pos, newV);
                if (
                    Math.abs(x) + Math.abs(y) <= maxMoves &&
                    newV.x < map.getWidth() && newV.y < map.getHeight() &&
                    newV.x >= 0 &&
                    newV.y >=0 &&
                    (
                        MCEntityManager.get().getEntityFromTile(1, newV) == null || MCEntityManager.get().getEntityFromTile(1, newV) == parent
                    ) &&
                    neighborsShelt(newV) &&
                    pathToNewV.size() - 1 <= maxMoves
                ) {
                        list.add(newV);
                        showSpot(newV);
                }
            }
        }



        return list;
    }

    /**
     * Checks if the specified position has adjacent protection (walls or obstacles).
     * @param pos The position to check.
     * @return True if the position is protected by neighbors, false otherwise.
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
     * Checks if a specific tile provides protection (is an obstacle and not an ally).
     * @param v The position to check.
     * @return True if the tile provides protection, false otherwise.
     */
    private boolean check(MCIntVector2 v) {
        return pathfinder.isProtect(v) && !(MCEntityManager.get().getEntityFromTile(1, v) instanceof MCAlly);
    }

    /**
     * Renders debug visuals.
     * @param batch The sprite batch used for drawing.
     */
    public void render(SpriteBatch batch) {
        for (Sprite spr : sprList) {
            spr.draw(batch);
        }
    }

    /**
     * Adds a debug marker to the specified position.
     * @param spot The position to mark.
     */
    public void showSpot(MCIntVector2 spot) {
        MCDebugRenderer.get().addDebugTile(spot);
    }

}