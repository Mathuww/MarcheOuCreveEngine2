package com.walk.or.die.engine.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.walk.or.die.engine.cameras.MCCameraManager;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.entities.MCEntityManager;
import com.walk.or.die.engine.shared.MCIntVector2;
import com.walk.or.die.engine.shared.MCSharedAssets;
import com.walk.or.die.engine.ui.MCUILayout.Zone;

/* 
si on appelle le render de cette classe,
alors on considere que la vérification "pas de voisins qui ont une 
barre HP qui se chevauche à la mienne" 
a déjà été faite. (dans MCCharacter.renderOnGridOverlay)

du coup ici on se concentre juste sur le fait d'adapter l'affichage aux limites de la caméra (X et Y)
*/

public class MCTerrainHPBar extends MCAbstractHUD {
    private final float CONTOUR_SIZE = 0.055f; // tile
    private final float BAR_HEIGHT = 0.08f; // tile
    private final float BAR_WIDTH = 0.85f; // tile
    private final float BAR_Y_OFFSET = 1.15f; // (tile) par rapport au bas de tile
    private final float BAR_Y_LOWER_OFFSET = 1f - BAR_HEIGHT - CONTOUR_SIZE;

    private final float DAMAGE_BASE_Y_OFFSET = 1.7f;
    private final float DAMAGE_END_Y_OFFSET = 3f;
    private final float DAMAGE_DURATION = 3f; 
    private final float DAMAGE_UP_LERP = 0.2f;
    private final float DAMAGE_HEIGHT = 1f;
    private final float DAMAGE_FONT_SCALE = 0.0125f;
    private final float DAMAGE_CONTOUR_OFFSET = 0.05f;
    private final String FONT_FAMILY = "ariBlackAlphaFP"; // float positions

    private final float MID_HP_THRESHOLD = 0.66f;
    private final float LOW_HP_THRESHOLD = 0.34f; // AHAHAHAAHAH

    private final float LERP = 4f;
    private final float FADING_DURATION = 0.3f;

    private MCSharedAssets sharedAssets = MCSharedAssets.get();
    // pour pas que le barre sorte de la zone visible
    private MCCameraManager camManager = MCCameraManager.get();

    private TextureRegion contourTexture;
    private TextureRegion backgroundTexture;
    private TextureRegion fillTextureHighHP;
    private TextureRegion fillTextureMidHP;
    private TextureRegion fillTextureLowHP;
    private TextureRegion currentFillTexture;

    private MCCharacter parent;
    private Vector2 gdxParentPos = new Vector2(0f, 0f);

    private float startX = 0f;
    private float startY = 0f;
    private float lerpedHpRatio = 1f;

    private float alpha = 1f;
    private float fadeStateTime = 0f;

    private boolean fading = false;
    private boolean display = true;

    private MCUILayout layout = new MCUILayout();
    private BitmapFont font;
    private String damageStr = "";
    private GlyphLayout damageLayout;
    private float damageWidth;
    private float damageAlpha = 0f;
    private float damageFadeStateTime = 0f;
    private float damageOffsetY = 0f;
    private float damageStartX = 0f;
    private float damageStartY = 0f;

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

    public void updateDamageIndicator(float delta) {
        damageFadeStateTime += delta;
        
        float timeRatio = damageFadeStateTime / DAMAGE_DURATION;
        if (timeRatio >= 1f) {
            damageAlpha = 0f;
            return;
        }

        damageAlpha = Interpolation.exp5In.apply(1f, 0f, timeRatio);
        System.out.println("damage alpha = " + damageAlpha);

        if (Math.abs(DAMAGE_END_Y_OFFSET - damageOffsetY) > 0.001f)
            damageOffsetY += (DAMAGE_END_Y_OFFSET - damageOffsetY) * delta * DAMAGE_UP_LERP;
        else
            damageOffsetY = DAMAGE_END_Y_OFFSET;

        damageStartX = gdxParentPos.x + 0.5f - (damageWidth / 2f);
        damageStartY = gdxParentPos.y + damageOffsetY;
    }

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

    public boolean isFullyShown() { return display && !fading; }
    public boolean posBelongsToHudComponent(Vector2 pos) { return false; }
    public void handleHover(Vector2 pos) {}
    public void handleHoverGone() {}
    public void handleClick(Vector2 pos) {}
}
