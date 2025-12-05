package com.walk.or.die.engine.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.shared.MCSharedAssets;

public class MCCharacterHUD {
    private final String FONT_FAMILY = "Minecraft";

    private final float HUD_HEIGHT = MCGame.WINDOW_DEFAULT_HEIGHT * 0.18f;
    private final float BOTTOM_MARGIN = MCGame.WINDOW_DEFAULT_HEIGHT * 0.05f;
    private final float RECT_BORDER = MCGame.WINDOW_DEFAULT_HEIGHT * 0.015f;

    private final float INTERPANEL_PADDING_WIDTH = MCGame.WINDOW_DEFAULT_WIDTH * 0.05f;
    private final float EFFECTIVE_PANELS_WIDTH = MCGame.WINDOW_DEFAULT_WIDTH - INTERPANEL_PADDING_WIDTH * 3f;
    private final float INFO_PANEL_WIDTH = EFFECTIVE_PANELS_WIDTH * 0.6f;
    private final float CHOICE_PANEL_WIDTH = EFFECTIVE_PANELS_WIDTH * 0.6f;

    private final float INSIDE_PADDING_WIDTH = EFFECTIVE_PANELS_WIDTH * 0.05f;
    private final float INSIDE_PADDING_HEIGHT = HUD_HEIGHT * 0.05f;

    private final float EFFECTIVE_INFO_PANEL_WIDTH = INFO_PANEL_WIDTH - INSIDE_PADDING_WIDTH * 3f;
    private final float CHARA_SPRITE_SIZE = EFFECTIVE_INFO_PANEL_WIDTH * 0.16f;
    private final float NAME_FONT_SCALE = 1.25f;
    private final float NAME_WIDTH = EFFECTIVE_INFO_PANEL_WIDTH * 0.67f;
    private final float NAME_HEIGHT = HUD_HEIGHT - (RECT_BORDER  + INSIDE_PADDING_HEIGHT) * 2f;

    private final float SCROLL_LERP = 300f;
    private final float SCROLL_Y = -(HUD_HEIGHT * 1.15f);

    private final MCSharedAssets sharedAssets = MCSharedAssets.get();
    private TextureRegion blackTexture;
    private TextureRegion whiteTexture;
    private BitmapFont font;
    private MCCharacter characterAfterScroll;
    private MCCharacter currentCharacter;

    private Rectangle infoPanelZone;

    private Sprite characterSprite = new Sprite();
    private SpriteBatch currentBatch;

    private MCUIScrollingText characterNameText;
    private Rectangle characterNameZone;

    private float offsetY = 0f;
    private float targetOffsetY = 0f;
    private boolean scrolling = false;

    public MCCharacterHUD() {
        try {
            font = sharedAssets.getSavedFont(FONT_FAMILY);
            blackTexture = sharedAssets.getSavedTexture("black");
            whiteTexture = sharedAssets.getSavedTexture("white");
        } catch (Exception e) {
            System.err.println("cant load character hud assets");
            e.printStackTrace();
        }

        infoPanelZone = new Rectangle(
            INTERPANEL_PADDING_WIDTH, 
            offsetY + BOTTOM_MARGIN, 
            INFO_PANEL_WIDTH, 
            HUD_HEIGHT
        );

        characterNameZone = new Rectangle(
            infoPanelZone.x + RECT_BORDER + INSIDE_PADDING_WIDTH + CHARA_SPRITE_SIZE + INSIDE_PADDING_WIDTH, 
            infoPanelZone.y + RECT_BORDER + INSIDE_PADDING_HEIGHT, 
            NAME_WIDTH, 
            NAME_HEIGHT
        );
        characterNameText = new MCUIScrollingText(
            font, 
            characterNameZone, 
            Color.BLACK, 
            NAME_FONT_SCALE, 
            4f
        );

        characterSprite.setSize(CHARA_SPRITE_SIZE, CHARA_SPRITE_SIZE);
    }
    
    private void drawWhiteRectangle(Rectangle rect) {
        currentBatch.draw(whiteTexture, rect.x, rect.y, rect.width, rect.height);
    }

