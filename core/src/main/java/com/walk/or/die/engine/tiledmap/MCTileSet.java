package com.walk.or.die.engine.tiledmap;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;


public class MCTileSet extends TiledMapTileSet {
    private TiledMapTileSet ts;

    public MCTileSet(TiledMapTileSet ts) {
        this.ts = ts;
    }

    public TiledMapTile getTileByType(String type) {
        return getTileByProperty("type", type);
    }
    
    public TiledMapTile getTileByProperty(String property, String value) {
        for (TiledMapTile tile : this.ts) {
            Object prop = tile.getProperties().get(property);
            if (value.equals(prop)) {
                return tile;
            }
        }

        return null;
    }
}
