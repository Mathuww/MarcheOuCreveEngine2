package com.walk.or.die.engine.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.entities.MCCharacter.HudCustomization;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.shared.MCEventBus;

public class MCCharacterHUD extends MCAbstractHUD {
    private final String FONT_FAMILY = "ariBlackAlpha";

    private final float HUD_HEIGHT = MCGame.WINDOW_DEFAULT_HEIGHT * 0.25f;
    private final float HUD_BOTTOM_MARGIN = MCGame.WINDOW_DEFAULT_HEIGHT * 0.05f;
    private final float HUD_PADDING_HEIGHT  = MCGame.WINDOW_DEFAULT_HEIGHT * 0.025f;
    private final float HUD_RECT_BORDER = MCGame.WINDOW_DEFAULT_HEIGHT * 0.0125f;
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

    private final float SCROLL_LERP = 16f;
    private final float SCROLL_Y = HUD_RECT_BORDER - HUD_PADDING_HEIGHT - HUD_HEIGHT;

    private BitmapFont font;
    private MCCharacter afterSwitchCharacter;
    private MCCharacter currentCharacter;

    private MCUILayout layout = new MCUILayout();

    private Sprite characterSprite = new Sprite();
    private MCUIScrollingText characterNameText;
    private MCHUDHPBar characterHpBar;
    private MCUISimpleText characterHpText;

    private MCUITypingText choiceMessageText;
    private MCUICarousel choiceCarousel;

    private float offsetY = SCROLL_Y;
    private float targetOffsetY = SCROLL_Y;
    private boolean scrolling = false;
    private boolean switching = false;
    private boolean shown = false;
    private boolean renderRightPanel = true;
    private boolean renderRPafterScroll = true;

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

