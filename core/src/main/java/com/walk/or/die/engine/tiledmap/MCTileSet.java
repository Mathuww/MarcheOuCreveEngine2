package com.walk.or.die.engine.tiledmap;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;

/**
 * An personal extend of TiledMapTileSet
 * @see TiledMapTileSet
 */
public class MCTileSet extends TiledMapTileSet {
    private TiledMapTileSet ts;

    /**
     * The constructor
     * @param ts
     */
    public MCTileSet(TiledMapTileSet ts) {
        this.ts = ts;
    }

    /**
     * Get a a tile from his type
     * @param type string
     * @return
     */
    public TiledMapTile getTileByType(String type) {
        return getTileByProperty("type", type);
    }
    
    /**
     * Return a tile with the given property
     * @param property
     * @param value
     * @return
     */
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
