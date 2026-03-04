package com.walk.or.die.engine.ui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.input.MCInputManager.Command;
import com.walk.or.die.engine.input.MCInputManager.HudCommand;
import com.walk.or.die.engine.shared.MCEventBus;
import com.walk.or.die.engine.ui.MCUICarousel.CarouselItem;

/**
 * This is the HUD shown within the main menu. <br>
 * It contains a sprite, a line of text, and a choice carousel (including copyrights). <br>
 * Much of the functionality here is highly similar to MCCharacterHUD, which is the most complex one.
 * @see MCCharacterHUD
 */
public class MCMainMenuHUD extends MCAbstractHUD {
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
     * The border width for HUD rectangles.
     */
    private final float HUD_RECT_BORDER = MCGame.WINDOW_DEFAULT_HEIGHT * 0.0125f;

    /**
     * The font used for rendering text in the HUD.
     */
    private BitmapFont font;
    /**
     * The rectangle representing the entire screen area.
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
     * The carousel component displayed in the lower part of the HUD.
     */
    private MCUICarousel lowerCarousel;
    /**
     * The simple text component displayed in the upper part of the HUD.
     */
    private MCUISimpleText upperText;

    /**
     * The horizontal padding for the HUD layout.
     */
    private final float PADDING_W = 100f;
    /**
     * The vertical padding for the HUD layout.
     */
    private final float PADDING_H = 160f;
    /**
     * The gap between layout zones.
     */
    private final float GAP = 0f;
    /**
     * The scale factor for larger text elements.
     */
    private final float BIG_SCALE = 0.65f;

    /**
     * The alpha value for the background.
     */
    private final float BG_ALPHA = 0.65f;
    
    /**
     * Is the HUD currently shown? Only true when it's ENTIRELY shown.
     */
    private boolean shown = false;
    /**
     * Indicates if the HUD is currently being hovered over.
     */
    private boolean hovered = false;

    /**
     * The event bus for inter-component communication.
     */
    private final MCEventBus bus = MCEventBus.get();

    /**
     * Constructs the main menu HUD.
     */
    public MCMainMenuHUD() {
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
        layout.zone("lower").pad(10f, 10f);
        lowerCarousel = new MCUICarousel(this, font, layout.zone("lower"));
        upperText = new MCUISimpleText(this, font, layout.zone("upper"), Color.BLACK, DEFAULT_FONT_SCALE, FONT_SPACING);
        upperText.setScale(BIG_SCALE);
        upperText.setText("MARCHE OU CRÈVE ENGINE 2");

        List<CarouselItem> items = new ArrayList<>();
        items.add(
            new CarouselItem(
                "PLAY", 
                () -> bus.emit("PlayFromMainMenu"), 
                null
            )
        );
        items.add(
            new CarouselItem(
                "EXIT TO DESKTOP",
                () -> bus.emit("Quit"),
                null
            )
        );
        lowerCarousel.loadItems(items, 0);
        lowerCarousel.setGradientAlpha(BG_ALPHA);

        bus.on(this, "InputPressed", this::inputPressed);
    }

    /**
     * Called on each frame.
     * @param delta The time in seconds since the last frame.
     */
    @Override
    public void update(float delta) {
        lowerCarousel.update(delta);
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
        drawCornerlessRectangle(layout.zone("lower"), HUD_RECT_BORDER);
        lowerCarousel.render(batch);
        upperText.render(batch);
    }

    /**
     * Sets the display state of the HUD.
     * @param display True to show the HUD, false to hide it.
     */
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
        return isFullyShown(); // vu que ca prend tout l'écran
    }
    
    /**
     * Handles hover events.
     * @param pos The position of the hover event.
     */
    public void handleHover(Vector2 pos) {
        if (lowerCarousel.posBelongsToHudComponent(pos) && isFullyShown())
            lowerCarousel.handleHover(pos);
        else 
            lowerCarousel.handleHoverGone();
    }

    /**
     * Handles hover gone events.
     */
    public void handleHoverGone() {
        lowerCarousel.handleHoverGone();
    }

    /**
     * Handles click events.
     * @param pos The position of the click event.
     */
    public void handleClick(Vector2 pos) {
        if (!isFullyShown())
            return;
        if (lowerCarousel.posBelongsToHudComponent(pos))
            lowerCarousel.handleClick(pos);
    }

    /**
     * Handles scroll events.
     * @param pos The position of the scroll event.
     * @param dy The amount of the scroll.
     */
    public void handleScroll(Vector2 pos, float dy) {
        if (!isFullyShown())
            return;
        if (lowerCarousel.posBelongsToHudComponent(pos))
            lowerCarousel.handleScroll(dy);
    }

    /**
     * Handles input pressed events.
     * @param cmd The command that was pressed.
     */
    public void inputPressed(Command cmd) {
        //System.out.print("mmhud is fully shown ? " + Boolean.toString(isFullyShown()));
        if (!isFullyShown())
            return;
        if (cmd instanceof HudCommand hudCmd) {
            switch (hudCmd.type) {
                case RIGHT:
                    lowerCarousel.next();
                    break;
                case LEFT:
                    lowerCarousel.previous();
                    break;
                case VALIDATE:
                    lowerCarousel.validate();
                    break;
                default:
                    break;
            }
        }
    }
}