package com.walk.or.die.engine.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
    private final float CHARA_SPRITE_SIZE = EFFECTIVE_INFO_PANEL_WIDTH * 0.33f;
    private final float NAME_WIDTH = EFFECTIVE_INFO_PANEL_WIDTH * 0.67f;
    private final float NAME_HEIGHT = HUD_HEIGHT - (RECT_BORDER  + INSIDE_PADDING_HEIGHT) * 2f;

    private final MCSharedAssets sharedAssets = MCSharedAssets.get();
    private TextureRegion blackTexture;
    private TextureRegion whiteTexture;
    private BitmapFont font;
    private MCCharacter character;

    private Sprite characterSprite = new Sprite();
    private SpriteBatch currentBatch;

    public MCCharacterHUD() {
        try {
            font = sharedAssets.getSavedFont(FONT_FAMILY);
            blackTexture = sharedAssets.getSavedTexture("black");
            whiteTexture = sharedAssets.getSavedTexture("white");
        } catch (Exception e) {
            System.err.println("cant load character hud assets");
            e.printStackTrace();
        }
    }

    private void drawSpacedText(String text, float x, float y, float spacing) {
        float cursor = x;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            GlyphLayout layout = new GlyphLayout(font, String.valueOf(c));
            font.draw(currentBatch, String.valueOf(c), cursor, y);
            cursor += layout.width + spacing;
        }
    }

    private void drawCenteredText(String text, Rectangle drawingZone, Color color, float scale, float spacing) {
        font.setColor(color);
        font.getData().setScale(scale);

        GlyphLayout layout = new GlyphLayout(font, text);

        float realWidth = layout.width + spacing * (text.length() - 1);
        float x = drawingZone.x + (drawingZone.width - realWidth) / 2f;
        float y = drawingZone.y + (drawingZone.height + layout.height) / 2f;

        drawSpacedText(text, x, y, spacing);
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
        this.character = character;
    }

    /**
     * HUD :
     * 1/3 infos - 2/3 choix
     * Parties infos :
     * 1/3 sprite - 2/3 noms : HP
     */
    public void renderInfoPanel() {
        Rectangle panel = new Rectangle(INTERPANEL_PADDING_WIDTH, BOTTOM_MARGIN, INFO_PANEL_WIDTH, HUD_HEIGHT);
        drawCornerlessRectangle(panel, RECT_BORDER);

        // partie gauche : sprite joueur
        float panelStartX = INTERPANEL_PADDING_WIDTH + RECT_BORDER;
        float panelStartY = BOTTOM_MARGIN + RECT_BORDER;

        TextureRegion characterTexture = new TextureRegion(character.getSprite().getTexture());
        characterSprite.setRegion(characterTexture);
        characterSprite.setPosition(panelStartX + INSIDE_PADDING_WIDTH, panelStartY + INSIDE_PADDING_HEIGHT);
        characterSprite.setSize(CHARA_SPRITE_SIZE, CHARA_SPRITE_SIZE);
        characterSprite.draw(currentBatch);

        // partie droite : infos (pour l'instant que l'ID mdr)
        Rectangle idRect = new Rectangle(
            panelStartX + INSIDE_PADDING_WIDTH + CHARA_SPRITE_SIZE + INSIDE_PADDING_WIDTH, 
            panelStartY + INSIDE_PADDING_HEIGHT, 
            NAME_WIDTH, 
            NAME_HEIGHT
        );
        drawCenteredText(character.getId(), idRect, Color.BLACK, 1f, 4f);
    }

    public void render(SpriteBatch batch) {
        if (character == null)
            return;
        currentBatch = batch;
        renderInfoPanel();
    }
}
