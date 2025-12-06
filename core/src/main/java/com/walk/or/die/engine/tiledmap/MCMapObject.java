package com.walk.or.die.engine.tiledmap;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.shared.MCUtils;

/**
 * The class which gives us an interface for MapObject from gdx
 * @see MapObject
 */
public class MCMapObject {
    private MapObject mo;

    /**
     * The constructor
     * @param mo
     */
    public MCMapObject(MapObject mo) {
        this.mo = mo;
    }

    /**
     * Return the MapObject
     * @return
     */
    public MapObject getRawObject() {
        return this.mo;
    }

    /**
     * Get the position of the object
     * @return
     */
    public Vector2 getPosition() {
        float x = MCUtils.getFloatProperty(this.mo.getProperties(), "x", 0f);
        float y = MCUtils.getFloatProperty(this.mo.getProperties(), "y", 0f);
        return new Vector2(x, y);
    }
}