    private void drawCorner(float x, float y, float cornerSize) {
        float tiersCorner = cornerSize / 3f;
        currentBatch.draw(blackTexture, x + tiersCorner, y, tiersCorner, tiersCorner);
        currentBatch.draw(blackTexture, x, y + tiersCorner, tiersCorner, tiersCorner);
        currentBatch.draw(blackTexture, x + tiersCorner * 2f, y + tiersCorner, tiersCorner, tiersCorner);
        currentBatch.draw(blackTexture, x + tiersCorner, y + tiersCorner * 2f, tiersCorner, tiersCorner);
    }

    private void drawCornerlessRectangle(Rectangle rect, float borderSize) {
        drawWhiteRectangle(rect);

        float tiersBorder = borderSize / 3f;
        // bord inférieur
        currentBatch.draw(blackTexture, rect.x + borderSize, rect.y, rect.width - borderSize * 2f, tiersBorder);
        currentBatch.draw(blackTexture, rect.x + borderSize, rect.y + tiersBorder * 2f, rect.width - borderSize * 2f, tiersBorder);
        // bord supérieur
        currentBatch.draw(blackTexture, rect.x + borderSize, rect.y + rect.height - borderSize, rect.width - borderSize * 2f, tiersBorder);
        currentBatch.draw(blackTexture, rect.x + borderSize, rect.y + rect.height - tiersBorder, rect.width - borderSize * 2f, tiersBorder);
        // bord gauche
        currentBatch.draw(blackTexture, rect.x, rect.y + borderSize, tiersBorder, rect.height - borderSize * 2f);
        currentBatch.draw(blackTexture, rect.x + tiersBorder * 2f, rect.y + borderSize, tiersBorder, rect.height - borderSize * 2f);
        // bord droit
        currentBatch.draw(blackTexture, rect.x + rect.width - borderSize, rect.y + borderSize, tiersBorder, rect.height - borderSize * 2f);
        currentBatch.draw(blackTexture, rect.x + rect.width - tiersBorder, rect.y + borderSize, tiersBorder, rect.height - borderSize * 2f);

        drawCorner(rect.x, rect.y, borderSize);
        drawCorner(rect.x, rect.y + rect.height - borderSize, borderSize);
        drawCorner(rect.x + rect.width - borderSize, rect.y, borderSize);
        drawCorner(rect.x + rect.width - borderSize, rect.y + rect.height - borderSize, borderSize);
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
    }

    /**
     * HUD :
     * 1/3 infos - 2/3 choix
     * Parties infos :
     * 1/3 sprite - 2/3 noms : HP
     */
    public void renderInfoPanel() {
        drawCornerlessRectangle(infoPanelZone, RECT_BORDER);

        // partie gauche : sprite joueur
        float panelStartX = infoPanelZone.x + RECT_BORDER;
        float panelStartY = infoPanelZone.y + RECT_BORDER;

        if (currentCharacter != null) {
            characterSprite.draw(currentBatch);
            characterNameText.render(currentBatch);
        }

        // partie droite : infos (pour l'instant que l'ID mdr)
    }

    public void render(SpriteBatch batch) {
        currentBatch = batch;
        renderInfoPanel();
    }

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

        infoPanelZone.y = offsetY + BOTTOM_MARGIN;

        if (currentCharacter != null) {
            characterNameZone.x = infoPanelZone.x + RECT_BORDER + INSIDE_PADDING_WIDTH + CHARA_SPRITE_SIZE + INSIDE_PADDING_WIDTH;
            characterNameZone.y = infoPanelZone.y + RECT_BORDER + INSIDE_PADDING_HEIGHT;
            characterNameText.setText(currentCharacter.getDisplayName());
            characterNameText.update(delta);

            characterSprite.setRegion(currentCharacter.getSprite());
            characterSprite.setPosition(
                infoPanelZone.x + RECT_BORDER + INSIDE_PADDING_WIDTH + CHARA_SPRITE_SIZE / 2f, 
                infoPanelZone.y + RECT_BORDER + INSIDE_PADDING_HEIGHT + CHARA_SPRITE_SIZE / 2f
            );
        }
    }

}
