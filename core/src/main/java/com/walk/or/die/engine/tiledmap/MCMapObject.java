package com.walk.or.die.engine.tiledmap;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;

public class MCMapObject {
    private MapObject mo;

    public MCMapObject(MapObject mo) {
        this.mo = mo;
    }

    public Vector2 getPosition() {
        float x = (Float)this.mo.getProperties().get("x");
        float y = (Float)this.mo.getProperties().get("y");
        return new Vector2(x, y);
    }
}
