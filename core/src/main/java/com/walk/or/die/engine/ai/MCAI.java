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


public class MCAI {
    
    private MCTerrainMap map;
    private MCPathfinder pathfinder;
    private TextureRegion validTileTexture;
    private MCEnemy parent;

    //debug
    private List<Sprite> sprList;

    public MCAI(MCTerrainMap map, MCEnemy parent) throws Exception {
        pathfinder = MCPathfinder.get();
        this.map = map;
        this.parent = parent;
    }

    public MCAlly getBestShootableAlly(MCIntVector2 pos, float degats) {
        float best_score = -1f;
        MCAlly bestVictim = null;

        for (MCAlly ally: getShootableAllies(pos)) {
            float newScore = scoreVictim(pos, ally, degats);
            if (newScore > best_score) {
                //System.out.println("Selection AI" + ally + " : " + newScore);
                bestVictim = ally;
                best_score = newScore;
            } 
        }
        return bestVictim;
    }

    private float scoreVictim(MCIntVector2 pos, MCAlly ally, float degats) {
        float score = 0f;
        if (ally.getHealth() <= degats) score += 10f;
        if (pathfinder.isCorrectTrajectory(pathfinder.getTrajectory(ally.getTilePosition(), pos)).success) score += 2f;
        //score += pos.dst2(ally.getTilePosition())*0.2f;

        return score;
    }

    private Set<MCAlly> getShootableAllies(MCIntVector2 pos) {
        Set<MCAlly> allies = new HashSet<>();
        for (MCAlly ally: MCEntityManager.get().getAllies()) {
            List<MCIntVector2> traj = pathfinder.getTrajectory(pos, ally.getTilePosition());
            traj.remove(traj.size() - 1); // on prend pas en compte le dernier, c'est la cible (donc forcément pas walkable)
            traj.remove(0);
            if (pathfinder.isCorrectTrajectory(traj).success && parent.getAttack().isValidTile(ally.getTilePosition())) {
                //System.out.println(ally);
                allies.add(ally);
            }
        }
        //System.out.println("##########################" + allies);
        return allies;
    }

    public MCIntVector2 getNewPos(MCIntVector2 oldPos, int max_deplacement) {
        List<MCIntVector2> shelts = searchShelts(oldPos, max_deplacement);

        MCIntVector2 best_shelt = oldPos;
        float best_score = -1000f;
        for (MCIntVector2 shelt: shelts) {
            float score = isSheltShootSpot(shelt) - isSheltSafe(shelt)*2f; // CACA
            if (best_score == -1000f || score > best_score) {
                System.out.println("Total :" + score + "pour la pos" + shelt);
                best_score = score;
                best_shelt = shelt;
            }
        }

        return best_shelt;
    }

    private float isSheltSafe(MCIntVector2 pos) {
        Set<MCAlly> list = MCEntityManager.get().getAllies();

        float result = 0f;
        if (MCEntityManager.get().getEntityFromTile(1, pos) instanceof MCCharacter) return 100f;
        for (MCAlly ally: list) {
            List<MCIntVector2> traj = pathfinder.getTrajectory(
                ally.getTilePosition(),
                pos);
            traj.remove(traj.size() - 1); // on prend pas en compte le dernier, c'est la cible (donc forcément pas walkable)
            traj.remove(0);
            if (pathfinder.isCorrectTrajectory(traj).success) {
                result += 1f;
            }
            
        }
        System.out.println("score safe " + result + " pour la pos " + pos);
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

    private float isSheltShootSpot(MCIntVector2 pos) {
        Set<MCAlly> list = MCEntityManager.get().getAllies();

        float result = 0f;
        for (MCAlly ally: list) {
            List<MCIntVector2> traj = pathfinder.getTrajectory(
                pos,
                ally.getTilePosition()
            );
            traj.remove(traj.size() - 1); // on prend pas en compte le dernier, c'est la cible (donc forcément pas walkable)
            traj.remove(0);
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
        System.out.println("score attaque " + result + " pour la pos " + pos);
        return result;
    }

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

    private boolean check(MCIntVector2 v) {
        return pathfinder.isProtect(v) && !(MCEntityManager.get().getEntityFromTile(1, v) instanceof MCAlly);
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
