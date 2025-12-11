package com.walk.or.die.engine.ui;

import java.util.HashMap;
import java.util.Map;

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
import com.walk.or.die.engine.entities.MCAlly;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.shared.MCEventBus;
import com.walk.or.die.engine.shared.MCSharedAssets;
import com.walk.or.die.engine.ui.MCUILayout.Zone;

public class MCCharacterHUD extends MCAbstractHUD {
    private final String FONT_FAMILY = "ariBlackAlpha";

    private final float HUD_HEIGHT = MCGame.WINDOW_DEFAULT_HEIGHT * 0.25f;
    private final float HUD_BOTTOM_MARGIN = MCGame.WINDOW_DEFAULT_HEIGHT * 0.05f;
    private final float HUD_PADDING_HEIGHT  = MCGame.WINDOW_DEFAULT_HEIGHT * 0.025f;
    private final float HUD_RECT_BORDER = MCGame.WINDOW_DEFAULT_HEIGHT * 0.015f;
    private final float HUD_INTERPANEL_GAP = MCGame.WINDOW_DEFAULT_WIDTH * 0.05f;

    private final float HUD_LEFT_PANEL_RATIO = 0.35f;

    private final float LEFT_PANEL_PADDING_WIDTH = HUD_RECT_BORDER * 2f;
    private final float LEFT_PANEL_PADDING_HEIGHT = HUD_RECT_BORDER * 2f;
    private final float CHARA_SPRITE_RATIO = 0.15f;
    private final float LEFT_PANEL_GAP = 5f;
    private final float NAME_FONT_SCALE = 0.34f;
    private final float NAME_FONT_SPACING = 4f;

    private final float RIGHT_PANEL_PADDING_WIDTH = HUD_RECT_BORDER * 2f;
    private final float RIGHT_PANEL_PADDING_HEIGHT = HUD_RECT_BORDER * 2f;
    private final float CHOICE_FONT_SCALE = 0.35f;
    private final float CHOICE_FONT_SPACING = 3f;

    private final float SCROLL_LERP = 500f;
    private final float SCROLL_Y = -(HUD_HEIGHT * 1.15f);

    private BitmapFont font;
    private MCCharacter characterAfterScroll;
    private MCCharacter currentCharacter;

    private MCUILayout layout = new MCUILayout();

    private Sprite characterSprite = new Sprite();
    private MCUIScrollingText characterNameText;
    private MCUISimpleText characterHpText;

    private MCUITypingText choiceMessageText;
    private MCUICarousel choiceCarousel;

    private float offsetY = SCROLL_Y;
    private float targetOffsetY = SCROLL_Y;
    private boolean scrolling = false;

    private final MCEventBus bus = MCEventBus.get();

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
                HUD_BOTTOM_MARGIN,
                MCGame.WINDOW_DEFAULT_WIDTH,
                HUD_HEIGHT
            )
        ).pad(HUD_INTERPANEL_GAP, HUD_PADDING_HEIGHT);

        layout.splitX("characterHud", HUD_LEFT_PANEL_RATIO, HUD_INTERPANEL_GAP, "infoPanel", "choicePanel");
        
        layout.zone("infoPanel").pad(LEFT_PANEL_PADDING_WIDTH, LEFT_PANEL_PADDING_HEIGHT);
        layout.splitX("infoPanel", CHARA_SPRITE_RATIO, LEFT_PANEL_GAP, "charaSprite", "charaInfos");
        layout.splitY("charaInfos", 0.5f, 5f, "charaName", "charaHp");

        layout.zone("choicePanel").pad(RIGHT_PANEL_PADDING_WIDTH, RIGHT_PANEL_PADDING_HEIGHT);
        layout.splitY("choicePanel", 0.4f, 5f, "choiceMessage", "choiceCarousel");

        float characterSpriteSize = layout.zone("charaSprite").size();
        characterSprite.setSize(characterSpriteSize, characterSpriteSize);
        characterNameText = new MCUIScrollingText(
            this, 
            font, 
            layout.zone("charaName"), 
            Color.BLACK, 
            NAME_FONT_SCALE, 
            NAME_FONT_SPACING
        );
        characterHpText = new MCUISimpleText(
            this, 
            font, 
            layout.zone("charaHp"), 
            Color.BLACK, 
            NAME_FONT_SCALE, 
            NAME_FONT_SPACING
        );

        choiceMessageText = new MCUITypingText(
            this,
            font,
            layout.zone("choiceMessage"),
            Color.BLACK,
            CHOICE_FONT_SCALE,
            CHOICE_FONT_SPACING
        );

        choiceCarousel = new MCUICarousel(
            this,
            font,
            layout.zone("choiceCarousel")
        );

        bus.on(this, "InputPressed", this::inputPressed);
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

        if (currentCharacter != null) {
            characterNameText.setText(currentCharacter.getDisplayName());
            choiceMessageText.setText("What should I do ?");
            choiceMessageText.startTyping();

            if (currentCharacter instanceof MCAlly) {
                Map<String, Runnable> carouselActions = new HashMap<>();
                carouselActions.put("MOVE", () -> bus.emit("InputPressed", new MCInputManager.ReadyCommand()));
                carouselActions.put("ATTACK", () -> bus.emit("InputPressed", new MCInputManager.AimCommand()));
                carouselActions.put("FINISH TURN", () -> bus.emit("InputPressed", new MCInputManager.NextTurnCommand()));
                carouselActions.put("DIE", () -> currentCharacter.getHurt(currentCharacter.getMaxHp(), "hurt"));
                choiceCarousel.loadActions(carouselActions);
                choiceCarousel.appear();
            }
        }
    }

    public void showActions() {

    }

    @Override
    public void inputPressed(MCInputManager.Command cmd) {
        if (cmd instanceof MCInputManager.HudCommand hudCmd)
            choiceCarousel.processInput(hudCmd);
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

        layout.zone("characterHud").setOffset(0f, offsetY);

        if (currentCharacter != null) {
            characterSprite.setRegion(currentCharacter.getSprite());
            float charaSpriteSize = layout.zone("charaSprite").size();
            Vector2 spriteCenter = layout
                .zone("charaSprite")
                .center(charaSpriteSize, charaSpriteSize);
            characterSprite.setPosition(spriteCenter.x, spriteCenter.y);
            characterNameText.update(delta);

            characterHpText.setText("HP : " + currentCharacter.getHealth() + " / " + currentCharacter.getMaxHp());
            characterHpText.update(delta);
            
            choiceMessageText.update(delta);
            choiceCarousel.update(delta);
        }
    }

    /**
     * HUD :
     * 1/3 infos - 2/3 choix
     * Parties infos :
     * 1/3 sprite - 2/3 noms : HP
     */
    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);

        drawCornerlessRectangle(layout.zone("infoPanel"), HUD_RECT_BORDER);
        drawCornerlessRectangle(layout.zone("choicePanel"), HUD_RECT_BORDER);

        // partie droite : infos (pour l'instant que l'ID mdr)
        if (currentCharacter != null) {
            characterSprite.draw(currentBatch);
            characterNameText.render(currentBatch);
            characterHpText.render(currentBatch);
            choiceMessageText.render(currentBatch);
            choiceCarousel.render(currentBatch);
        }      
    }

    public void renderDebug() {
        layout.renderDebug();
    }

    public boolean isShown() {
        return (currentCharacter != null);
    }
}
