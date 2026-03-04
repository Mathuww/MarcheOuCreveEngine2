package com.walk.or.die.engine.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.walk.or.die.engine.cameras.MCCameraManager;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.shared.MCSharedAssets;

/**
 * Represents the HP bar displayed above each character on the terrain.
 * It is only displayed when the HP is below the maximum.
 */
public class MCTerrainHPBar extends MCAbstractHUD {
    /**
     * The size of the black contour around the bar (in tiles).
     */
    private final float CONTOUR_SIZE = 0.055f;
    /**
     * The height of the bar (in tiles).
     */
    private final float BAR_HEIGHT = 0.08f;
    /**
     * The width of the bar (in tiles).
     */
    private final float BAR_WIDTH = 0.85f;
    /**
     * The Y offset relative to the bottom of the tile.
     */
    private final float BAR_Y_OFFSET = 1.15f;
    /**
     * The Y offset used when the bar would otherwise be off-screen (top).
     */
    private final float BAR_Y_LOWER_OFFSET = 1f - BAR_HEIGHT - CONTOUR_SIZE;

    /**
     * The starting Y offset for the damage indicator.
     */
    private final float DAMAGE_BASE_Y_OFFSET = 1.7f;
    /**
     * The final Y offset for the damage indicator.
     */
    private final float DAMAGE_END_Y_OFFSET = 3f;
    /**
     * The duration of the damage floating animation.
     */
    private final float DAMAGE_DURATION = 3f;
    /**
     * The interpolation speed for the damage floating animation.
     */
    private final float DAMAGE_UP_LERP = 0.2f;
    /**
     * The height of the damage text area.
     */
    private final float DAMAGE_HEIGHT = 1f;
    /**
     * The font scale for the damage text.
     */
    private final float DAMAGE_FONT_SCALE = 0.0125f;
    /**
     * The offset for the damage text contour.
     */
    private final float DAMAGE_CONTOUR_OFFSET = 0.05f;
    /**
     * The font family used for floating numbers.
     */
    private final String FONT_FAMILY = "ariBlackAlphaFP"; // float positions

    /**
     * The threshold ratio below which the bar turns yellow.
     */
    private final float MID_HP_THRESHOLD = 0.66f;
    /**
     * The threshold ratio below which the bar turns red.
     */
    private final float LOW_HP_THRESHOLD = 0.34f;

    /**
     * The interpolation speed for the HP bar fill.
     */
    private final float LERP = 4f;
    /**
     * The duration of the fading out animation.
     */
    private final float FADING_DURATION = 0.3f;

    /**
     * The shared assets instance.
     */
    private MCSharedAssets sharedAssets = MCSharedAssets.get();
    /**
     * The camera manager, used to check bounds.
     */
    private MCCameraManager camManager = MCCameraManager.get();

    /**
     * The texture for the bar contour.
     */
    private TextureRegion contourTexture;
    /**
     * The texture for the bar background.
     */
    private TextureRegion backgroundTexture;
    /**
     * The texture for the high HP fill.
     */
    private TextureRegion fillTextureHighHP;
    /**
     * The texture for the mid HP fill.
     */
    private TextureRegion fillTextureMidHP;
    /**
     * The texture for the low HP fill.
     */
    private TextureRegion fillTextureLowHP;
    /**
     * The currently active fill texture (green, yellow or red).
     */
    private TextureRegion currentFillTexture;

    /**
     * The character this bar belongs to.
     */
    private MCCharacter parent;
    /**
     * The cached position of the parent character.
     */
    private Vector2 gdxParentPos = new Vector2(0f, 0f);

    /**
     * The calculated X position of the bar.
     */
    private float startX = 0f;
    /**
     * The calculated Y position of the bar.
     */
    private float startY = 0f;
    /**
     * The current interpolated health ratio (0 to 1).
     */
    private float lerpedHpRatio = 1f;

    /**
     * The current opacity of the bar.
     */
    private float alpha = 1f;
    /**
     * The time elapsed since the fading started.
     */
    private float fadeStateTime = 0f;

    /**
     * Indicates if the bar is currently fading out.
     */
    private boolean fading = false;
    /**
     * Indicates if the bar should be displayed.
     */
    private boolean display = true;

    /**
     * The UI layout manager.
     */
    private MCUILayout layout = new MCUILayout();
    /**
     * The font used for drawing text.
     */
    private BitmapFont font;

    /**
     * The string representation of the damage amount.
     */
    private String damageStr = "";
    /**
     * The layout for the damage text.
     */
    private GlyphLayout damageLayout;
    /**
     * The width of the damage text.
     */
    private float damageWidth;
    /**
     * The current opacity of the damage text.
     */
    private float damageAlpha = 0f;
    /**
     * The time elapsed since the damage display started.
     */
    private float damageFadeStateTime = 0f;
    /**
     * The current Y offset of the floating damage text.
     */
    private float damageOffsetY = 0f;
    /**
     * The starting X position for the damage text.
     */
    private float damageStartX = 0f;
    /**
     * The starting Y position for the damage text.
     */
    private float damageStartY = 0f;

    /**
     * Constructs a {@code MCTerrainHPBar}.
     * @param parent The parent MCCharacter.
     * @param vp The viewport instance.
     */
    public MCTerrainHPBar(MCCharacter parent, Viewport vp) {
        this.parent = parent;

        try {
            contourTexture = sharedAssets.getSavedTexture("black");
            backgroundTexture = sharedAssets.getSavedTexture("white");
            fillTextureHighHP = sharedAssets.getSavedTexture("green");
            fillTextureMidHP = sharedAssets.getSavedTexture("yellow");
            fillTextureLowHP = sharedAssets.getSavedTexture("red");
            font = sharedAssets.getSavedFont(FONT_FAMILY);
        } catch (Exception e) {
            System.err.println("cant init health bar ui");
            e.printStackTrace();
        }
    }

