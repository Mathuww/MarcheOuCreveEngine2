package com.walk.or.die.engine.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.shared.MCSharedAssets;
import com.walk.or.die.engine.ui.MCUILayout.Zone;

/**
 * The HP bar shown inside the character HUD's left panel. <br>
 * Very, very important to use lerp here to make it look aesthetic.
 */
public class MCHUDHPBar {
    /**
     * The HP bar outline width.
     */
    private final float CONTOUR_SIZE = 5f; // tile

    /**
     * The threshould at which HP become yellow.
     */
    private final float MID_HP_THRESHOLD = 0.66f;
    /**
     * The threshould at which HP become red.
     */
    private final float LOW_HP_THRESHOLD = 0.34f; // AHAHAHAAHAH

    /**
     * HP interpolation constant.
     */
    private final float LERP = 4f;
    /**
     * Used to fade out when the character dies.
     */
    private final float FADING_DURATION = 0.3f;

    private MCSharedAssets sharedAssets = MCSharedAssets.get();

    private TextureRegion fillTextureHighHP;
    private TextureRegion fillTextureMidHP;
    private TextureRegion fillTextureLowHP;
    private TextureRegion currentFillTexture;

    private MCAbstractHUD parent;
    private MCCharacter target;
    /**
     * @see MCUILayout.Zone
     */
    private Zone zone;

    /**
     * Stores the "visual" HP ratio, which is not equals to the "logical" one because of the interpolation.
     */
    private float lerpedHpRatio = 1f;

    /**
     * Constructs a new MCHUDHPBar.
     *
     * @param parent the Parent HUD.
     * @param zone the Zone where the HP bar is located.
     */
    public MCHUDHPBar(MCAbstractHUD parent, Zone zone) {
        this.parent = parent;
        this.zone = zone;

        try {
            fillTextureHighHP = sharedAssets.getSavedTexture("green");
            fillTextureMidHP = sharedAssets.getSavedTexture("yellow");
            fillTextureLowHP = sharedAssets.getSavedTexture("red");
        } catch (Exception e) {
            System.err.println("cant init health bar ui");
            e.printStackTrace();
        }
    }

    /**
     * Sets the target character for the HP bar.
     *
     * @param target the Target character.
     */
    public void setTarget(MCCharacter target) {
        this.target = target;
        float newHpRatio = MathUtils.clamp(
            (float) target.getHealth() / (float) target.getMaxHp(), 
            0f, 
            1f
        );
        lerpedHpRatio = newHpRatio;
    }

    /**
     * Called on each frame.
     * @param delta The time in seconds since the last frame.
     */
    public void update(float delta) {
        if (target == null)
            return;

        float newHpRatio = MathUtils.clamp(
            (float) target.getHealth() / (float) target.getMaxHp(), 
            0f, 
            1f
        );

        if (Math.abs(newHpRatio - lerpedHpRatio) > 0.001f)
            lerpedHpRatio += (newHpRatio - lerpedHpRatio) * delta * LERP; 
        else
            lerpedHpRatio = newHpRatio; // snap

        lerpedHpRatio = MathUtils.clamp(lerpedHpRatio, 0f, 1f);
        // j'ai mis ca parce que c'est de la logique pas du rendu
        if (lerpedHpRatio < MID_HP_THRESHOLD && lerpedHpRatio > LOW_HP_THRESHOLD)
            currentFillTexture = fillTextureMidHP;
        else if (lerpedHpRatio < LOW_HP_THRESHOLD)
            currentFillTexture = fillTextureLowHP;
        else 
            currentFillTexture = fillTextureHighHP;
    }

    /**
     * Gets the current lerped HP value.
     *
     * @return the Current lerped HP ratio.
     */
    public int getCurrentLerpedHp() {
        float hpRatio = MathUtils.clamp(lerpedHpRatio * target.getMaxHp(), 0f, target.getMaxHp());
        return MathUtils.floor(hpRatio);
    }

    /**
     * Called on each frame.
     * @param batch the Sprite batch being used to draw.
     */
    public void render(SpriteBatch batch) {
        if (target == null)
            return;

        Rectangle inside = zone.inside();
    
        // 1 : le fond (contourTexture)
        parent.drawCornerlessRectangle(zone, CONTOUR_SIZE);

        // 3 : les points de vie (fillTexture)
        batch.draw(
            currentFillTexture, 
            inside.x + CONTOUR_SIZE,       
            inside.y + CONTOUR_SIZE,      
            (inside.width - 2f * CONTOUR_SIZE) * lerpedHpRatio,
            inside.height - 2f * CONTOUR_SIZE
        );
        

        batch.setColor(1f, 1f, 1f, 1f);
    }
}