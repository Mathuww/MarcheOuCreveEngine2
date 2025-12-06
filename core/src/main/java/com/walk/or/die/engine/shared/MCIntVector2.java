package com.walk.or.die.engine.shared;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.input.MCInputManager.DirectionalCommand;

/**
 * Our personal Vector2, which uses int instead of float.
 * @see Vector2
 */
public class MCIntVector2 {
    public int x;
    public int y;

    /**
     * The constructor from int.
     * @param x
     * @param y
     */
    public MCIntVector2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * The constructor from float.
     * @param x
     * @param y
     */
    public MCIntVector2(float x, float y) {
        this.x = MathUtils.floor(x);
        this.y = MathUtils.floor(y);
    }

    /**
     * The constructor from Vector2 from libGdx.
     * @param v
     * @see Vector2
     */
    public MCIntVector2(Vector2 v) {
        this.x = MathUtils.floor(v.x);
        this.y = MathUtils.floor(v.y);
    }

    /**
     * A convertissor to Vector2 from libGdx.
     * @return
     */
    public Vector2 toGdxVect() {
        return new Vector2(x, y);
    } 

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MCIntVector2 comp = (MCIntVector2) obj;
        return Integer.compare(x, comp.x) == 0 && Integer.compare(y, comp.y) == 0;
    }

    @Override
    public int hashCode() {
        int res = Integer.hashCode(x);
        res = 31 * res + Integer.hashCode(y);
        return res;
    }

    public String toString() {
        return "(" + x + "," + y + ")";
    }

    /**
     * Give the result of subtracting the given vector from the vector.
     * @param v2
     * @return
     */
    public MCIntVector2 subTo(MCIntVector2 v2) {
        return new MCIntVector2(this.x - v2.x, this.y - v2.y);
    }

    /**
     * Give the result of adding the given vector from the vector.
     * @param v2
     * @return
     */
    public MCIntVector2 addTo(MCIntVector2 v2) {
        return new MCIntVector2(v2.x + this.x, v2.y + this.y);
    }

    /**
     * Returns the distance between the vector and the given one.
     * @param v2
     * @return
     */
    public float dst2(MCIntVector2 v2) {
        return ((v2.y - y) * (v2.y - y)) + ((v2.x - x) * (v2.x - x));
    }
}
