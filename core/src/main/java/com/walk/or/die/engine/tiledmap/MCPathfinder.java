package com.walk.or.die.engine.tiledmap ;

import com.badlogic.gdx.math.Vector2;

import java.util.List;
import java.beans.VetoableChangeSupport;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.HashSet;
import java.util.Collections;

class Tuple {
    int g;        // distance depuis le départ
    int h;        // heuristique
    Vector2 pos;  // position
    Tuple parent; // parent pour reconstruire le chemin

    public Tuple(int g, int h, Vector2 pos, Tuple parent) {
        this.g = g;
        this.h = h;
        this.pos = pos;
        this.parent = parent;
    }
}

public class MCPathfinder {
    // A* ma gueule, algo de zigzaging
    private MCMap parent;

    public MCPathfinder(MCMap parent) {
        this.parent = parent;
    }

    public List<Vector2> getPath(Vector2 start, Vector2 end) {

        PriorityQueue<Tuple> file = new PriorityQueue<>(this::comparaison);
        file.add(new Tuple(0, getDist(start, end), start, null));

        HashSet<Vector2> closedList = new HashSet<>();

        while (!file.isEmpty()) {

            Tuple node = file.poll();
            Vector2 current = node.pos;

            if (closedList.contains(current)) continue;

            closedList.add(current);
            if (current.equals(end)) {
                return reconstructPath(node);
            }

            for (Vector2 i : getNeighbors(current)) {
                if (!closedList.contains(i)) {
                    file.add(new Tuple(node.g + 1, getDist(i, end), i, node));
                }
            }
        }

        return new ArrayList<>();
    }

    public List<Vector2> clean(List<Vector2> path) {
        if (path.size() == 0) return path;
        Vector2 current = path.get(0);
        List<Vector2> newList = new ArrayList<>();

        for (int i=1; i < path.size(); i++) {

            if (path.get(i).x != current.x && current.y != path.get(i).y) {
                newList.add(path.get(i-1));
                current = path.get(i-1);
            }
        }

        newList.add(path.get(path.size()-1));

        return newList;
    }

    private int comparaison(Tuple x, Tuple y) {
        int value_x = x.g + x.h;
        int value_y = y.g + y.h;

        if (value_x < value_y) return -1;
        else if (value_x > value_y) return 1;
        else if (x.g > y.g) return -1;

        return 0;
    }

    private int getDist(Vector2 x, Vector2 y) {
        return (int)(Math.abs(x.x - y.x) + Math.abs(y.y - x.y));
    }

    private List<Vector2> reconstructPath(Tuple endNode) {
        List<Vector2> path = new ArrayList<>();
        Tuple current = endNode;
        while (current != null) {
            path.add(current.pos);
            current = current.parent;
        }
        Collections.reverse(path);
        return clean(path);
    }

    private List<Vector2> getNeighbors(Vector2 current) {
        List<Vector2> neighbors = new ArrayList<>();

        int x = (int) current.x;
        int y = (int) current.y;

        int[][] directions = { {1, 0}, {-1, 0}, {0, 1}, {0, -1} };

        for (int[] dir : directions) {
            int nx = x + dir[0];
            int ny = y + dir[1];

            if (parent.isWalkable(nx, ny)) {
                neighbors.add(new Vector2(nx, ny));
            }
        }

        return neighbors;
    }
}