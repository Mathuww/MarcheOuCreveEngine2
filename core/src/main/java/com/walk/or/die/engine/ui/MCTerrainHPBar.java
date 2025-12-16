package com.walk.or.die.engine.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.walk.or.die.engine.cameras.MCCameraManager;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.entities.MCEntityManager;
import com.walk.or.die.engine.shared.MCIntVector2;
import com.walk.or.die.engine.shared.MCSharedAssets;

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
    private final float BAR_Y_OFFSET = 1.1f; // (tile) par rapport au bas de tile
    private final float BAR_Y_LOWER_OFFSET = 1f - BAR_HEIGHT - CONTOUR_SIZE;

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

    public MCTerrainHPBar(MCCharacter parent) {
        this.parent = parent;

        try {
            contourTexture = sharedAssets.getSavedTexture("black");
            backgroundTexture = sharedAssets.getSavedTexture("white");
            fillTextureHighHP = sharedAssets.getSavedTexture("green");
            fillTextureMidHP = sharedAssets.getSavedTexture("yellow");
            fillTextureLowHP = sharedAssets.getSavedTexture("red");
        } catch (Exception e) {
            System.err.println("cant init health bar ui");
            e.printStackTrace();
        }
    }

    public void update(float delta) {
        // pour ne pas faire new Vector2 à chaque frame !
        gdxParentPos.set(parent.getPosition());
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
