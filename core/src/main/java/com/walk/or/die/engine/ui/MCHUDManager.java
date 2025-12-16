package com.walk.or.die.engine.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.walk.or.die.engine.entities.MCCharacter;

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
    private MCSimpleActionHUD simpleActionHUD;
    private MCTerrainFocusHUD focusHUD;

    public void init(int width, int height) {
        hudCamera = new OrthographicCamera();
        hudViewport = new FitViewport(width, height, hudCamera);
        characterHUD = new MCCharacterHUD();
        simpleActionHUD = new MCSimpleActionHUD();
        focusHUD = new MCTerrainFocusHUD();
    }

    public FitViewport getViewport() {
        return hudViewport;
    }

    public OrthographicCamera getCamera() {
        return hudCamera;
    }

    public MCCharacterHUD getCharacterHud() {
        return characterHUD;
    }

    public MCSimpleActionHUD getSimpleHud() {
        return simpleActionHUD;
    }

    public MCTerrainFocusHUD getFocusHud() {
        return focusHUD;
    }

    public void update(float delta) {
        hudCamera.update();
        characterHUD.update(delta);
        simpleActionHUD.update(delta);
        focusHUD.update(delta);
    }

    public void render(SpriteBatch batch) {
        hudViewport.apply();
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        characterHUD.render(batch);
        simpleActionHUD.render(batch);
        batch.end();
        //characterHUD.renderDebug();
    }

    public boolean posBelongsToHud(Vector2 pos) {
        return characterHUD.posBelongsToHudComponent(pos) 
            || simpleActionHUD.posBelongsToHudComponent(pos);
    }

    public void handleHover(Vector2 pos) {
        if (characterHUD.posBelongsToHudComponent(pos))
            characterHUD.handleHover(pos);
        else if (simpleActionHUD.posBelongsToHudComponent(pos))
            simpleActionHUD.handleHover(pos);
    }

    public void handleHoverGone() {
        characterHUD.handleHoverGone();
        simpleActionHUD.handleHoverGone();
    }

    public void handleClick(Vector2 pos) {
        if (characterHUD.posBelongsToHudComponent(pos))
            characterHUD.handleClick(pos);
        else if (simpleActionHUD.posBelongsToHudComponent(pos))
            simpleActionHUD.handleClick(pos);
    }

    public void handleScroll(Vector2 pos, float dy) {
        characterHUD.handleScroll(pos, dy);
    }
}
