package com.walk.or.die.engine.shared;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * A singleton only used to debug.
 */
public class MCDebugRenderer {
    /** The singleton instance of the renderer. */
    private static MCDebugRenderer instance = null;

    /**
     * Gets the singleton instance.
     * @return The singleton instance.
     */
    public static MCDebugRenderer get() {
        if (instance == null) instance = new MCDebugRenderer();
        return instance;
    }

    /** Constructs a new debug renderer instance. */
    private MCDebugRenderer() {}

    /**
     * Initializes the singleton and loads textures.
     * @throws Exception Throws an exception if texture loading fails.
     */
    public void init() throws Exception {
        validTileTexture = MCSharedAssets.get().getSavedTexture("validAttackTile");
    }

    /** A set of tile positions to be debug-highlighted. */
    private Set<MCIntVector2> debugTiles = new HashSet<>();
    /** The texture region used for valid debug tiles. */
    private TextureRegion validTileTexture;

    /**
     * Highlights a tile with its position.
     * @param pos The position of the tile.
     */
    public void addDebugTile(MCIntVector2 pos) {
        debugTiles.add(pos);
    }

    /**
     * Un-highlights a tile with its position.
     * @param pos The position of the tile.
     */
    public void removeDebugTile(MCIntVector2 pos) {
        debugTiles.remove(pos);
    }

    /**
     * Renders the debug.
     * @param batch The sprite batch.
     */
    public void render(SpriteBatch batch) {
        batch.setColor(0, 1, 0, 0.5f);
        for (MCIntVector2 pos : debugTiles) {
            batch.draw(validTileTexture, pos.x, pos.y, 1, 1);
        }
        batch.setColor(1, 1, 1, 1);
    }

}