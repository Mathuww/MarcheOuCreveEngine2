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
 * This is the HUD shown at the upper right corner.
 * It only consists of the single button.
 * The only complexity resides in managing mouse hovering.
 * A lot of the stuff here is highly similar to MCCharacterHUD, except for the callback function.
 * @see MCCharacterHUD
 */
public class MCSimpleActionHUD extends MCAbstractHUD {
    /**
     * The font family for the HUD.
     */
    private final String FONT_FAMILY = "ariBlackAlpha";

    /**
     * The HUD height (in HUD viewport units).
     */
    private final float HUD_HEIGHT = MCGame.WINDOW_DEFAULT_HEIGHT * 0.095f;
    /**
     * The margin between the upper screen edge and the start of the HUD (in HUD viewport units).
     */
    private final float HUD_TOP_MARGIN = MCGame.WINDOW_DEFAULT_HEIGHT * 0.05f;
    /**
     * The width of the panels' borders.
     */
    private final float HUD_RECT_BORDER = MCGame.WINDOW_DEFAULT_HEIGHT * 0.0125f;
    /**
     * The padding between the start of the HUD and the true visual zone.
     */
    private final float HUD_PADDING_HEIGHT  = MCGame.WINDOW_DEFAULT_HEIGHT * 0.025f;
    /**
     * The horizontal padding.
     */
    private final float HUD_PADDING_WIDTH  = MCGame.WINDOW_DEFAULT_HEIGHT * 0.05f;

    /**
     * How much relative space the right panel takes.
     */
    private final float HUD_RIGHT_PANEL_RATIO = 0.25f;

    /**
     * Horizontal padding between right panel border and its elements.
     */
    private final float RIGHT_PANEL_PADDING_WIDTH = HUD_RECT_BORDER * 2f;
    /**
     * Vertical padding between right panel border and its elements.
     */
    private final float RIGHT_PANEL_PADDING_HEIGHT = HUD_RECT_BORDER * 2f;
    /**
     * The font scale of the text.
     */
    private final float FONT_SCALE = 0.35f;
    /**
     * The font spacing (between characters) of the text.
     */
    private final float FONT_SPACING = 3f;

    /**
     * Scrolling interpolation constant.
     */
    private final float SCROLL_LERP = 16f;
    /**
     * Target Y position when the HUD is "hidden".
     */
    private final float SCROLL_Y = HUD_TOP_MARGIN + HUD_HEIGHT;

    /**
     * The font used for displaying text.
     */
    private BitmapFont font;
    /**
     * @see MCUILayout
     */
    private MCUILayout layout = new MCUILayout();

    /**
     * @see MCUISimpleText
     */
    private MCUISimpleText text;

    /**
     * The current Y offset. Used for scrolling.
     */
    private float offsetY = SCROLL_Y;
    /**
     * The target Y offset.
     */
    private float targetOffsetY = SCROLL_Y;
    /**
     * Is the HUD currently scrolling?
     */
    private boolean scrolling = false;
    /**
     * Is the HUD currently shown? Only true when it's ENTIRELY shown.
     */
    private boolean shown = false;

    /**
     * Is this HUD currently hovered? Used to adjust rendering accordingly.
     */
    private boolean hovered = false;

    /**
     * The function to run when the button is clicked.
     */
    private Runnable callback;

    /**
     * The event bus for game events.
     */
    private final MCEventBus bus = MCEventBus.get();

    /**
     * Constructs the simple action HUD.
     */
    public MCSimpleActionHUD() {
        try {
            font = sharedAssets.getSavedFont(FONT_FAMILY);
        } catch (Exception e) {
            System.err.println("cant load character hud assets");
            e.printStackTrace();
        }
        
        layout.addZone(
            "nextTurnZone",
            new Rectangle(
                0f,
                MCGame.WINDOW_DEFAULT_HEIGHT - HUD_TOP_MARGIN - HUD_HEIGHT,
                MCGame.WINDOW_DEFAULT_WIDTH,
                HUD_HEIGHT
            )
        );
        layout.zone("nextTurnZone").pad(HUD_PADDING_WIDTH, 0f);

        layout.splitX("nextTurnZone", 1 - HUD_RIGHT_PANEL_RATIO, 0f, "uselessQuantumVoidSpace", "nextTurnPanel");
        layout.zone("nextTurnPanel").pad(RIGHT_PANEL_PADDING_WIDTH, RIGHT_PANEL_PADDING_HEIGHT);

        text = new MCUISimpleText(
            this,
            font,
            layout.zone("nextTurnPanel"),
            Color.BLACK,
            FONT_SCALE,
            FONT_SPACING
        );
        text.setText("END TURN");

        enable(); // pour tester
    }

    /**
     * Sets the text displayed on the button.
     * @param newText The new text to set.
     */
    public void setText(String newText) {
        text.setText(newText);
    }

    /**
     * Scrolls to a specific target offset.
     * @param targetOffsetY The target offset Y.
     */
    private void scrollTo(float targetOffsetY) {
        if (this.targetOffsetY == targetOffsetY) {
            return;
        }
        scrolling = true;
        this.targetOffsetY = targetOffsetY;
    }

    /**
     * Disables and hides this HUD instance.
     */
    public void disable() {
        scrollTo(SCROLL_Y);
        shown = false;
    }

    /**
     * Enables and shows this HUD instance.
     */
    public void enable() {
        scrollTo(0f);
        shown = true;
    }

    /**
     * Called on each frame.
     * @param delta The time in seconds since the last frame.
     */
    @Override
    public void update(float delta) {
        if (scrolling) {
            boolean arrived = Math.abs(targetOffsetY - offsetY) <= 0.05f; // tolerance de fou je sais
            if (arrived) {
                //System.out.println("arrived");
                scrolling = false;
                offsetY = targetOffsetY; // pour isFullyShown
            } else {
                offsetY += (targetOffsetY - offsetY) * delta * SCROLL_LERP;
            }
            offsetY = MathUtils.clamp(offsetY, 0f, SCROLL_Y);
        }

        layout.zone("nextTurnZone").setOffset(0f, offsetY);
    }

    /**
     * Called on each frame.
     * @param batch The sprite batch to render with.
     */
    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);

        TextureRegion innerPanelTexture = hovered ? greyTexture : whiteTexture;
        drawCornerlessRectangle(layout.zone("nextTurnPanel"), HUD_RECT_BORDER, innerPanelTexture);

        text.render(batch);
    }

    /**
     * Renders debug information.
     */
    public void renderDebug() {
        layout.renderDebug();
    }

    /**
     * Checks if the HUD is fully shown.
     * @return True if fully shown, false otherwise.
     */
    public boolean isFullyShown() {
        return shown && !scrolling && (offsetY == targetOffsetY);
    }

    /**
     * Checks if a position belongs to a HUD component.
     * @param mousePos The mouse position.
     * @return True if the position belongs to a HUD component, false otherwise.
     */
    public boolean posBelongsToHudComponent(Vector2 mousePos) {
        boolean belongs = layout.zone("nextTurnPanel").posBelongsToZone(mousePos);
        return belongs;
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
        if (!isFullyShown() || callback == null) {
            return;
        }
        callback.run();
    }

    /**
     * Sets the action to be executed on click.
     * @param callback The callback runnable.
     */
    public void setAction(Runnable callback) {
        this.callback = callback;
    }
}