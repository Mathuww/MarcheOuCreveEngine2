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
    private MCNextTurnHUD nextTurnHUD;

    public void init(int width, int height) {
        hudCamera = new OrthographicCamera();
        hudViewport = new FitViewport(width, height, hudCamera);
        characterHUD = new MCCharacterHUD();
        nextTurnHUD = new MCNextTurnHUD();
    }

    public FitViewport getViewport() {
        return hudViewport;
    }

    public OrthographicCamera getCamera() {
        return hudCamera;
    }

    public void hideCharaHud() {
        characterHUD.hide();
    }

    public void showCharaHud() {
        characterHUD.show();
    }

    public void setCharaHudTarget(MCCharacter character) {
        characterHUD.setCharacter(character);
    }

    public void refreshCharaHud(MCCharacter c) {
        characterHUD.refreshRequest(c);
    }

    public boolean isCharaHudShown() {
        return characterHUD.isFullyShown();
    }

    public void disableNextTurnHud() {
        nextTurnHUD.disable();
    }

    public void enableNextTurnHud() {
        nextTurnHUD.enable();
    }

    public void update(float delta) {
        hudCamera.update();
        characterHUD.update(delta);
        nextTurnHUD.update(delta);
    }

    public void render(SpriteBatch batch) {
        hudViewport.apply();
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        characterHUD.render(batch);
        nextTurnHUD.render(batch);
        batch.end();
        //characterHUD.renderDebug();
    }

    public boolean posBelongsToHud(Vector2 pos) {
        return characterHUD.posBelongsToHudComponent(pos) 
            || nextTurnHUD.posBelongsToHudComponent(pos);
    }

    public void handleClick(Vector2 pos) {
        if (characterHUD.posBelongsToHudComponent(pos))
            characterHUD.handleClick(pos);
        else if (nextTurnHUD.posBelongsToHudComponent(pos))
            nextTurnHUD.handleClick(pos);
    }
}
