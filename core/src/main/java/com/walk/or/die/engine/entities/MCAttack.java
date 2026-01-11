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
    private final MCSharedAssets sharedAssets = MCSharedAssets.get();
    private int basePower;
    private int currentPower;
    private Map<MCIntVector2, Float> damagePattern;
    private Array<Sprite> displaySprites = new Array<>();
    private Array<Sprite> trajectorySprites = new Array<>();
    private TextureRegion validTileTexture;
    private TextureRegion trajTexture;
    private String projectileName = "projectile";
    public boolean display = false;
    private final Color VALID_COLOR = new Color(1f, 1f, 1f, 0.5f);
    private final Color TRAJ_COLOR = new Color(1f, 1f, 1f, 0.8f);

    private String senderAnim;

    /**
     * The creator.
     * @param parent The parent entity.
     * @param name The name of the attack.
     * @param power The base power of the attack.
     * @param pattern The damage pattern of the attack.
     * @throws Exception
     */
    
    public MCAttack(MCEntity parent, String name, int power, Map<MCIntVector2, Float> pattern) throws Exception {
        this.parent = parent;
        this.attackName = name;
        this.basePower = power;
        this.currentPower = power;
        this.damagePattern = pattern;
        validTileTexture = sharedAssets.getSavedTexture("validAttackTile");
        trajTexture = sharedAssets.getSavedTexture("trajectoryTile");
    }

    /**
     * Initializes parameters from tiled datas.
     * @param props The map properties.
     */
    public void initFromProperties(MapProperties props) {
        this.senderAnim = props.get("senderAnim", String.class);
        this.projectileName = props.get("projectileName", String.class);
    }

    /**
     * Gets if the tile is concerned by the attack.
     * @param targetPos The target position.
     * @return True if the tile is valid, false otherwise.
     */
    public boolean isValidTile(MCIntVector2 targetPos) {
        MCIntVector2 relativePos = targetPos.subTo(parent.getTilePosition());
        return damagePattern.containsKey(relativePos);
    }
    
    /**
     * Gets the damage to a specific tile.
     * @param targetPos The target position.
     * @return The damage value.
     */
    private int getDamageAtTile(MCIntVector2 targetPos) {
        MCIntVector2 relativePos = targetPos.subTo(parent.getTilePosition());
        Float damage = damagePattern.get(relativePos);
        if (damage == null)
            return -1;
        else
            return MathUtils.round(damage * currentPower);
    }

    /**
     * Gets the damage to an entity.
     * @param targetEntity The target entity.
     * @return The damage value.
     */
    public int getDamageTo(MCEntity targetEntity) {
        return getDamageAtTile(targetEntity.getTilePosition());
    }

    /**
     * Computes all the tile to display.
     */
    public void computeValidTilesDisplay() {
        //System.out.println("updating attack");
        displaySprites.clear();
        MCIntVector2 parentTile = parent.getTilePosition();
        for (MCIntVector2 relativeTile : damagePattern.keySet()) {
            MCIntVector2 absoluteTile = new MCIntVector2(relativeTile.x, relativeTile.y).addTo(parentTile);
            Float damage = damagePattern.get(relativeTile);
            if (damage != null && damage > 0f) {
                if (parent.getMap().isWalkable(absoluteTile)) {
                    MCEntity e = MCEntityManager.get().getEntityFromTile(1, absoluteTile);
                    if (e != null && e instanceof MCAlly) // can't shoot an ally !
                        continue;
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
     * Clears the shown trajectories.
     */
    public void clearTrajectory() {
        trajectorySprites.clear();
    }

    /**
     * Shows a trajectory.
     * @param traj The list of tile positions that make up the trajectory.
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
     * Called on each frame.
     * @param batch The sprite batch.
     */
    public void render(SpriteBatch batch) {
        if (!display) return;
        for (Sprite spr : displaySprites)
            spr.draw(batch);
        for (Sprite spr : trajectorySprites)
            spr.draw(batch);
    }

    /**
     * Gets the name of the anim to play on the sender.
     * @return The sender animation name.
     */
    public String getSenderAnim() {
        return senderAnim;
    }

    /**
     * Creates the right projectile.
     * @return The projectile spawned.
     * @throws Exception
     */
    public MCProjectile spawnProjectile() throws Exception {
        return MCEntityManager.get().buildProjectile(projectileName);
    }

    /**
     * Gets the base power.
     * @return The base power of the attack.
     */
    public int getBasePower() {
        return basePower;
    }

    /**
     * Sets the current power.
     * @param newPower The new power value.
     */
    public void setPower(int newPower) {
        currentPower = newPower;
    }

    /**
     * Gets the current power.
     * @return The current power of the attack.
     */
    public int getPower() {
        return currentPower;
    }
    
    /**
     * Gets a summary of the attack.
     * @return The summary string.
     */
    public String getSummary() {
        String summary = "Power : " + currentPower;
        return summary;
    }

    /**
     * Gets the name of the attack.
     * @return The attack name.
     */
    public String getName() {
        return attackName;
    }
}