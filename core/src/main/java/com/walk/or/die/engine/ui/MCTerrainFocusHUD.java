package com.walk.or.die.engine.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.entities.MCCharacter;

public class MCTerrainFocusHUD extends MCAbstractHUD {
    private final float CORNER_SIZE = 0.2f; // tile (unit viewport monde pas viewport HUD)

    private MCCharacter target;

    private Rectangle rect = new Rectangle();

    private final float BLINKING_INTERVAL = 0.65f;
    private float blinkingTime = 0f;

    private boolean display = true;

    public MCTerrainFocusHUD() {}

    /**
     * Sets the target character.
     *
     * @param target The target character to set.
     */
    public void setTarget(MCCharacter target) {
        //if (target != null)
            //System.out.println("focus hud target is now " + target.getId());
        this.target = target;
    }

    /**
     * Called on each frame.
     * @param delta The time in seconds since the last frame.
     */
    @Override
    public void update(float delta) {
        if (target == null)
            return;

        blinkingTime += delta;

        if (blinkingTime >= BLINKING_INTERVAL) {
            display = !display;
            blinkingTime = 0f;
        }

        rect.x = target.getX() - CORNER_SIZE;
        rect.y = target.getY() - CORNER_SIZE;
        rect.width = target.getSize() + 2f * CORNER_SIZE;
        rect.height = target.getSize() + 2f * CORNER_SIZE;
    }

    /**
     * Called on each frame.
     * @param batch The sprite batch to render with.
     */
    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);

        if (target == null)
            return;

        if (!display)
            return;

        fillWhiteFourCorners(rect, CORNER_SIZE);
        drawFourCorners(rect, CORNER_SIZE);
    }

    /**
     * Gets whether the hud is fully shown.
     *
     * @return True if the hud is fully shown, false otherwise.
     */
    public boolean isFullyShown() { return display; }

    /**
     * Checks if the position belongs to the hud component.
     *
     * @param pos The position to check.
     * @return False, as the position never belongs to the hud component.
     */
    public boolean posBelongsToHudComponent(Vector2 pos) { return false; }

    /**
     * Handles the hover event.
     *
     * @param pos The position of the hover.
     */
    public void handleHover(Vector2 pos) {}

    /**
     * Handles the event when the hover is gone.
     */
    public void handleHoverGone() {}

    /**
     * Handles the click event.
     *
     * @param pos The position of the click.
     */
    public void handleClick(Vector2 pos) {}
}