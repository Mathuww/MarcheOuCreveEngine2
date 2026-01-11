package com.walk.or.die.engine.tiledmap;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;

/**
 * Our personal extend of TiledMapTileSet
 * @see TiledMapTileSet
 */
public class MCTileSet extends TiledMapTileSet {
    private TiledMapTileSet ts;

    /**
     * The constructor.
     * @param ts The TiledMapTileSet to wrap.
     */
    public MCTileSet(TiledMapTileSet ts) {
        this.ts = ts;
    }

    /**
     * Gets a tile from its type.
     * @param type The type string.
     * @return The TiledMapTile that matches the given type.
     */
    public TiledMapTile getTileByType(String type) {
        return getTileByProperty("type", type);
    }
    
    /**
     * Returns a tile with the given property.
     * @param property The property to search for.
     * @param value The value of the property.
     * @return The TiledMapTile with the given property and value.
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