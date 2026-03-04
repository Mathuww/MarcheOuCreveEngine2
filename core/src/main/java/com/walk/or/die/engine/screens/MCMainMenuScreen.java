package com.walk.or.die.engine.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.cameras.MCCameraManager;
import com.walk.or.die.engine.cameras.MCCameraManager.CameraMode;
import com.walk.or.die.engine.entities.MCEntityManager;
import com.walk.or.die.engine.exceptions.InvalidDataException;
import com.walk.or.die.engine.ui.MCHUDManager;
import com.walk.or.die.engine.ui.MCMainMenuHUD;

/**
 * Our main menu screen.
 * @see Screen
 */
public class MCMainMenuScreen implements Screen {
    /**
     * The game instance.
     */
    private MCGame game;
    /**
     * The HUD manager instance.
     */
    private MCHUDManager hudManager = MCHUDManager.get();

    /**
     * Constructs a new main menu screen.
     * @param game The game instance.
     * @throws InvalidDataException If the data is invalid.
     */
    public MCMainMenuScreen(MCGame game) {
        this.game = game;
    }
    
    /**
     * Called once when the window is opened.
     */
    @Override
    public void show() {
        hudManager.getMainMenuHud().setDisplay(true);
    }

    /**
     * Called on each frame.
     * @param delta The time in seconds since the last frame.
     */
    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.WHITE);
        hudManager.renderMainMenu(game.batch);
    }

    /**
     * Resizes the screen to the given dimensions.
     * @param width The new width.
     * @param height The new height.
     */
    @Override
    public void resize(int width, int height) {
        hudManager.getViewport().update(width, height, true);
    }

    /**
     * Invoked when the application is paused.
     */
    @Override
    public void pause() {
        // Invoked when your application is paused.
    }
    
    /**
     * Invoked when the application is resumed after a pause.
     */
    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    /**
     * Called when another screen replaces the current one.
     */
    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    /**
     * Destroys the screen's assets.
     */
    @Override
    public void dispose() {
        // Destroy screen's assets here.
    }
}