    /**
     * Triggers the display of a floating damage number.
     * @param damage The damage amount to show.
     */
    public void showDamage(int damage) {
        damageStr = Integer.toString(damage);
        damageAlpha = 1f;
        damageFadeStateTime = 0f;
        damageOffsetY = DAMAGE_BASE_Y_OFFSET;

        font.getData().setScale(DAMAGE_FONT_SCALE, DAMAGE_FONT_SCALE * 0.75f);
        damageLayout = new GlyphLayout(font, damageStr);
        damageWidth = damageLayout.width;

        updateDamageIndicator(0f);
    }

    /**
     * Updates the damage indicator animation.
     * @param delta The time elapsed since the last frame.
     */
    public void updateDamageIndicator(float delta) {
        damageFadeStateTime += delta;

        float timeRatio = damageFadeStateTime / DAMAGE_DURATION;
        if (timeRatio >= 1f) {
            damageAlpha = 0f;
            return;
        }

        damageAlpha = Interpolation.exp5In.apply(1f, 0f, timeRatio);
        //System.out.println("damage alpha = " + damageAlpha);

        if (Math.abs(DAMAGE_END_Y_OFFSET - damageOffsetY) > 0.001f)
            damageOffsetY += (DAMAGE_END_Y_OFFSET - damageOffsetY) * delta * DAMAGE_UP_LERP;
        else
            damageOffsetY = DAMAGE_END_Y_OFFSET;

        damageStartX = gdxParentPos.x + 0.5f - (damageWidth / 2f);
        damageStartY = gdxParentPos.y + damageOffsetY;
    }

    /**
     * Updates the bar logic. Called on each frame.
     * @param delta The time elapsed since the last frame.
     */
    public void update(float delta) {
        // pour ne pas faire new Vector2 à chaque frame !
        gdxParentPos.set(parent.getPosition());

        if (damageAlpha > 0f) {
            updateDamageIndicator(delta);
        }

        float newHpRatio = MathUtils.clamp(
                (float) parent.getHealth() / (float) parent.getMaxHp(),
                0f,
                1f
        );

        if (Math.abs(newHpRatio - lerpedHpRatio) > 0.001f)
            lerpedHpRatio += (newHpRatio - lerpedHpRatio) * delta * LERP;
        else
            lerpedHpRatio = newHpRatio; // snap

        lerpedHpRatio = MathUtils.clamp(lerpedHpRatio, 0f, 1f);

        startX = gdxParentPos.x + 0.5f - (BAR_WIDTH / 2f);
        startY = gdxParentPos.y + BAR_Y_OFFSET;
        if (startY > parent.getMap().getHeight())
            startY = gdxParentPos.y + BAR_Y_LOWER_OFFSET;
        // là on a startX et startY c'est bon

        if (fading) {
            fadeStateTime += delta;
            alpha = 1 - (fadeStateTime / FADING_DURATION);
            return;
        }

        if (lerpedHpRatio <= 0f)
            fading = true;

        // j'ai mis ca parce que c'est de la logique pas du rendu
        if (lerpedHpRatio < MID_HP_THRESHOLD && lerpedHpRatio > LOW_HP_THRESHOLD)
            currentFillTexture = fillTextureMidHP;
        else if (lerpedHpRatio < LOW_HP_THRESHOLD)
            currentFillTexture = fillTextureLowHP;
        else
            currentFillTexture = fillTextureHighHP;
    }

    /**
     * Renders the bar and damage indicator. Called on each frame.
     * @param batch The sprite batch used for drawing.
     */
    public void render(SpriteBatch batch) {
        if(!display)
            return;

        if (damageAlpha > 0f) {
            font.getData().setScale(DAMAGE_FONT_SCALE, DAMAGE_FONT_SCALE * 0.75f);

            Color c = Color.BLACK;
            font.setColor(c.r, c.g, c.b, damageAlpha);
            font.draw(batch, damageStr, damageStartX, damageStartY);
        }

        if (fading)
            batch.setColor(1f, 1f, 1f, alpha);

        // 1 : le fond (contourTexture)
        batch.draw(
                contourTexture,
                startX - CONTOUR_SIZE,
                startY - CONTOUR_SIZE,
                BAR_WIDTH + CONTOUR_SIZE * 2f,
                BAR_HEIGHT + CONTOUR_SIZE * 2f
        );

        // 2 : la barre vide (backgroundTexture)
        batch.draw(
                backgroundTexture,
                startX,
                startY,
                BAR_WIDTH,
                BAR_HEIGHT
        );

        // 3 : les points de vie (fillTexture)
        batch.draw(
                currentFillTexture,
                startX,
                startY,
                BAR_WIDTH * lerpedHpRatio,
                BAR_HEIGHT
        );

        batch.setColor(1f, 1f, 1f, 1f);
    }

    /**
     * Checks if the HP bar is fully visible (not fading).
     * @return True if the HP bar is fully shown, false otherwise.
     */
    public boolean isFullyShown() { return display && !fading; }

    /**
     * Checks if a screen position belongs to this HUD component.
     * @param pos The position to check.
     * @return Always false for the terrain HP bar.
     */
    public boolean posBelongsToHudComponent(Vector2 pos) { return false; }

    /**
     * Handles mouse hover events.
     * @param pos The position of the cursor.
     */
    public void handleHover(Vector2 pos) {}

    /**
     * Handles the event when the mouse stops hovering over the component.
     */
    public void handleHoverGone() {}

    /**
     * Handles mouse click events.
     * @param pos The position of the click.
     */
    public void handleClick(Vector2 pos) {}
}