package com.walk.or.die.engine.entities;

import java.awt.Point;
import java.util.Map;

import org.w3c.dom.Text;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.walk.or.die.engine.shared.MCSharedAssets;

// ne pas mettre de point d'attaque 

public class MCAttack {
    private final MCEntity parent;
    private int power;
    private Map<Point, Float> damagePattern;
    private Array<Sprite> displaySprites = new Array<>();
    private TextureRegion validTileTexture;
    public boolean display = false;

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

    public void update() {
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
                    //System.out.println("adding attack display sprite at  : " + absTileX + ", " + absTileY);
                    Sprite spr = new Sprite(validTileTexture);
                    spr.setPosition(absTileX, absTileY);
                    spr.setSize(1f, 1f);
                    displaySprites.add(spr);
                }
            }
        }
    }

    public void render(SpriteBatch batch) {
        if (!display) return;
        for (Sprite spr : displaySprites)
            spr.draw(batch);
    }

    @Override 
    public String toString() {
        String s = "";
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                Vector2 v = new Vector2(i, j);
                Float d = damagePattern.get(v);
                s += "(" + v.x + "," + v.y + ") : " + d + "\n";
            }
        }
        return s;
    }
}
