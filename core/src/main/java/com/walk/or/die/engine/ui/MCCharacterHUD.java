package com.walk.or.die.engine.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.shared.MCSharedAssets;
import com.walk.or.die.engine.ui.MCUILayout.Zone;

public class MCCharacterHUD extends MCAbstractHUD {
    private final String FONT_FAMILY = "ariBlackAlpha";

    private final float HUD_HEIGHT = MCGame.WINDOW_DEFAULT_HEIGHT * 0.18f;
    private final float BOTTOM_MARGIN = MCGame.WINDOW_DEFAULT_HEIGHT * 0.05f;
    private final float RECT_BORDER = MCGame.WINDOW_DEFAULT_HEIGHT * 0.015f;

    private final float INTERPANEL_PADDING_WIDTH = MCGame.WINDOW_DEFAULT_WIDTH * 0.05f;
    private final float MAIN_PADDING_HEIGHT = MCGame.WINDOW_DEFAULT_HEIGHT * 0.025f;

    private final float INSIDE_PADDING_WIDTH = 2f;
    private final float INSIDE_PADDING_HEIGHT = 2f;

    private final float CHARA_SPRITE_SIZE = 15f;
    private final float NAME_FONT_SCALE = 0.34f;
    private final float NAME_FONT_SPACING = 4f;

    private final float SCROLL_LERP = 300f;
    private final float SCROLL_Y = -(HUD_HEIGHT * 1.15f);

    private BitmapFont font;
    private MCCharacter characterAfterScroll;
    private MCCharacter currentCharacter;

    private MCUILayout layout = new MCUILayout();

    private Sprite characterSprite = new Sprite();
    private MCUIScrollingText characterNameText;
    private Rectangle characterNameZone;

    private float offsetY = SCROLL_Y;
    private float targetOffsetY = SCROLL_Y;
    private boolean scrolling = false;

    public MCCharacterHUD() {
        try {
            font = sharedAssets.getSavedFont(FONT_FAMILY);
        } catch (Exception e) {
            System.err.println("cant load character hud assets");
            e.printStackTrace();
        }
        
        layout.addZone(
            "characterHud", 
            new Rectangle(
                0f,
                BOTTOM_MARGIN,
                MCGame.WINDOW_DEFAULT_WIDTH,
                HUD_HEIGHT
            )
        ).pad(INTERPANEL_PADDING_WIDTH, MAIN_PADDING_HEIGHT);

        layout.splitX("characterHud", 0.35f, 30f, "infoPanel", "choicePanel");
        layout.zone("infoPanel").pad(INTERPANEL_PADDING_WIDTH, 0f);
        layout.splitX("infoPanel", 0.15f, 5f, "charaSprite", "charaInfos");
        layout.zone("charaSprite").pad(INSIDE_PADDING_WIDTH, INSIDE_PADDING_HEIGHT);
        layout.zone("charaInfos").pad(INSIDE_PADDING_WIDTH, INSIDE_PADDING_HEIGHT);

        float characterSpriteSize = layout.zone("charaSprite").size();
        characterSprite.setSize(characterSpriteSize, characterSpriteSize);
        characterNameText = new MCUIScrollingText(
            this, 
            font, 
            layout.zone("charaInfos"), 
            Color.BLACK, 
            NAME_FONT_SCALE, 
            NAME_FONT_SPACING
        );
    }

    public void setHudTarget(MCCharacter character) {
        if (character != null && this.currentCharacter == null) {
            currentCharacter = character;
            characterAfterScroll = character;
            scrolling = true;
            targetOffsetY = 0f;
        } else if (character == null && this.currentCharacter != null) {
            characterAfterScroll = character;
            scrolling = true;
            targetOffsetY = SCROLL_Y;
        } else {
            currentCharacter = character;
            characterAfterScroll = character;
        }

        if (currentCharacter != null)
            characterNameText.setText(currentCharacter.getDisplayName());
    }

    @Override
    public void update(float delta) {
        if (scrolling) {
            if (MathUtils.isEqual(targetOffsetY, 0f, 0.001f) 
                && offsetY >= targetOffsetY) {
                scrolling = false;
                currentCharacter = characterAfterScroll;
            } else if (MathUtils.isEqual(targetOffsetY, SCROLL_Y, 0.001f) 
                && offsetY <= targetOffsetY) {
                scrolling = false;
                currentCharacter = characterAfterScroll;
            }

            float diff = targetOffsetY - offsetY;
            float maxChange = SCROLL_LERP * delta;

            if (Math.abs(diff) <= maxChange) {
                offsetY = targetOffsetY;
            } else {
                offsetY += Math.signum(diff) * maxChange;
            }

            offsetY = MathUtils.clamp(offsetY, SCROLL_Y, 0f);
        }

        layout.zone("infoPanel").setOffset(0f, offsetY);

        if (currentCharacter != null) {
            //characterNameZone.x = infoPanelZone.x + RECT_BORDER + INSIDE_PADDING_WIDTH + CHARA_SPRITE_SIZE + INSIDE_PADDING_WIDTH;
            characterNameText.update(delta);

            characterSprite.setRegion(currentCharacter.getSprite());
            Vector2 spriteCenter = layout.zone("charaSprite").center(CHARA_SPRITE_SIZE, CHARA_SPRITE_SIZE);
            characterSprite.setPosition(
                spriteCenter.x,
                spriteCenter.y
            ); 
        }
    }

    /**
     * HUD :
     * 1/3 infos - 2/3 choix
     * Parties infos :
     * 1/3 sprite - 2/3 noms : HP
     */
    public void renderInfoPanel() {
        drawCornerlessRectangle(layout.zone("infoPanel"), RECT_BORDER);

        // partie droite : infos (pour l'instant que l'ID mdr)
        if (currentCharacter != null) {
            characterSprite.draw(currentBatch);
            //drawCornerlessRectangle(characterNameZone, RECT_BORDER);
            characterNameText.render(currentBatch);
        }      
    }

    @Override
    public void render(SpriteBatch batch) {
        currentBatch = batch;
        renderInfoPanel();
    }

    public void renderDebug() {
        layout.renderDebug();
    }

}
