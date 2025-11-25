package com.walk.or.die.engine.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
//import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.assets.AssetManager;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Queue;


import com.walk.or.die.engine.MCGame;
import com.walk.or.die.engine.MCEventBus;
import com.walk.or.die.engine.cameras.MCArrowsCamBehavior;
import com.walk.or.die.engine.cameras.MCCameraBehavior;
import com.walk.or.die.engine.cameras.MCCameraManager;
import com.walk.or.die.engine.cameras.MCCameraMode;
import com.walk.or.die.engine.cameras.MCFollowCamBehavior;
import com.walk.or.die.engine.entities.MCCharacter;
import com.walk.or.die.engine.entities.MCEntity;
import com.walk.or.die.engine.exceptions.DataException;
import com.walk.or.die.engine.exceptions.UnexistingBehaviorException;
import com.walk.or.die.engine.input.MCInputManager;
import com.walk.or.die.engine.input.MCInputManager.Command;
import com.walk.or.die.engine.sm.MCStateMachine;
import com.walk.or.die.engine.sm.game.MCGameState;
import com.walk.or.die.engine.sm.game.states.MCGSCombat;
import com.walk.or.die.engine.sm.game.states.MCGSExploration;
import com.walk.or.die.engine.tiledmap.MCGameMap;
import com.walk.or.die.engine.tiledmap.MCMap;

public class MCGameScreen implements Screen {
    private MCGame game;
    private AssetManager drh;
    
    // Map
    private MCGameMap map;

    // Camera
    private MCCameraManager camManager;

    // Input
    private MCInputManager inputHandler;
    
    // Entity
    private List<MCEntity> entities;
    private MCEntity focusedEntity;
    private float speed = 4f;
    
    // Movements
    private Deque<Vector2> movements;
    private boolean moving = false;
    private float percent = 0f;
    private Vector2 start = new Vector2(0,0);
    private Vector2 deplacement = new Vector2(0, 0);

    public MCGameScreen(MCGame game) throws DataException {
        this.game = game;
        drh = new AssetManager();
        entities = new ArrayList<>();

        game.getStateManager().setCurrentState("combat", new MCGSCombat.CombatStateArgs());

        camManager = MCCameraManager.get();
        camManager.init(8, 5, MCCameraMode.ARROWS);

        map = new MCGameMap("unoriginal_packed_maps/CArte.tmx", camManager.getGdxCam(), drh);
        try {
            TextureRegion playerTexture = map.getTileSet("player").getTileByType("player").getTextureRegion();
            entities.add(new MCCharacter(this, map, map.getEntitySpawnPos("player"), playerTexture));
            entities.add(new MCCharacter(this, map, new Vector2(3, 2), playerTexture));
        } catch (DataException e) {
            e.printStackTrace();
        }


        camManager.setLimitX(map.getWidth());
        camManager.setLimitY(map.getHeight());
        camManager.setFollowTarget(entities.get(0));
        game.viewport.setCamera(camManager.getGdxCam());

        movements = new ArrayDeque<>();
        inputHandler = new MCInputManager(game.viewport);
        Gdx.input.setInputProcessor(inputHandler);
        MCEventBus bus = MCEventBus.get();
        //bus.on(this, "ChangedFocus", this::changeFocus);
        bus.on(this, "InputPressed", this::inputPressed);
    }

    // Called once (when the window oppened)
    @Override
    public void show() {}

    // Called every frame
    @Override
    public void render(float delta) {
        logic(delta);
        draw(delta);
    }

    private void logic(float delta) {
        // We don't give a fuck about logic
        // We're going to do random things
        // player.danseSalsa()
        // Ptn la fonction marche pas...
    }

    private void draw(float delta) {
        ScreenUtils.clear(Color.BLACK);
        try {
            camManager.update(delta);
        } catch (UnexistingBehaviorException e) {
            e.printStackTrace();
        }

        game.getStateManager().update(delta);

        game.viewport.apply();
        map.render();
        game.batch.setProjectionMatrix(camManager.getGdxCam().combined);       
        game.batch.begin();

        for (MCEntity e : entities) {
            e.update(delta);
            e.render(game.batch);
        }

        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
       game.viewport.update(width, height, false);
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

    public MCEntity getEntityFromTile(int layer, Vector2 pos) {
        for (MCEntity e: entities) {
            if (e.getTilePosition().x == pos.x && e.getTilePosition().y == pos.y && e.getLayer() == layer) return e;
        }
        return null;
    }

    public MCEntity getFocusedEntity() {
        return focusedEntity;
    }

    public void changeFocus(MCEntity e) {
        if (focusedEntity != null) {
            System.out.println("Yey");
            if (focusedEntity.loseFocus()) {
                focusedEntity = e;
                if (e != null) {
                    e.getFocus();
                    System.out.println("Haha");
                }
            }
        } else {
            System.out.println("AAAAAAAAA");
            focusedEntity = e;
            if (e != null) e.getFocus();
        }
    }

    protected void inputPressed(MCInputManager.Command data) {
        if (data instanceof MCInputManager.ClickTileCommand tileCmd) {
            //System.out.println("Détecté par le game");
            MCEntity e = getEntityFromTile(1, tileCmd.getVector());
            changeFocus(e);
        }
    }

}