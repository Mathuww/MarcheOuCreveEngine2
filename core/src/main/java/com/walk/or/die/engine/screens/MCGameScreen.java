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
import com.walk.or.die.engine.shared.MCEventBus;
import com.walk.or.die.engine.ui.MCHUDManager;

/**
 * Represents our personal game screen.
 * @see Screen
 */
public class MCGameScreen implements Screen {
    /** The main game instance. */
    private MCGame game;
    /** The camera manager instance. */
    private MCCameraManager camManager = MCCameraManager.get();
    /** The HUD manager instance. */
    private MCHUDManager hudManager = MCHUDManager.get();

    /**
     * Constructs a new game screen.
     * @param game The game instance.
     * @throws InvalidDataException If the data is invalid.
     */
    public MCGameScreen(MCGame game) throws InvalidDataException {
        this.game = game;
    }
    
    /**
     * Called once when the window is opened.
     */
    @Override
    public void show() {
        hudManager.getMainMenuHud().setDisplay(false);
    }

    /**
     * Called on each frame.
     * @param delta The time in seconds since the last frame.
     */
    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
        game.gameViewport.apply();

        game.getTerrainMap().render(camManager.getGdxCam());
        game.batch.setProjectionMatrix(camManager.getGdxCam().combined);       
        game.batch.begin();

        game.getStateManager().render(game.batch);
        MCEntityManager.get().render(game.batch);

        game.batch.end();

        // 2 : HUD (le manager s'occupe de out)
        hudManager.render(game.batch);
    }

    /**
     * Resizes the screen's viewports.
     * @param width The new width.
     * @param height The new height.
     */
    @Override
    public void resize(int width, int height) {
       game.gameViewport.update(width, height, true);
       hudManager.getViewport().update(width, height, true);
    }

    /**
     * Pauses the game screen.
     */
    @Override
    public void pause() {
        MCEventBus.get().emit("Pause");
    }
    
    /**
     * Resumes the game screen after a pause.
     */
    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    /**
     * Hides the game screen.
     */
    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
    }

    /**
     * Disposes of the screen's assets.
     */
    @Override
    public void dispose() {
        // Destroy screen's assets here.
    }
}