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
import com.walk.or.die.engine.exceptions.DataException;
import com.walk.or.die.engine.ui.MCHUDManager;

public class MCGameScreen implements Screen {
    private MCGame game;
    private MCCameraManager camManager = MCCameraManager.get();
    private MCHUDManager hudManager = MCHUDManager.get();

    public MCGameScreen(MCGame game) throws DataException {
        this.game = game;
    }
    
    // Called once (when the window oppened)
    @Override
    public void show() {
    }

    // Called every frame
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

        // 2 : HUD
        game.hudViewport.apply();
        game.batch.setProjectionMatrix(hudManager.getCamera().combined);
        game.batch.begin();
        hudManager.render(game.batch);
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
       game.gameViewport.update(width, height, true);
       MCHUDManager.get().hudViewport.update(width, height, true);
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