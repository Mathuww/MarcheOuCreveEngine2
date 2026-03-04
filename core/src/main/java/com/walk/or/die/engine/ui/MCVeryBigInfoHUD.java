package com.walk.or.die.engine.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.shared.MCEventBus;

/**
 * Displays two lines of text when a battle concludes.
 * Much of its functionality is similar to MCCharacterHUD, the most complex HUD.
 * @see MCCharacterHUD
 */
public class MCVeryBigInfoHUD extends MCAbstractHUD {
    /**
     * The font family for the HUD.
     */
    private final String FONT_FAMILY = "ariBlackAlpha";
    /**
     * The default font scale of the text.
     */
    private final float DEFAULT_FONT_SCALE = 0.35f;
    /**
     * The font spacing (between characters) of the text.
     */
    private final float FONT_SPACING = 3f;

    /**
     * The font used for displaying text.
     */
    private BitmapFont font;
    /**
     * Represents the entire screen area.
     */
    private Rectangle wholeScreen = new Rectangle(
        0, 0,
        MCGame.WINDOW_DEFAULT_WIDTH, 
        MCGame.WINDOW_DEFAULT_HEIGHT
    );
    /**
     * @see MCUILayout
     */
    private MCUILayout layout = new MCUILayout();

    /**
     * The text component for the lower line.
     */
    private MCUISimpleText lowerText;
    /**
     * The text component for the upper line.
     */
    private MCUISimpleText upperText;

    /**
     * The horizontal padding for the layout.
     */
    private final float PADDING_W = 100f;
    /**
     * The vertical padding for the layout.
     */
    private final float PADDING_H = 160f;
    /**
     * The gap between the upper and lower text zones.
     */
    private final float GAP = 0f;

    /**
     * The alpha value for the background.
     */
    private final float BG_ALPHA = 0.65f;
    
    /**
     * Is the HUD currently shown? Only true when it's ENTIRELY shown.
     */
    private boolean shown = false;
    /**
     * Indicates whether the HUD is currently hovered.
     */
    private boolean hovered = false;

    /**
     * The event bus for dispatching and receiving events.
     */
    private final MCEventBus bus = MCEventBus.get();

    /**
     * Constructs the very big information HUD.
     */
    public MCVeryBigInfoHUD() {
        try {
            font = sharedAssets.getSavedFont(FONT_FAMILY);
        } catch (Exception e) {
            System.err.println("cant load character hud assets");
            e.printStackTrace();
        }
        
        layout = new MCUILayout();
        layout.addZone(
            "screen",
            wholeScreen
        );
        layout.zone("screen").pad(PADDING_W, PADDING_H);

        layout.splitY("screen", 0.5f, GAP, "upper", "lower");
    
        lowerText = new MCUISimpleText(this, font, layout.zone("lower"), Color.BLACK, DEFAULT_FONT_SCALE, FONT_SPACING);
        upperText = new MCUISimpleText(this, font, layout.zone("upper"), Color.BLACK, DEFAULT_FONT_SCALE, FONT_SPACING);
    }

    /**
     * Sets the text for the lower line.
     * @param newText The new text to display.
     */
    public void setLowerText(String newText) {
        lowerText.setText(newText);
    }

    /**
     * Sets the scale for the lower text.
     * @param scale The scale factor for the text.
     */
    public void setLowerTextScale(float scale) {
        lowerText.setScale(scale);
    }

    /**
     * Sets the text for the upper line.
     * @param newText The new text to display.
     */
    public void setUpperText(String newText) {
        upperText.setText(newText);
    }

    /**
     * Sets the scale for the upper text.
     * @param scale The scale factor for the text.
     */
    public void setUpperTextScale(float scale) {
        upperText.setScale(scale);
    }

    /**
     * Called on each frame.
     * @param delta The time in seconds since the last frame.
     */
    @Override
    public void update(float delta) {
    }

    /**
     * Called on each frame.
     * @param batch The sprite batch to render with.
     */
    @Override
    public void render(SpriteBatch batch) {
        if (!shown)
            return;
        super.render(batch);
        batch.setColor(1f, 1f, 1f, BG_ALPHA);
        batch.draw(whiteTexture, wholeScreen.x, wholeScreen.y, wholeScreen.width, wholeScreen.height);
        batch.setColor(1f, 1f, 1f, 1f);
        lowerText.render(batch);
        upperText.render(batch);
    }

    /**
     * Sets the display status of the HUD.
     * @param display True to show the HUD, false to hide it.
     */
    public void setDisplay(boolean display) {
        this.shown = display;
    }

    /**
     * Checks whether the HUD is fully shown.
     * @return True if fully shown, false otherwise.
     */
    public boolean isFullyShown() {
        return shown;
    }

    /**
     * Checks whether a position belongs to a HUD component.
     * @param mousePos The mouse position.
     * @return True if the position belongs to a HUD component, false otherwise.
     */
    public boolean posBelongsToHudComponent(Vector2 mousePos) {
        return shown;
    }
    
    /**
     * Handles hover events for the HUD.
     * @param hoverPos The hover position.
     */
    public void handleHover(Vector2 hoverPos) {
        hovered = true;
    }

    /**
     * Handles events when the hover state ends.
     */
    public void handleHoverGone() {
        hovered = false;
    }

    /**
     * Handles click events for the HUD.
     * @param clickPos The click position.
     */
    public void handleClick(Vector2 clickPos) {
        if (!isFullyShown()) {
            return;
        }
    }
}