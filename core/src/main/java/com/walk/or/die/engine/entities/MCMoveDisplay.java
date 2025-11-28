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
import com.walk.or.die.engine.shared.MCSharedAssets;
import com.walk.or.die.engine.tiledmap.MCPathfinder;

public class MCMoveDisplay {
    private final MCCharacter parent;
    private Array<Sprite> displaySprites = new Array<>();
    private TextureRegion validTileTexture;
    private final Color VALID_COLOR = new Color(1f, 1f, 1f, 0.5f);
    private final Color TRAJ_COLOR = new Color(1f, 0f, 0f, 0.6f);
    public boolean display = false;

    private String senderAnim;
    private String targetAnim;

    public MCMoveDisplay(MCCharacter parent) throws Exception {
        this.parent = parent;
        validTileTexture = MCSharedAssets.get().getSavedTexture("validAttackTile");
    }

    public void computeValidTilesDisplay() {
        System.out.println("updating attack");
        displaySprites.clear();
        int maxMoves = parent.getMaxMoves();
        Vector2 parentTile = parent.getTilePosition();
        for (int x = -maxMoves; x <= maxMoves; x++) {
            for (int y = -maxMoves ; y <= maxMoves; y++) {
                if (Math.abs(x) + Math.abs(y) <= maxMoves) {
                    Vector2 relativeTile = new Vector2(x, y);
                    Vector2 absoluteTile = new Vector2(relativeTile.x, relativeTile.y).add(parentTile.cpy());
                    int absTileX = MathUtils.floor(absoluteTile.x);
                    int absTileY = MathUtils.floor(absoluteTile.y);
                    if (MCPathfinder.get().isWalkable(absTileX, absTileY)
                        && MCPathfinder.get().getPath(parentTile, absoluteTile).size() <= maxMoves) {
                        //System.out.println("adding attack display sprite at  : " + absTileX + ", " + absTileY);
                        Sprite spr = new Sprite(validTileTexture);
                        spr.setPosition(absTileX, absTileY);
                        spr.setSize(1f, 1f);
                        spr.setColor(VALID_COLOR);
                        displaySprites.add(spr);
                    }
                }
            }
        }
    }

    public void clearTrajectory() {
        for (Sprite spr : displaySprites)
            spr.setColor(VALID_COLOR);
    }

    public void showTrajectory(List<Vector2> traj) {
        //System.out.println("New trajectory");
        for (Sprite spr : displaySprites) {
            spr.setColor(VALID_COLOR);
        }
        for (Sprite spr : displaySprites) {
            boolean isInTrajectory = false;

            for (Vector2 pos : traj) {
                if (MathUtils.floor(spr.getX()) == MathUtils.floor(pos.x)
                    && MathUtils.floor(spr.getY()) == MathUtils.floor(pos.y)) {
                    isInTrajectory = true;
                    break;
                }
            }

            if (isInTrajectory) {
                // System.out.println("(" + spr.posX + "," + spr.posY + ")");
                spr.setColor(TRAJ_COLOR);
            } 
        }
    }

    public void render(SpriteBatch batch) {
        if (!display) return;
        for (Sprite spr : displaySprites)
            spr.draw(batch);
    }
    
}
