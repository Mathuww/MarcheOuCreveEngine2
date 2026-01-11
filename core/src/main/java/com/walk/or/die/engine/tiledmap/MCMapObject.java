package com.walk.or.die.engine.tiledmap;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.shared.MCUtils;

/**
 * The class which gives an interface for MapObject from gdx.
 * @see MapObject
 */
public class MCMapObject {
    private MapObject mo;

    /**
     * The constructor.
     * @param mo The MapObject to wrap.
     */
    public MCMapObject(MapObject mo) {
        this.mo = mo;
    }

    /**
     * Gets the MapObject.
     * @return The raw MapObject.
     */
    public MapObject getRawObject() {
        return this.mo;
    }

    /**
     * Gets the position of the object.
     * @return The position of the object as a Vector2.
     */
    public Vector2 getPosition() {
        float x = MCUtils.getFloatProperty(this.mo.getProperties(), "x", 0f);
        float y = MCUtils.getFloatProperty(this.mo.getProperties(), "y", 0f);
        return new Vector2(x, y);
    }
}