package com.walk.or.die.engine.ui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.compression.CRC;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.walk.or.die.engine.entities.MCAlly;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.entities.MCEntityManager;

public class MCHUDManager {
    private static MCHUDManager instance = null;

    public static MCHUDManager get() {
        if (instance == null)
            instance = new MCHUDManager();
        return instance;
    }

    public FitViewport hudViewport;
    private OrthographicCamera hudCamera;

    private MCCharacterHUD characterHUD;

    public void init(int width, int height) {
        hudCamera = new OrthographicCamera();
        //hudCamera = new OrthographicCamera(viewport.getWorldWidth(), viewport.getWorldHeight());
        //hudViewport.setCamera(hudCamera);
        hudViewport = new FitViewport(width, height, hudCamera);
        characterHUD = new MCCharacterHUD();

        // faut bien tester
        //List<MCAlly> test = new ArrayList<>(MCEntityManager.get().getAllies());
        //characterHUD.setHudTarget(test.get(0));
    }

    public FitViewport getViewport() {
        return hudViewport;
    }

    public OrthographicCamera getCamera() {
        return hudCamera;
    }

    public void setHudTarget(MCCharacter character) {
        characterHUD.setHudTarget(character);
    }

    public void update(float delta) {
        hudCamera.update();
        characterHUD.update(delta);
    }

    public void render(SpriteBatch batch) {
        hudViewport.apply();
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        characterHUD.render(batch);
        batch.end();
        characterHUD.renderDebug();
    }
}
