package com.walk.or.die.engine.shared;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * A singleton only used to debug.
 */
public class MCDebugRenderer {
    private static MCDebugRenderer instance = null;

    /**
     * Gets the singleton instance.
     * @return the singleton instance
     */
    public static MCDebugRenderer get() {
        if (instance == null) instance = new MCDebugRenderer();
        return instance;
    }

    private MCDebugRenderer() {}

    /**
     * Initializes the singleton and loads textures.
     * @throws Exception
     */
    public void init() throws Exception {
        validTileTexture = MCSharedAssets.get().getSavedTexture("validAttackTile");
    }

    private Set<MCIntVector2> debugTiles = new HashSet<>();
    private TextureRegion validTileTexture;

    /**
     * Highlights a tile with its position.
     * @param pos The position of the tile
     */
    public void addDebugTile(MCIntVector2 pos) {
        debugTiles.add(pos);
    }

    /**
     * Un-highlights a tile with its position.
     * @param pos The position of the tile
     */
    public void removeDebugTile(MCIntVector2 pos) {
        debugTiles.remove(pos);
    }

    /**
     * Renders the debug.
     * @param batch The sprite batch
     */
    public void render(SpriteBatch batch) {
        batch.setColor(0, 1, 0, 0.5f);
        for (MCIntVector2 pos : debugTiles) {
            batch.draw(validTileTexture, pos.x, pos.y, 1, 1);
        }
        batch.setColor(1, 1, 1, 1);
    }

}