package com.walk.or.die.engine.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * This class makes the link between several HUD elements. <br>
 * It also greatly helps to manage HUD input (see posBelongsToHud)
 */
public class MCHUDManager {
    /** Holds the singleton instance of the MCHUDManager. */
    private static MCHUDManager instance = null;

    /**
     * Gets the instance of the MCHUDManager.
     *
     * @return The instance.
     */
    public static MCHUDManager get() {
        if (instance == null)
            instance = new MCHUDManager();
        return instance;
    }

    /** Represents the viewport for the HUD elements. */
    public FitViewport hudViewport;
    /** Represents the camera for the HUD elements. */
    private OrthographicCamera hudCamera;

    /**
     * @see MCCharacterHUD
     */
    private MCCharacterHUD characterHUD;
    /**
     * @see MCSimpleActionHUD
     */
    private MCSimpleActionHUD simpleActionHUD;
    /**
     * @see MCTerrainFocusHUD
     */
    private MCTerrainFocusHUD focusHUD;
    /**
     * @see MCVeryBigInfoHUD
     */
    private MCVeryBigInfoHUD vbiHUD;
    /**
     * @see MCPauseHUD
     */
    private MCPauseHUD pauseHUD;
    /**
     * @see MCMainMenuHUD
     */
    private MCMainMenuHUD mmHUD;

    /**
     * Initializes the HUD manager.
     *
     * @param width The width of the viewport.
     * @param height The height of the viewport.
     */
    public void init(int width, int height) {
        hudCamera = new OrthographicCamera();
        hudViewport = new FitViewport(width, height, hudCamera);
        characterHUD = new MCCharacterHUD();
        simpleActionHUD = new MCSimpleActionHUD();
        focusHUD = new MCTerrainFocusHUD();
        vbiHUD = new MCVeryBigInfoHUD();
        pauseHUD = new MCPauseHUD();
        mmHUD = new MCMainMenuHUD();
    }

    /**
     * Gets the viewport.
     *
     * @return The hudViewport.
     */
    public FitViewport getViewport() {
        return hudViewport;
    }

    /**
     * Gets the camera.
     *
     * @return The hudCamera.
     */
    public OrthographicCamera getCamera() {
        return hudCamera;
    }

    /**
     * Gets the character HUD.
     *
     * @return The characterHUD.
     */
    public MCCharacterHUD getCharacterHud() {
        return characterHUD;
    }

    /**
     * Gets the simple HUD.
     *
     * @return The simpleActionHUD.
     */
    public MCSimpleActionHUD getSimpleHud() {
        return simpleActionHUD;
    }

    /**
     * Gets the focus HUD.
     *
     * @return The focusHUD.
     */
    public MCTerrainFocusHUD getFocusHud() {
        return focusHUD;
    }

    /**
     * Gets the very big info HUD.
     *
     * @return The very big info HUD.
     */
    public MCVeryBigInfoHUD getVbiHud() {
        return vbiHUD;
    }

    /**
     * Gets the pause HUD.
     *
     * @return The pause HUD.
     */
    public MCPauseHUD getPauseHud() {
        return pauseHUD;
    }

    /**
     * Gets the main menu HUD.
     *
     * @return The main menu HUD.
     */
    public MCMainMenuHUD getMainMenuHud() {
        return mmHUD;
    }

    /**
     * Called on each frame.
     *
     * @param delta The time in seconds since the last frame.
     */
    public void update(float delta) {
        hudCamera.update();
        characterHUD.update(delta);
        simpleActionHUD.update(delta);
        focusHUD.update(delta);
        pauseHUD.update(delta);
    }

    /**
     * Called on each frame.
     *
     * @param delta The time in seconds since the last frame.
     */
    public void updateMainMenu(float delta) {
        hudCamera.update();
        mmHUD.update(delta);
    }

    /**
     * Called on each frame.
     *
     * @param batch The sprite batch.
     */
    public void render(SpriteBatch batch) {
        hudViewport.apply();
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        characterHUD.render(batch);
        simpleActionHUD.render(batch);
        vbiHUD.render(batch);
        pauseHUD.render(batch);
        batch.end();
        //characterHUD.renderDebug();
    }

    /**
     * Called on each frame.
     *
     * @param batch The sprite batch used for rendering.
     */
    public void renderMainMenu(SpriteBatch batch) {
        hudViewport.apply();
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        mmHUD.render(batch);
        batch.end();
    }

    /**
     * Determines if any active HUD component can receive a command.
     *
     * @return True if any active HUD component is fully shown and can receive commands, false otherwise.
     */
    public boolean canReceiveHudCommand() {
        return characterHUD.isFullyShown()
            || pauseHUD.isFullyShown()
            || mmHUD.isFullyShown();
    }

    /**
     * Determines if the position belongs to the HUD. <br>
     * It would be difficult to implement the input dispatching like that without a single Manager class.
     *
     * @param pos The position to check.
     * @return True if the position belongs to the HUD, false otherwise.
     */
    public boolean posBelongsToHud(Vector2 pos) {
        return characterHUD.posBelongsToHudComponent(pos)
            || simpleActionHUD.posBelongsToHudComponent(pos)
            || pauseHUD.posBelongsToHudComponent(pos)
            || mmHUD.posBelongsToHudComponent(pos);
    }

    /**
     * Handles the hover event. <br>
     * Dispatches the same way as posBelongsToHud.
     * @see #posBelongsToHud(Vector2)
     *
     * @param pos The position of the hover event.
     */
    public void handleHover(Vector2 pos) {
        if (characterHUD.posBelongsToHudComponent(pos))
            characterHUD.handleHover(pos);
        else if (simpleActionHUD.posBelongsToHudComponent(pos))
            simpleActionHUD.handleHover(pos);
        else if (pauseHUD.posBelongsToHudComponent(pos))
            pauseHUD.handleHover(pos);
        else if (mmHUD.posBelongsToHudComponent(pos))
            mmHUD.handleHover(pos);
    }

    /**
     * Handles the hover gone event.
     */
    public void handleHoverGone() {
        characterHUD.handleHoverGone();
        simpleActionHUD.handleHoverGone();
        pauseHUD.handleHoverGone();
        mmHUD.handleHoverGone();
    }

    /**
     * Handles the click event.
     *
     * @param pos The position of the click event.
     */
    public void handleClick(Vector2 pos) {
        if (characterHUD.posBelongsToHudComponent(pos))
            characterHUD.handleClick(pos);
        else if (simpleActionHUD.posBelongsToHudComponent(pos))
            simpleActionHUD.handleClick(pos);
        else if (pauseHUD.posBelongsToHudComponent(pos))
            pauseHUD.handleClick(pos);
        else if (mmHUD.posBelongsToHudComponent(pos))
            mmHUD.handleClick(pos);
    }

    /**
     * Handles the scroll event.
     *
     * @param pos The position of the scroll event.
     * @param dy The scroll amount.
     */
    public void handleScroll(Vector2 pos, float dy) {
        characterHUD.handleScroll(pos, dy);
        pauseHUD.handleScroll(pos, dy);
        mmHUD.handleScroll(pos, dy);
    }
}