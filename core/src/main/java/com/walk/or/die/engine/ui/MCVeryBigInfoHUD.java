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
 * This is the HUD showing just two lines of text
 * when the battle is over. <br>
 * A lot of the stuff here is highly similar to MCCharacterHUD, the most complex one.
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


    private BitmapFont font;
    private Rectangle wholeScreen = new Rectangle(
        0, 0,
        MCGame.WINDOW_DEFAULT_WIDTH, 
        MCGame.WINDOW_DEFAULT_HEIGHT
    );
    /**
     * @see MCUILayout
     */
    private MCUILayout layout = new MCUILayout();

    private MCUISimpleText lowerText;
    private MCUISimpleText upperText;

    private final float PADDING_W = 100f;
    private final float PADDING_H = 160f;
    private final float GAP = 0f;

    private final float BG_ALPHA = 0.65f;
    
    /**
     * Is the HUD currently shown? Only true when it's ENTIRELY shown.
     */
    private boolean shown = false;
    private boolean hovered = false;

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

    public void setLowerText(String newText) {
        lowerText.setText(newText);
    }

    public void setLowerTextScale(float scale) {
        lowerText.setScale(scale);
    }

    public void setUpperText(String newText) {
        upperText.setText(newText);
    }

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

    public void setDisplay(boolean display) {
        this.shown = display;
    }

    /**
     * Checks if the HUD is fully shown.
     * @return True if fully shown, false otherwise.
     */
    public boolean isFullyShown() {
        return shown;
    }

    /**
     * Checks if a position belongs to a HUD component.
     * @param mousePos The mouse position.
     * @return True if the position belongs to a HUD component, false otherwise.
     */
    public boolean posBelongsToHudComponent(Vector2 mousePos) {
        return shown;
    }
    
    /**
     * Handles hover events.
     * @param hoverPos The hover position.
     */
    public void handleHover(Vector2 hoverPos) {
        hovered = true;
    }

    /**
     * Handles hover gone events.
     */
    public void handleHoverGone() {
        hovered = false;
    }

    /**
     * Handles click events.
     * @param clickPos The click position.
     */
    public void handleClick(Vector2 clickPos) {
        if (!isFullyShown()) {
            return;
        }
    }
}