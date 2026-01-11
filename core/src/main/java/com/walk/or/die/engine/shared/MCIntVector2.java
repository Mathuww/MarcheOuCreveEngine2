package com.walk.or.die.engine.shared;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Our personal Vector2, which uses int instead of float.
 * @see Vector2
 */
public class MCIntVector2 {
    public int x;
    public int y;

    /**
     * Constructs a new MCIntVector2 from int.
     * @param x The x component.
     * @param y The y component.
     */
    public MCIntVector2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructs a new MCIntVector2 from float.
     * @param x The x component.
     * @param y The y component.
     */
    public MCIntVector2(float x, float y) {
        this.x = MathUtils.floor(x);
        this.y = MathUtils.floor(y);
    }

    /**
     * Constructs a new MCIntVector2 from Vector2 from libGdx.
     * @param v The Vector2 to copy.
     * @see Vector2
     */
    public MCIntVector2(Vector2 v) {
        this.x = MathUtils.floor(v.x);
        this.y = MathUtils.floor(v.y);
    }

    /**
     * Converts to Vector2 from libGdx.
     * @return The Vector2 representation.
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
     * Gets the result of subtracting the given vector from this vector.
     * @param v2 The vector to subtract.
     * @return A new MCIntVector2 representing the subtraction.
     */
    public MCIntVector2 subTo(MCIntVector2 v2) {
        return new MCIntVector2(this.x - v2.x, this.y - v2.y);
    }

    /**
     * Gets the result of adding the given vector to this vector.
     * @param v2 The vector to add.
     * @return A new MCIntVector2 representing the addition.
     */
    public MCIntVector2 addTo(MCIntVector2 v2) {
        return new MCIntVector2(v2.x + this.x, v2.y + this.y);
    }

    /**
     * Returns the squared distance between this vector and the given one.
     * @param v2 The other vector.
     * @return The squared distance between the two vectors.
     */
    public float dst2(MCIntVector2 v2) {
        return ((v2.y - y) * (v2.y - y)) + ((v2.x - x) * (v2.x - x));
    }
}