        layout.splitY("charaHp", 0.5f, 5f, "hpBar", "hpText");

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
        characterHpBar = new MCHUDHPBar(this, layout.zone("hpBar"));
        characterHpText = new MCUISimpleText(
            this, 
            font, 
            layout.zone("hpText"), 
            Color.BLACK, 
            NAME_FONT_SCALE / 1.5f, 
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

    /**
     * Scrolls to a specific target offset.
     * @param targetOffsetY The target offset to scroll to.
     */
    private void scrollTo(float targetOffsetY) {
        if (this.targetOffsetY == targetOffsetY)
            return;
        scrolling = true;
        this.targetOffsetY = targetOffsetY;
    }

    /**
     * Hides the HUD.
     */
    public void hide() {
        if (currentCharacter != null)
            currentCharacter.onHudVisibilityLost();
        MCHUDManager.get().getFocusHud().setTarget(null);
        scrollTo(SCROLL_Y);
    }

    /**
     * Shows the HUD.
     */
    public void show() {
        HudCustomization customization = currentCharacter.getHudCustomization();
        if (!customization.canShow)
            return;
        MCHUDManager.get().getFocusHud().setTarget(currentCharacter);
        scrollTo(0f);
    }

    /**
     * Gets the display status of the right panel.
     * @return True if the right panel is displayed, false otherwise.
     */
    public boolean getRightPanelDisplay() {
        if (scrolling)
            return renderRPafterScroll;
        else
            return renderRightPanel;
    }

    /**
     * Sets the display status of the right panel.
     * @param display True to display the right panel, false otherwise.
     */
    public void setRightPanelDisplay(boolean display) {
        if (display && (shown || scrolling)) 
            renderRPafterScroll = display;
        else {
            renderRightPanel = display;
            renderRPafterScroll = display;
        }
    }

    /**
     * Gets the current character.
     * @return The current character.
     */
    public MCCharacter getCharacter() {
        return currentCharacter;
    }

    /**
     * Sets the current character.
     * @param newCharacter The new character to set.
     */
    public void setCharacter(MCCharacter newCharacter) {
        if (newCharacter == null || newCharacter.isDead()) {
            // System.out.println("simple close");
            hide();
            return;
        }

        if (newCharacter != null && newCharacter.equals(currentCharacter)) {
            show();
            return;
        }

        if (shown) {
            // System.out.println("beginning switch");
            switching = true;
            afterSwitchCharacter = newCharacter;
            hide();
        } else {
            // System.out.println("simple open");
            currentCharacter = newCharacter;
            repopulateHud(true);
            show();
        } 
    }

    /**
     * Requests a refresh of the HUD.
     * @param c The character to refresh the HUD for.
     * @param reloadCarousel True to reload the carousel, false otherwise.
     */
    public void refreshRequest(MCCharacter c, boolean reloadCarousel) {
        if (currentCharacter == null) 
            return;
        if (!currentCharacter.equals(c)) 
            return;
        if (switching) 
            return;
        HudCustomization customization = c.getHudCustomization();
        if (!customization.canShow)
            hide();
        else {
            // System.out.println("je vais réafficher");
            repopulateHud(reloadCarousel);
            if (!shown)
                show();
        }
    }

    /**
     * Repopulates the HUD with the current character's information.
     * @param reloadCarousel True to reload the carousel, false otherwise.
     */
    private void repopulateHud(boolean reloadCarousel) {
        if (currentCharacter == null)
            return;

        HudCustomization customization = currentCharacter.getHudCustomization();
        characterNameText.setText(currentCharacter.getDisplayName());
        characterHpBar.setTarget(currentCharacter);
        choiceMessageText.setText(customization.choiceMessage);
        choiceMessageText.startTyping();
        //choiceCarousel.clearActions();
        if (reloadCarousel) {
            choiceCarousel.loadItems(customization.carouselItems, customization.carouselFirstIndex);
            choiceCarousel.setEmptyText(customization.carouselEmptyMsg);
        }
    }

    /**
     * Sets the message to be displayed in the choice message text.
     * @param text The text to set.
     */
    public void setMessage(String text) {
        choiceMessageText.setText(text);
        choiceMessageText.startTyping();
    }
    
    /**
     * Handles input when a button is pressed.
     * @param cmd The command that was pressed.
     */
    @Override
    public void inputPressed(MCInputManager.Command cmd) {
        if (!isFullyShown())
            return;

        if (cmd instanceof MCInputManager.HudCommand hudCmd){
            // System.out.println("commande recue et je vais la process");
            switch (hudCmd.type) {
                case LEFT:
                    choiceCarousel.previous();
                    break;
                case RIGHT:
                    choiceCarousel.next();
                    break;
                case VALIDATE:
                    choiceCarousel.validate();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Called on each frame.
     * @param delta The time in seconds since the last frame.
     */
    @Override
    public void update(float delta) {
        if (scrolling) {
            boolean arrived = Math.abs(targetOffsetY - offsetY) <= 5f; // tolerance de fou je sais
            if (arrived) {
                // System.out.println("arrived");
                scrolling = false;
                offsetY = targetOffsetY; // pour isFullyShown

                if (switching) {
                    // System.out.println("arrived & switching");
                    currentCharacter = afterSwitchCharacter;
                    switching = false;
                    repopulateHud(true);
                    show();
                } else
                    shown = (offsetY == 0f);

                renderRightPanel = renderRPafterScroll;
            } else 
                offsetY += (targetOffsetY - offsetY) * delta * SCROLL_LERP;
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

            characterHpBar.update(delta);
            characterHpText.setText("HP : " + characterHpBar.getCurrentLerpedHp() + " / " + currentCharacter.getMaxHp());
            characterHpText.update(delta);
            
            choiceMessageText.update(delta);
            choiceCarousel.update(delta);
        }
    }

    /**
     * Called on each frame.
     * @param batch The sprite batch to render with.
     */
    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);

        drawCornerlessRectangle(layout.zone("infoPanel"), HUD_RECT_BORDER);
        if (renderRightPanel)
            drawCornerlessRectangle(layout.zone("choicePanel"), HUD_RECT_BORDER);

        // partie droite : infos (pour l'instant que l'ID mdr)
        if (currentCharacter != null) {
            characterSprite.draw(currentBatch);
            characterNameText.render(currentBatch);
            characterHpBar.render(currentBatch);
            characterHpText.render(currentBatch);
    
            if (renderRightPanel) {
                choiceMessageText.render(currentBatch);
                choiceCarousel.render(currentBatch);
            }
        }      
    }

    /**
     * Renders debug information.
     */
    public void renderDebug() {
        layout.renderDebug();
    }

    /**
     * Checks if the HUD is fully shown.
     * @return True if the HUD is fully shown, false otherwise.
     */
    public boolean isFullyShown() {
        return shown && !scrolling && (offsetY == targetOffsetY);
    }

    /**
     * Checks if a position belongs to a HUD component.
     * @param mousePos The position to check.
     * @return True if the position belongs to a HUD component, false otherwise.
     */
    public boolean posBelongsToHudComponent(Vector2 mousePos) {
        if (renderRightPanel)
            return layout.zone("characterHud").posBelongsToZone(mousePos);
        else
            return layout.zone("infoPanel").posBelongsToZone(mousePos);
    }

    /**
     * Handles hover events.
     * @param pos The position of the hover event.
     */
    public void handleHover(Vector2 pos) {
        if (renderRightPanel && choiceCarousel.posBelongsToHudComponent(pos) && isFullyShown())
            choiceCarousel.handleHover(pos);
        else 
            choiceCarousel.handleHoverGone();
    }

    /**
     * Handles hover gone events.
     */
    public void handleHoverGone() {
        choiceCarousel.handleHoverGone();
    }

    /**
     * Handles click events.
     * @param pos The position of the click event.
     */
    public void handleClick(Vector2 pos) {
        if (!isFullyShown())
            return;
        if (renderRightPanel && choiceCarousel.posBelongsToHudComponent(pos))
            choiceCarousel.handleClick(pos);
    }

    /**
     * Handles scroll events.
     * @param pos The position of the scroll event.
     * @param dy The amount of the scroll.
     */
    public void handleScroll(Vector2 pos, float dy) {
        if (!isFullyShown())
            return;
        if (renderRightPanel && choiceCarousel.posBelongsToHudComponent(pos))
            choiceCarousel.handleScroll(dy);
    }
}