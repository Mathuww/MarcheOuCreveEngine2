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
import com.walk.or.die.engine.shared.MCIntVector2;
import com.walk.or.die.engine.shared.MCSharedAssets;
import com.walk.or.die.engine.tiledmap.MCPathfinder;

// ne pas mettre de point d'attaque 

/**
 * A class that represents an attack.
 */
public class MCAttack {
    private String attackName;
    private final MCEntity parent;
    private int power; // A multiplier...
    private Map<MCIntVector2, Float> damagePattern;
    private Array<Sprite> displaySprites = new Array<>();
    private TextureRegion validTileTexture;
    private String projectileName = "projectile";
    public boolean display = false;
    private final Color VALID_COLOR = new Color(1f, 1f, 1f, 0.5f);
    private final Color TRAJ_COLOR = new Color(1f, 0f, 0f, 0.6f);

    private String senderAnim;

    /**
     * The creator.
     * @param parent
     * @param power
     * @param pattern
     * @throws Exception
     */
    public MCAttack(MCEntity parent, String name, int power, Map<MCIntVector2, Float> pattern) throws Exception {
        this.parent = parent;
        this.attackName = name;
        this.power = power;
        this.damagePattern = pattern;
        validTileTexture = MCSharedAssets.get().getSavedTexture("validAttackTile");
    }

    /**
     * Initialize parameters from tiled datas.
     * @param props
     */
    public void initFromProperties(MapProperties props) {
        this.senderAnim = props.get("senderAnim", String.class);
        this.projectileName = props.get("projectileName", String.class);
    }

    /**
     * Get if the tile is concerned by the attack.
     * @param targetPos
     * @return
     */
    public boolean isValidTile(MCIntVector2 targetPos) {
        MCIntVector2 relativePos = targetPos.subTo(parent.getTilePosition());
        return damagePattern.containsKey(relativePos);
    }
    
    /**
     * Get the damage to a specific tile.
     * @param targetPos
     * @return
     */
    private int getDamageAtTile(MCIntVector2 targetPos) {
        MCIntVector2 relativePos = targetPos.subTo(parent.getTilePosition());
        Float damage = damagePattern.get(relativePos);
        if (damage == null)
            return -1;
        else
            return MathUtils.round(damage * power);
    }

    /**
     * Get the damage to an entity.
     * @param targetEntity
     * @return
     */
    public int getDamageTo(MCEntity targetEntity) {
        return getDamageAtTile(targetEntity.getTilePosition());
    }

    /**
     * Compute all the tile to displaye.
     */
    public void computeValidTilesDisplay() {
        //System.out.println("updating attack");
        displaySprites.clear();
        MCIntVector2 parentTile = parent.getTilePosition();
        for (MCIntVector2 relativeTile : damagePattern.keySet()) {
            Float damage = damagePattern.get(relativeTile);
            if (damage != null && damage > 0f) {
                MCIntVector2 absoluteTile = new MCIntVector2(relativeTile.x, relativeTile.y).addTo(parentTile);
                if (parent.getMap().isWalkable(absoluteTile)) {
                    MCEntity e = MCEntityManager.get().getEntityFromTile(1, absoluteTile);
                    if (e != null && e instanceof MCAlly) // can't shoot an ally !
                        continue;
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

    /**
     * Clear the shown trajectories.
     */
    public void clearTrajectory() {
        for (Sprite spr : displaySprites)
            spr.setColor(VALID_COLOR);
    }

    /**
     * Show a trajectory.
     * @param traj
     */
    public void showTrajectory(List<MCIntVector2> traj) {
        for (Sprite spr : displaySprites)
            spr.setColor(VALID_COLOR);
        //System.out.println("New trajectory");
        for (Sprite spr : displaySprites) {
            boolean isInTrajectory = false;

            for (MCIntVector2 pos : traj) {
                if (new MCIntVector2(spr.getX(), spr.getY()).equals(pos)) {
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

    /**
     * Render (call each frame).
     * @param batch
     */
    public void render(SpriteBatch batch) {
        if (!display) return;
        for (Sprite spr : displaySprites)
            spr.draw(batch);
    }

    /**
     * Get the name of the anim to play on the sender.
     * @return
     */
    public String getSenderAnim() {
        return senderAnim;
    }

    /**
     * Create the right projectile.
     * @return
     * @throws Exception
     */
    public MCProjectile spawnProjectile() throws Exception {
        return MCEntityManager.get().buildProjectile(projectileName);
    }

    public String getSummary() {
        String summary = "Power : " + power;
        return summary;
    }

    public String getName() {
        return attackName;
    }
}
