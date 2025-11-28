package com.walk.or.die.engine.entities;

import java.awt.Point;
import java.util.Map;
import java.util.List;

import org.w3c.dom.Text;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.walk.or.die.engine.shared.MCSharedAssets;
import com.walk.or.die.engine.tiledmap.MCPathfinder;

// ne pas mettre de point d'attaque 

public class MCAttack {
    private final MCEntity parent;
    private int power;
    private Map<Point, Float> damagePattern;
    private Array<Sprite> displaySprites = new Array<>();
    private TextureRegion validTileTexture;
    public boolean display = false;
    private final Color VALID_COLOR = new Color(1f, 1f, 1f, 0.5f);
    private final Color TRAJ_COLOR = new Color(1f, 0f, 0f, 0.6f);

    private String senderAnim;
    private String targetAnim;

    public MCAttack(MCEntity parent, int power, Map<Point, Float> pattern) throws Exception {
        this.parent = parent;
        this.power = power;
        this.damagePattern = pattern;
        validTileTexture = MCSharedAssets.get().getSavedTexture("validAttackTile");
    }

    public void initFromProperties(MapProperties props) {
        this.senderAnim = props.get("senderAnim", String.class);
        this.targetAnim = props.get("targetAnim", String.class);
    }

    public boolean isValidTile(Vector2 targetPos) {
        Vector2 relativePos = targetPos.cpy().sub(parent.getTilePosition());
        Point key = new Point(MathUtils.floor(relativePos.x), MathUtils.floor(relativePos.y));
        return damagePattern.containsKey(key);
    }
    
    private int getDamageAtTile(Vector2 targetPos) {
        Vector2 relativePos = targetPos.cpy().sub(parent.getTilePosition());
        Point key = new Point(MathUtils.floor(relativePos.x), MathUtils.floor(relativePos.y));
        Float damage = damagePattern.get(key);
        if (damage == null)
            return -1;
        else
            return MathUtils.round(damage * power);
    }

    public int getDamageTo(MCEntity targetEntity) {
        return getDamageAtTile(targetEntity.getTilePosition());
    }

    public void computeValidTilesDisplay() {
        System.out.println("updating attack");
        displaySprites.clear();
        Vector2 parentTile = parent.getTilePosition();
        for (Point relativeTile : damagePattern.keySet()) {
            Float damage = damagePattern.get(relativeTile);
            if (damage != null && damage > 0f) {
                Vector2 absoluteTile = new Vector2(relativeTile.x, relativeTile.y).add(parentTile.cpy());
                int absTileX = MathUtils.floor(absoluteTile.x);
                int absTileY = MathUtils.floor(absoluteTile.y);
                if (parent.getMap().isWalkable(absTileX, absTileY)) {
                    MCEntity e = MCEntityManager.get().getEntityFromTile(1, absoluteTile);
                    if (e != null && e instanceof MCAlly) // can't shoot an ally !
                        continue;
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

    public void clearTrajectory() {
        for (Sprite spr : displaySprites)
            spr.setColor(VALID_COLOR);
    }

    public void showTrajectory(List<Vector2> traj) {
        for (Sprite spr : displaySprites)
            spr.setColor(VALID_COLOR);
        //System.out.println("New trajectory");
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
