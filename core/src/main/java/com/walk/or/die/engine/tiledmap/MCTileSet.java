package com.walk.or.die.engine.tiledmap;

import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;

/**
 * Represents a custom extension of {@link TiledMapTileSet}.
 * @see TiledMapTileSet
 */
public class MCTileSet extends TiledMapTileSet {
    /**
     * The internal {@link TiledMapTileSet} instance.
     */
    private TiledMapTileSet ts;

    /**
     * Constructs a new {@code MCTileSet}.
     * @param ts The {@link TiledMapTileSet} to wrap.
     */
    public MCTileSet(TiledMapTileSet ts) {
        this.ts = ts;
    }

    /**
     * Gets a tile based on its type.
     * @param type The type string.
     * @return The {@link TiledMapTile} that matches the given type.
     */
    public TiledMapTile getTileByType(String type) {
        return getTileByProperty("type", type);
    }
    
    /**
     * Retrieves a tile by a specific property.
     * @param property The property to search for.
     * @param value The value of the property.
     * @return The {@link TiledMapTile} with the given property and value, or {@code null} if not found.
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