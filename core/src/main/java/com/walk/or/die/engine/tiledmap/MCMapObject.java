package com.walk.or.die.engine.tiledmap;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.MCUtils;

public class MCMapObject {
    private MapObject mo;

    public MCMapObject(MapObject mo) {
        this.mo = mo;
    }

    public MapObject getRawObject() {
        return this.mo;
    }

    public Vector2 getPosition() {
        float x = MCUtils.getFloatProperty(this.mo.getProperties(), "x", 0f);
        float y = MCUtils.getFloatProperty(this.mo.getProperties(), "y", 0f);
        return new Vector2(x, y);
    }
}
