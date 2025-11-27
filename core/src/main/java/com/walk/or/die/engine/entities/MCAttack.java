package com.walk.or.die.engine.entities;

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
    private Map<Vector2, Float> damagePattern;
    private Array<Sprite> displaySprites = new Array<>();
    private TextureRegion validTileTexture;

    private String senderAnim;
    private String targetAnim;

    public MCAttack(MCEntity parent, int power, Map<Vector2, Float> pattern) throws Exception {
        this.parent = parent;
        this.power = power;
        this.damagePattern = pattern;
        //validTileTexture = MCSharedAssets.get().getSavedTexture("validAttackTile");
    }

    public void initFromProperties(MapProperties props) {
        this.senderAnim = props.get("senderAnim", String.class);
        this.targetAnim = props.get("targetAnim", String.class);
    }

    public boolean isValidTile(Vector2 targetPos) {
        return damagePattern.containsKey(targetPos.cpy().sub(parent.getTilePosition()));
    }
    
    private int getDamageAtTile(Vector2 targetPos) {
        Vector2 relativeDist = targetPos.cpy().sub(parent.getTilePosition());
        Float damage = damagePattern.get(relativeDist);
        if (damage == null)
            return -1;
        else
            return MathUtils.round(damage * power);
    }

    public int getDamageTo(MCEntity targetEntity) {
        return getDamageAtTile(targetEntity.getTilePosition());
    }

    public void hide() {
        displaySprites.clear();
    }

    public void update() {
        displaySprites.clear();
        Vector2 parentTile = parent.getTilePosition();
        for (Vector2 relativeTile : damagePattern.keySet()) {
            Float damage = damagePattern.get(relativeTile);
            if (damage != null && damage > 0f) {
                Vector2 absoluteTile = relativeTile.add(parentTile.cpy());
                int absTileX = MathUtils.floor(absoluteTile.x);
                int absTileY = MathUtils.floor(absoluteTile.y);
                if (parent.getMap().isWalkable(absTileX, absTileY)) {
                    Sprite spr = new Sprite(validTileTexture);
                    spr.setPosition(absTileX, absTileY);
                    displaySprites.add(spr);
                }
            }
        }
    }

    public void render(SpriteBatch batch) {
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
