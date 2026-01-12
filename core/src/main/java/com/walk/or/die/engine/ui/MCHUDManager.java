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
    private static MCHUDManager instance = null;

    /**
     * Gets the instance of the MCHUDManager.
     *
     * @return the instance
     */
    public static MCHUDManager get() {
        if (instance == null)
            instance = new MCHUDManager();
        return instance;
    }

    public FitViewport hudViewport;
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
     * @param width  the width of the viewport
     * @param height the height of the viewport
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
     * @return the hudViewport
     */
    public FitViewport getViewport() {
        return hudViewport;
    }

    /**
     * Gets the camera.
     *
     * @return the hudCamera
     */
    public OrthographicCamera getCamera() {
        return hudCamera;
    }

    /**
     * Gets the character HUD.
     *
     * @return the characterHUD
     */
    public MCCharacterHUD getCharacterHud() {
        return characterHUD;
    }

    /**
     * Gets the simple HUD.
     *
     * @return the simpleActionHUD
     */
    public MCSimpleActionHUD getSimpleHud() {
        return simpleActionHUD;
    }

    /**
     * Gets the focus HUD.
     *
     * @return the focusHUD
     */
    public MCTerrainFocusHUD getFocusHud() {
        return focusHUD;
    }

    public MCVeryBigInfoHUD getVbiHud() {
        return vbiHUD;
    }

    public MCPauseHUD getPauseHud() {
        return pauseHUD;
    }

    public MCMainMenuHUD getMainMenuHud() {
        return mmHUD;
    }

    /**
     * Called on each frame
     */
    public void update(float delta) {
        hudCamera.update();
        characterHUD.update(delta);
        simpleActionHUD.update(delta);
        focusHUD.update(delta);
        pauseHUD.update(delta);
    }

    public void updateMainMenu(float delta) {
        hudCamera.update();
        mmHUD.update(delta);
    }

    /**
     * Called on each frame
     *
     * @param batch the sprite batch
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

    public void renderMainMenu(SpriteBatch batch) {
        hudViewport.apply();
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        mmHUD.render(batch);
        batch.end();
    }

    public boolean canReceiveHudCommand() {
        return characterHUD.isFullyShown()
            || pauseHUD.isFullyShown()
            || mmHUD.isFullyShown();
    }

    /**
     * Determines if the position belongs to the HUD. <br>
     * It would difficult to implement the input dispatching like that without a single Manager class.
     *
     * @param pos the position
     * @return true if the position belongs to the HUD, false otherwise
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
     * @see posBelongsToHud
     *
     * @param pos the position
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
     * @param pos the position
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
     * @param pos the position
     * @param dy  the scroll amount
     */
    public void handleScroll(Vector2 pos, float dy) {
        characterHUD.handleScroll(pos, dy);
        pauseHUD.handleScroll(pos, dy);
        mmHUD.handleScroll(pos, dy);
    }
}