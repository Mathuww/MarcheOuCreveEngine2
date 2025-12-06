package com.walk.or.die.engine.shared;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * A singleton only used to debug.
 */
public class MCDebugRenderer {
    private static MCDebugRenderer instance = null;

    /**
     * Get the singleton instance.
     * @return
     */
    public static MCDebugRenderer get() {
        if (instance == null) instance = new MCDebugRenderer();
        return instance;
    }

    private MCDebugRenderer() {}

    /**
     * Init the singleton and load textures.
     * @throws Exception
     */
    public void init() throws Exception {
        validTileTexture = MCSharedAssets.get().getSavedTexture("validAttackTile");
    }

    private Set<MCIntVector2> debugTiles = new HashSet<>();
    private TextureRegion validTileTexture; 

    /**
     * Highlight a tile with his position.
     * @param pos
     */
    public void addDebugTile(MCIntVector2 pos) {
        debugTiles.add(pos);
    }

    /**
     * Un-highlight a tile with his position.
     * @param pos
     */
    public void removeDebugTile(MCIntVector2 pos) {
        debugTiles.remove(pos);
    }

    /**
     * Render the debug.
     * @param batch
     */
    public void render(SpriteBatch batch) {
        batch.setColor(0, 1, 0, 0.5f);
        for (MCIntVector2 pos : debugTiles) {
            batch.draw(validTileTexture, pos.x, pos.y, 1, 1);
        }
        batch.setColor(1, 1, 1, 1); 
    }

}
