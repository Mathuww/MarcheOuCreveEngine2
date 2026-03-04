package com.walk.or.die.engine.entities;

import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.walk.or.die.engine.shared.MCIntVector2;
import com.walk.or.die.engine.shared.MCSharedAssets;
import com.walk.or.die.engine.tiledmap.MCPathfinder;

/**
 * A class to display movements options.
 */
public class MCMoveDisplay {
    /** The parent character associated with this movement display. */
    private final MCCharacter parent;
    /** The shared assets instance for loading resources. */
    private final MCSharedAssets sharedAssets = MCSharedAssets.get();
    /** The array of sprites used to display valid movement tiles. */
    private Array<Sprite> displaySprites = new Array<>();
    /** The array of sprites used to display the current trajectory. */
    private Array<Sprite> trajectorySprites = new Array<>();
    /** The texture region for valid movement tiles. */
    private TextureRegion validTileTexture;
    /** The texture region for trajectory tiles. */
    private TextureRegion trajTexture;
    /** The color used for valid movement tiles. */
    private final Color VALID_COLOR = new Color(1f, 1f, 1f, 0.5f);
    /** The color used for trajectory tiles. */
    private final Color TRAJ_COLOR = new Color(1f, 1f, 1f, 0.8f);
    /** Indicates whether the movement display is currently active. */
    public boolean display = false;

    /** The animation identifier for the sender character. */
    private String senderAnim;
    /** The animation identifier for the target character. */
    private String targetAnim;

    /**
     * Initializes a new instance of the move display.
     * @param parent The parent MCCharacter.
     * @throws Exception If an error occurs during initialization.
     */
    public MCMoveDisplay(MCCharacter parent) throws Exception {
        this.parent = parent;
        validTileTexture = sharedAssets.getSavedTexture("validAttackTile");
        trajTexture = sharedAssets.getSavedTexture("trajectoryTile");
    }

    /**
     * Computes and updates the tiles to display valid movement options.
     */
    public void computeValidTilesDisplay() {
        //System.out.println("updating attack");
        displaySprites.clear();
        int maxMoves = parent.getMaxMoves();
        MCIntVector2 parentTile = parent.getTilePosition();
        for (int x = -maxMoves; x <= maxMoves; x++) {
            for (int y = -maxMoves ; y <= maxMoves; y++) {
                if (Math.abs(x) + Math.abs(y) <= maxMoves) {
                    MCIntVector2 relativeTile = new MCIntVector2(x, y);
                    MCIntVector2 absoluteTile = relativeTile.addTo(parentTile);
                    if (MCPathfinder.get().isWalkable(absoluteTile)
                        && MCPathfinder.get().getPath(parentTile, absoluteTile).size() <= maxMoves) {
                        //System.out.println("adding attack display sprite at  : " + absTileX + ", " + absTileY);
                        Sprite spr = new Sprite(validTileTexture);
                        spr.setPosition(absoluteTile.x, absoluteTile.y);
                        spr.setSize(1f, 1f);
                        spr.setColor(VALID_COLOR);
                        displaySprites.add(spr);
                    }
                }
            }
        }
    }

    /**
     * Clears all displayed trajectory sprites.
     */
    public void clearTrajectory() {
        trajectorySprites.clear();
    }

    /**
     * Displays a given trajectory.
     * @param traj The list of MCIntVector2 representing the trajectory.
     */
    public void showTrajectory(List<MCIntVector2> traj) {
        trajectorySprites.clear();
        for (int i = 0; i < traj.size(); i++) {
            if (i == 0)
                continue;
            MCIntVector2 pos = traj.get(i);
            Sprite spr = new Sprite(trajTexture);
            spr.setPosition(pos.x, pos.y);
            spr.setSize(1f, 1f);
            spr.setColor(TRAJ_COLOR);
            trajectorySprites.add(spr);
        }
    }

    /**
     * Renders the movement display elements.
     * @param batch The sprite batch used for rendering.
     */
    public void render(SpriteBatch batch) {
        if (!display) return;
        for (Sprite spr : displaySprites)
            spr.draw(batch);
        for (Sprite spr : trajectorySprites)
            spr.draw(batch);
    }
    
}