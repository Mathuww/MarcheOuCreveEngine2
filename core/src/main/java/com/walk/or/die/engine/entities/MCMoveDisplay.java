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
    private final MCCharacter parent;
    private final MCSharedAssets sharedAssets = MCSharedAssets.get();
    private Array<Sprite> displaySprites = new Array<>();
    private Array<Sprite> trajectorySprites = new Array<>();
    private TextureRegion validTileTexture;
    private TextureRegion trajTexture;
    private final Color VALID_COLOR = new Color(1f, 1f, 1f, 0.5f);
    private final Color TRAJ_COLOR = new Color(1f, 1f, 1f, 0.8f);
    public boolean display = false;

    private String senderAnim;
    private String targetAnim;

    /**
     * The constructor.
     * @param parent The parent MCCharacter.
     * @throws Exception If an error occurs during initialization.
     */
    public MCMoveDisplay(MCCharacter parent) throws Exception {
        this.parent = parent;
        validTileTexture = sharedAssets.getSavedTexture("validAttackTile");
        trajTexture = sharedAssets.getSavedTexture("trajectoryTile");
    }

    /**
     * Updates the tiles to show.
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
     * Erases trajectories.
     */
    public void clearTrajectory() {
        trajectorySprites.clear();
    }

    /**
     * Shows a trajectory.
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
     * Called on each frame
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