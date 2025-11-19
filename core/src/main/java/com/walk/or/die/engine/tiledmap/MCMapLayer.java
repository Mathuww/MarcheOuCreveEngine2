package com.walk.or.die.engine.tiledmap;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;

public class MCMapLayer {
    private MapLayer ml;

    public MCMapLayer(MapLayer ml) {
        this.ml = ml;
    }

    public MCMapObject getObjectByType(String name) {
        for (MapObject obj : this.ml.getObjects()) {
            Object type = obj.getProperties().get("type");
            if (name.equals(type)) {
                return new MCMapObject(obj);
            }
        }

        return null;
    }
}
