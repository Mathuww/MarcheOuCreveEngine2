package com.walk.or.die.engine.tiledmap ;

import com.badlogic.gdx.math.MathUtils;
//import com.badlogic.gdx.math.MathUtils;
//import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.entities.MCEntityManager;
import com.walk.or.die.engine.shared.MCIntVector2;

import java.util.List;
import java.util.ArrayDeque;
//import java.beans.VetoableChangeSupport;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;

//import javax.xml.parsers.ParserConfigurationException;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Collections;

/**
 * A useful class for A*
 */
class Tuple {
    int g;        // distance depuis le départ
    int h;        // istance min jusqu'à la fin
    MCIntVector2 pos;  // position
    Tuple parent; // parent pour reconstruire le chemin

    public Tuple(int g, int h, MCIntVector2 pos, Tuple parent) {
        this.g = g;
        this.h = h;
        this.pos = pos;
        this.parent = parent;
    }
}

/**
 * A singleton class which gives us methods to know paths and trajectories.
 */
public class MCPathfinder {
    // A* ma gueule, algo de zigzaging
    private MCGame game;
    private static MCPathfinder instance = null;

    /**
     * A class who returns a simulation of a trajectory, with the success and where it ends.
     */
    public static class Simulation {
        public boolean success;
        public MCIntVector2 endPos;

        /**
         * The constructor
         * @param success
         * @param pos
         */
        public Simulation(boolean success, MCIntVector2 pos) {
            this.success = success;
            endPos = pos;
        }
    }

    /**
     * The getter.
     */
    public static MCPathfinder get() {
        if (instance == null) instance = new MCPathfinder();
        return instance;
    }

    /**
     * The init.
     * @param game
     */
    public void init (MCGame game) {
        this.game = game;
    }

    /**
     * Get a path between 2 points using A*. Return a empty list if there's no path.
     * @param start
     * @param end
     * @return
     * @see MCIntVector2
     */
    public List<MCIntVector2> getPath(MCIntVector2 start, MCIntVector2 end) {

        PriorityQueue<Tuple> file = new PriorityQueue<>(this::comparaison);
        file.add(new Tuple(0, getDist(start, end), start, null));

        HashSet<MCIntVector2> closedList = new HashSet<>();

        while (!file.isEmpty()) {

            Tuple node = file.poll();
            MCIntVector2 current = node.pos;

            if (closedList.contains(current)) continue;

            closedList.add(current);
            if (current.equals(end)) {
                return reconstructPath(node);
            }

            for (MCIntVector2 i : getNeighbors(current)) {
                if (!closedList.contains(i)) {
                    file.add(new Tuple(node.g + 1, getDist(i, end), i, node));
                }
            }
        }

        return new ArrayList<>();
    }

    /**
     * Return if the tile at the given position is.
     * @param pos
     * @return
     */
    public boolean isWalkable(MCIntVector2 pos) {
        return game.isWalkable(pos);
    }

    /**
     * Return if a position contains something who protects from bullets.
     * @param pos
     * @return 
     */
    public boolean isProtect(MCIntVector2 pos) {
        if (MCEntityManager.get().getEntityFromTile(1, pos) == null) {
            return game.getTerrainMap().isProtect(pos);
        }
        //System.out.println(MCEntityManager.get().getEntityFromTile(1, pos));
        return true;
    }

    /**
     * Get a straight trajectory between two points, useful for bullets.
     * @param v1
     * @param v2
     * @return
     */
    private List<MCIntVector2> getTrajectory(MCIntVector2 v1, MCIntVector2 v2) {

        List<MCIntVector2> result = new ArrayList<>();

        int x1 = v1.x, x2 = v2.x;
        int y1 = v1.y, y2 = v2.y;

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            result.add(new MCIntVector2(x1, y1));
            if (x1 == x2 && y1 == y2) break;
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }

