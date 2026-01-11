package com.walk.or.die.engine.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.walk.or.die.engine.input.MCInputManager.Command;
import com.walk.or.die.engine.shared.MCSharedAssets;
import com.walk.or.die.engine.ui.MCUILayout.Zone;

public abstract class MCAbstractHUD {
    protected SpriteBatch currentBatch;
    protected final MCSharedAssets sharedAssets = MCSharedAssets.get();
    protected TextureRegion blackTexture;
    protected TextureRegion whiteTexture;
    protected TextureRegion greyTexture;

    public MCAbstractHUD() {
        try {
            blackTexture = sharedAssets.getSavedTexture("black");
            whiteTexture = sharedAssets.getSavedTexture("white");
            greyTexture = sharedAssets.getSavedTexture("grey");
        } catch (Exception e) {
            System.err.println("cant load character hud assets");
            e.printStackTrace();
        }
    }

    /**
     * Called on each frame
     * @param delta The time in seconds since the last frame.
     */
    public abstract void update(float delta);

    /**
     * Handles input when a command is pressed.
     * Jouer un petit son quand on interagit avec le hud
     * @param cmd The command that was pressed.
     */
    public void inputPressed(Command cmd) {
        // jouer un petit son quand on interagit avec le hud
    }

    /**
     * Called on each frame
     * @param batch The sprite batch to render with.
     */
    public void render(SpriteBatch batch) {
        currentBatch = batch;
    }
    
    /**
     * Draws a filled rectangle with the given texture.
     *
     * @param rect The rectangle to draw.
     * @param innerTexture The texture to fill the rectangle with.
     */
    public void drawFilledRectangle(Rectangle rect, TextureRegion innerTexture) {
        currentBatch.draw(innerTexture, rect.x, rect.y, rect.width, rect.height);
    }

    /**
     * Draws a corner.
     *
     * @param x The x-coordinate of the corner.
     * @param y The y-coordinate of the corner.
     * @param cornerSize The size of the corner.
     */
    protected void drawCorner(float x, float y, float cornerSize) {
        float tiersCorner = cornerSize / 3f;
        currentBatch.draw(blackTexture, x + tiersCorner, y, tiersCorner, tiersCorner);
        currentBatch.draw(blackTexture, x, y + tiersCorner, tiersCorner, tiersCorner);
        currentBatch.draw(blackTexture, x + tiersCorner * 2f, y + tiersCorner, tiersCorner, tiersCorner);
        currentBatch.draw(blackTexture, x + tiersCorner, y + tiersCorner * 2f, tiersCorner, tiersCorner);
    }    

    /**
     * Fills the four corners of a rectangle with white.
     *
     * @param rect The rectangle to fill the corners of.
     * @param borderSize The size of the white border.
     */
    public void fillWhiteFourCorners(Rectangle rect, float borderSize) {
        currentBatch.draw(whiteTexture, rect.x, rect.y, borderSize, borderSize);
        currentBatch.draw(whiteTexture, rect.x, rect.y + rect.height - borderSize, borderSize, borderSize);
        currentBatch.draw(whiteTexture, rect.x + rect.width - borderSize, rect.y, borderSize, borderSize);
        currentBatch.draw(whiteTexture, rect.x + rect.width - borderSize, rect.y + rect.height - borderSize, borderSize, borderSize);
    }

    /**
     * Draws the four corners of a rectangle.
     *
     * @param rect The rectangle to draw the corners of.
     * @param borderSize The size of the corners.
     */
    public void drawFourCorners(Rectangle rect, float borderSize) {
        drawCorner(rect.x, rect.y, borderSize);
        drawCorner(rect.x, rect.y + rect.height - borderSize, borderSize);
        drawCorner(rect.x + rect.width - borderSize, rect.y, borderSize);
        drawCorner(rect.x + rect.width - borderSize, rect.y + rect.height - borderSize, borderSize);
    }

    /**
     * Draws a cornerless rectangle using the default white texture.
     *
     * @param zone The zone to draw the rectangle in.
     * @param borderSize The size of the border.
     */
    public void drawCornerlessRectangle(Zone zone, float borderSize) {
        drawCornerlessRectangle(zone, borderSize, whiteTexture);
    }

    /**
     * Draws a cornerless rectangle.
     *
     * @param zone The zone to draw the rectangle in.
     * @param borderSize The size of the border.
     * @param innerTexture The texture to fill the rectangle with.
     */
    public void drawCornerlessRectangle(Zone zone, float borderSize, TextureRegion innerTexture) {
        Rectangle rect = zone.outside();
        drawFilledRectangle(rect, innerTexture);

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

        drawFourCorners(rect, borderSize);
    }

    /**
     * Checks if the HUD is fully shown.
     *
     * @return True if the HUD is fully shown, false otherwise.
     */
    public abstract boolean isFullyShown();

    /**
     * Checks if a position belongs to a HUD component.
     *
     * @param pos The position to check.
     * @return True if the position belongs to a HUD component, false otherwise.
     */
    public abstract boolean posBelongsToHudComponent(Vector2 pos);

    /**
     * Handles the hover event.
     *
     * @param pos The position of the hover.
     */
    public abstract void handleHover(Vector2 pos);

    /**
     * Handles when the hover is gone.
     */
    public abstract void handleHoverGone();

    /**
     * Handles a click event.
     *
     * @param pos The position of the click.
     */
    public abstract void handleClick(Vector2 pos);
}