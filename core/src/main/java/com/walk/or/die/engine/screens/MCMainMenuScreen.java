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
    private MCGame game;
    private MCHUDManager hudManager = MCHUDManager.get();

    /**
     * The constructor.
     * @param game The game instance.
     * @throws InvalidDataException If the data is invalid.
     */
    public MCMainMenuScreen(MCGame game) {
        this.game = game;
    }
    
    /**
     * Called once (when the window is opened).
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
     * @param width The new width.
     * @param height The new height.
     */
    @Override
    public void resize(int width, int height) {
        hudManager.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }
    
    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
    }
}