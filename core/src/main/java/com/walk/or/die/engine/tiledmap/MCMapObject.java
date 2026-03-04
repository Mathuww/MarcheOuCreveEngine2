package com.walk.or.die.engine.tiledmap;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.shared.MCUtils;

/**
 * Provides an interface for a MapObject from GDX.
 * @see MapObject
 */
public class MCMapObject {
    /** The wrapped MapObject. */
    private MapObject mo;

    /**
     * Constructs a new MCMapObject.
     * @param mo The {@code MapObject} to wrap.
     */
    public MCMapObject(MapObject mo) {
        this.mo = mo;
    }

    /**
     * Gets the raw {@code MapObject}.
     * @return The raw {@code MapObject}.
     */
    public MapObject getRawObject() {
        return this.mo;
    }

    /**
     * Gets the position of the object.
     * @return The position of the object as a {@code Vector2}.
     */
    public Vector2 getPosition() {
        float x = MCUtils.getFloatProperty(this.mo.getProperties(), "x", 0f);
        float y = MCUtils.getFloatProperty(this.mo.getProperties(), "y", 0f);
        return new Vector2(x, y);
    }
}