        return result;
    }

    /**
     * Cut the given list when it hit something.
     * @param list
     */
    public void cutTrajectoryOnHit(List<MCIntVector2> list) {
        Iterator<MCIntVector2> it = list.iterator();
        MCIntVector2 vec;
        boolean cut = false;

        it.next();
        while (it.hasNext()) {
            vec = it.next();
            if (cut) {
                it.remove();
            } else if (isProtect(vec)) {
                cut = true;
            }
        }
    }

    /**
     * Get the best trajectory to shoot on a target.
     * @param v1
     * @param v2
     * @return
     */
    public List<MCIntVector2> getBestTrajectory(MCIntVector2 v1, MCIntVector2 v2) {
        // Mon implémentation ne distinguera pas un personnage d'un bloc/obstacle, ce que je trouve problématique

        // Il faut simplement détecter la taille du chemin (a savoir si il fait 2) + si le bloc est a coté
        // il faut faire remplacé les quarts par des tiers

        List<MCIntVector2> try1 = getTrajectory(v1, v2);
        cutTrajectoryOnHit(try1);
        if (try1.get(try1.size()-1).equals(v2) || try1.size()==2) return try1;
        

        int dx = v2.x - v1.x;
        int dy = v2.y - v1.y;

        Queue<MCIntVector2> queuePos = new ArrayDeque<>();

        
        if (dx >= 0 && dy >= 0) { // Nord-Est
            if (Math.abs(dx) >= Math.abs(dy)) { // priorité verticale
                queuePos.add(new MCIntVector2(v1.x, v1.y + 1));
            }
            if (Math.abs(dx) <= Math.abs(dy)) { // priorité horizontale
                queuePos.add(new MCIntVector2(v1.x + 1, v1.y));
            }
        }
        else if (dx < 0 && dy >= 0) { // Nord-Ouest
            if (Math.abs(dx) >= Math.abs(dy)) { // priorité verticale
                queuePos.add(new MCIntVector2(v1.x, v1.y + 1));
            }
            if (Math.abs(dx) <= Math.abs(dy)) { // priorité horizontale
                queuePos.add(new MCIntVector2(v1.x - 1, v1.y));
            }
        }
        else if (dx >= 0 && dy < 0) { // Sud-Est
            if (Math.abs(dx) >= Math.abs(dy)) { // priorité verticale
                queuePos.add(new MCIntVector2(v1.x, v1.y - 1));
            }
            if (Math.abs(dx) <= Math.abs(dy)) { // priorité horizontale
                queuePos.add(new MCIntVector2(v1.x + 1, v1.y));
            }
        }
        if (dx < 0 && dy < 0) { // Sud-Ouest
            if (Math.abs(dx) >= Math.abs(dy)) { // priorité verticale
                queuePos.add(new MCIntVector2(v1.x, v1.y - 1));
            }
            if (Math.abs(dx) <= Math.abs(dy)) { // priorité horizontale
                queuePos.add(new MCIntVector2(v1.x - 1, v1.y));
            }
        }

        List<MCIntVector2> try2;
        while (!queuePos.isEmpty()) { // En gros on teste si ça marche, sinon on revient sur la solution de base
            try2 = getTrajectory(queuePos.poll(), v2);
            cutTrajectoryOnHit(try2);
            if (try2.get(try2.size()-1).equals(v2)) return try2;
        }
        
        // L'implémentation est simpliste, on devrait ptet garder la traj la plus longue mais bon, faut bien commencer qqpart
        return try1;
    }

    /**
     * Get a trajectory between two points, without those points, for being tested.
     * @param start
     * @param end
     * @return
     * @deprecated in theory
     */
    public List<MCIntVector2> getValidTrajectory(MCIntVector2 start, MCIntVector2 end) {
        List<MCIntVector2> list = getTrajectory(start, end);
        list.remove(0);
        list.remove(list.size()-1);
        return list;
    }

    /**
     * Test if the given trajectory can be achieved (by checking between the first and the last point).
     * @param trajectory
     * @return the results of the Test
     * @see Simulation
     */
    public Simulation simulateTrajectory(List<MCIntVector2> trajectory, MCIntVector2 end) {
        //System.out.println(trajectory);
        Iterator<MCIntVector2> it = trajectory.iterator();
        MCIntVector2 pos = it.next();
        while (it.hasNext()) {
            pos = it.next();
            if (isProtect(pos) && it.hasNext()) return new Simulation (false, pos);
            if (pos.equals(end)) return new Simulation(true, new MCIntVector2(-1, -1));
        }
        //System.out.println("hop ça marche (pas)");
        return new Simulation(false, pos);
    }

    /**
     * Get the probability of succes of a trajectory.
     * @param trajectory
     * @return
     */
    public float isCorrectTrajectory(List<MCIntVector2> trajectory, MCIntVector2 end) {
        Iterator<MCIntVector2> it = trajectory.iterator();
        MCIntVector2 pos = it.next();
        while (it.hasNext()) {
            pos = it.next();
            if (pos.equals(end)) return 1f;
            if (isProtect(pos) && it.hasNext()) return 0f;
        }

        return 0f;
    }
    
    /**
     * Clean the given path, by leaving only the intersection's positions.
     * @param path
     * @return
     * @see MCIntVector2
     */
    public List<MCIntVector2> clean(List<MCIntVector2> path) {
        if (path.size() == 0) return path;
        MCIntVector2 current = path.get(0);
        List<MCIntVector2> newList = new ArrayList<>();

        for (int i=1; i < path.size(); i++) {

            if (path.get(i).x != current.x && current.y != path.get(i).y) {
                newList.add(path.get(i-1));
                current = path.get(i-1);
            }
        }

        newList.add(path.get(path.size()-1));

        return newList;
    }

    private List<MCIntVector2> reconstructPath(Tuple endNode) {
        List<MCIntVector2> path = new ArrayList<>();
        Tuple current = endNode;
        while (current != null) {
            path.add(current.pos);
            current = current.parent;
        }
        Collections.reverse(path);
        return path;
    }

    private int comparaison(Tuple x, Tuple y) {
        int value_x = x.g + x.h;
        int value_y = y.g + y.h;

        if (value_x < value_y) return -1;
        else if (value_x > value_y) return 1;
        else if (x.g > y.g) return -1;

        return 0;
    }

    private int getDist(MCIntVector2 x, MCIntVector2 y) {
        return (int)(Math.abs(x.x - y.x) + Math.abs(y.y - x.y));
    }

    private List<MCIntVector2> getNeighbors(MCIntVector2 current) {
        List<MCIntVector2> neighbors = new ArrayList<>();

        int x = (int) current.x;
        int y = (int) current.y;

        int[][] directions = { {1, 0}, {-1, 0}, {0, 1}, {0, -1} };

        for (int[] dir : directions) {
            int nx = x + dir[0];
            int ny = y + dir[1];

            MCIntVector2 npos = new MCIntVector2(nx, ny);
            if (game.isWalkable(npos)) {
                neighbors.add(npos);
            }
        }

        return neighbors;
    }
}